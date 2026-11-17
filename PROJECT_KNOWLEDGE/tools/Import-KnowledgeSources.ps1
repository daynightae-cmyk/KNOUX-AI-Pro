# KNOUX AI Pro knowledge import. Source paths are READ-ONLY. Re-runnable; no archive, code change, delete, stage or push.
$ErrorActionPreference='Stop'
$repo='K:\KNOUX-AI-Pro'
$vault=Join-Path $repo 'PROJECT_KNOWLEDGE'
$downloads='C:\Users\day night\Downloads'
$allowed=@('.md','.txt','.json','.csv','.pdf','.docx','.png','.jpg','.jpeg','.webp','.svg')
$secretPath=[regex]'(?i:client_secret|auth_private|service_role|access.?token|refresh.?token|password|credential|secrets?|\.env|\.pem$|\.p12$|\.jks$|\.keystore$|ssh_)'
$secretValue=[regex]'(?i:-----BEGIN [^-]*PRIVATE KEY|(?:sb_secret_|ghp_|github_pat_|sk-)[A-Za-z0-9_\-]{12,}|(?:client_secret|service_role|access_token|refresh_token)\s*["'']?\s*[:=]\s*["'']?[A-Za-z0-9_\-\.]{16,}|Authorization:\s*Bearer\s+[A-Za-z0-9_\-\.]{18,})'
$manifest=New-Object System.Collections.ArrayList
$byHash=@{}
$prior=@{}
$priorPath=Join-Path $vault '90_INDEX\IMPORT_MANIFEST.local.csv'
if (Test-Path -LiteralPath $priorPath) {foreach($r in (Import-Csv -LiteralPath $priorPath)){if($r.Source -and $r.SHA256 -and $r.Stored){$prior[$r.Source+'|'+$r.SHA256]=$r}}}
$seen=@{}
$importCount=0
$verifiedBytes=[long]0
function Add-Knowledge([string]$src,[string]$dir,[string]$group){
 if (!(Test-Path -LiteralPath $src -PathType Leaf) -or $seen.ContainsKey($src)) {return}
 $seen[$src]=$true
 $fi=Get-Item -LiteralPath $src
 $ext=$fi.Extension.ToLowerInvariant()
 if ($allowed -notcontains $ext -or $fi.Length -gt 15000000 -or $secretPath.IsMatch($src)) {return}
 $originalHash=(Get-FileHash -LiteralPath $src -Algorithm SHA256).Hash.ToLowerInvariant()
 $oldKey=$src+'|'+$originalHash
 if ($prior.ContainsKey($oldKey)) {
   $prev=$prior[$oldKey]
   if (Test-Path -LiteralPath (Join-Path $repo $prev.Stored) -PathType Leaf) {
     $byHash[$originalHash]=$prev.Stored
     [void]$manifest.Add([pscustomobject]@{Group=$group;Status=$prev.Status;Source=$src;Stored=$prev.Stored;Bytes=$fi.Length;SHA256=$originalHash})
     return
   }
 }
 if ($byHash.ContainsKey($originalHash)) {
   [void]$manifest.Add([pscustomobject]@{Group=$group;Status='DEDUP';Source=$src;Stored=$byHash[$originalHash];Bytes=$fi.Length;SHA256=$originalHash})
   return
 }
 $text=$null; $redacted=$false
 if ($ext -in @('.md','.txt','.json','.csv','.svg')) {
   try {
     $text=[IO.File]::ReadAllText($src)
     if ($secretValue.IsMatch($text)) {
       [void]$manifest.Add([pscustomobject]@{Group=$group;Status='SKIP_SUSPECT_SECRET';Source='[redacted]';Stored='';Bytes=0;SHA256=''})
       return
     }
     $safe=[regex]::Replace($text,'\bR[A-Z0-9]{10,14}\b','[REDACTED_DEVICE_SERIAL]')
     $safe=[regex]::Replace($safe,'(?i)\b(?:192\.168|10\.0)\.\d{1,3}\.\d{1,3}(?::\d{2,5})?\b','[REDACTED_NETWORK_ENDPOINT]')
     $redacted=($safe -cne $text)
   } catch {
     [void]$manifest.Add([pscustomobject]@{Group=$group;Status='SKIP_UNREADABLE';Source=$src;Stored='';Bytes=$fi.Length;SHA256=$originalHash})
     return
   }
 }
 $destination=Join-Path (Join-Path $vault $dir) $fi.Name
 $folder=Split-Path -Parent $destination
 if (!(Test-Path -LiteralPath $folder)){$null=New-Item -ItemType Directory -Force -Path $folder}
 if (Test-Path -LiteralPath $destination) {
   $existing=(Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash.ToLowerInvariant()
   if ($existing -ne $originalHash -or $redacted) {
     $destination=Join-Path $folder ($fi.BaseName+'__'+$originalHash.Substring(0,8)+$fi.Extension)
   }
 }
 try {
   if (!(Test-Path -LiteralPath $destination)) {
     if ($redacted) {[IO.File]::WriteAllText($destination,$safe,[Text.UTF8Encoding]::new($true))}
     else {Copy-Item -LiteralPath $src -Destination $destination -ErrorAction Stop}
     $script:importCount++
     $script:verifiedBytes += (Get-Item -LiteralPath $destination).Length
   }
   $targetHash=(Get-FileHash -LiteralPath $destination -Algorithm SHA256).Hash.ToLowerInvariant()
   if (!$redacted -and $targetHash -ne $originalHash) {throw 'Target hash does not match source'}
   $rel=($destination.Substring($repo.Length+1) -replace '\\','/')
   $byHash[$originalHash]=$rel
   $status=if($redacted){'SANITIZED_COPY'}else{'COPY_SHA_VERIFIED'}
   [void]$manifest.Add([pscustomobject]@{Group=$group;Status=$status;Source=$src;Stored=$rel;Bytes=$fi.Length;SHA256=$originalHash})
 } catch {
   [void]$manifest.Add([pscustomobject]@{Group=$group;Status='FAILED';Source=$src;Stored='';Bytes=$fi.Length;SHA256=$originalHash})
 }
}
function Import-Group([string]$source,[string]$target,[string]$group,[bool]$recurse=$false,[string]$pattern='') {
 if (!(Test-Path -LiteralPath $source)) {return}
 $opts=@{LiteralPath=$source;File=$true;ErrorAction='SilentlyContinue'}
 if ($recurse){$opts.Recurse=$true}
 foreach($f in (Get-ChildItem @opts)) {
   if ($pattern -and $f.Name -notmatch $pattern){continue}
   $relDir=$target
   if ($recurse) {
     $parent=$f.DirectoryName.Substring($source.TrimEnd('\').Length).TrimStart('\')
     if ($parent) {$relDir=Join-Path $target $parent}
   }
   Add-Knowledge $f.FullName $relDir $group
 }
}
# Direct Pro-specific owner documents, prioritized but never mistaken for live state.
foreach($n in @('KNOUX_AI_PRO_WINDOWS_CANONICAL_MASTER_20261115.md','KNOUX_AI_PRO_NEW_CHAT_START_20261115.txt','KNOUX_AI_PRO_FIGMA_NATIVE_ANDROID_SINGLE_AGENT_MASTER_20261114.md')) {
 Add-Knowledge (Join-Path $downloads $n) '01_PRO_CANONICAL\OwnerDirectives' 'PRO_OWNER_HISTORY'
}
Import-Group 'K:\KNOUX-Phone-Exports\AGENT_HANDOFF_20261114' '01_PRO_CANONICAL\EarlierHandoffs' 'PRO_EARLIER_PHONE' $false '(?i:KNOUX_AI_PRO|ANDCODE)'
Import-Group 'K:\KNOUX-Phone-Exports' '02_OPENCODE_ANDCODE_HISTORY\PhoneHandoffs' 'PHONE_DOCS' $false '(?i:KNOUX_AI|ANDCODE|OPENCODE|MOBILE)'
Import-Group 'K:\KNOUX-Phone-Exports\RECOVERY_20261113' '02_OPENCODE_ANDCODE_HISTORY\RecoveryNotes' 'PHONE_RECOVERY' $false '(?i:KNOUX_AI|MOBILE_CANONICAL|MOBILE_RECOVERY|ANDCODE)'
Import-Group 'D:\KNOUX-AI-Phone-Exports\mobile_archive_audit' '02_OPENCODE_ANDCODE_HISTORY\ArchiveReviewNotes' 'PHONE_AUDIT_NOT_ARCHIVE' $false '(?i:readiness|inspect|deep)'
Import-Group (Join-Path $downloads 'KNOUX_AI_AUTH_TWO_AGENTS_VERIFIED_PUBLIC_CONFIG_20261114') '03_AUTH_PUBLIC\PublicAuthHandoff' 'PUBLIC_AUTH_ONLY' $false
foreach($n in @('01_KNOUX_AI_MOBILE_ECOSYSTEM_APPROVED.png','02_KNOUX_AI_MOBILE_FOUNDER_APPROVED.png')) {
 Add-Knowledge (Join-Path $downloads $n) '04_DESIGN_AND_IMAGES\ApprovedOnboardingHistory' 'APPROVED_HISTORY'
}
Import-Group 'K:\KNOUX-AI-NATIVE-UI-20261114\qa-evidence' '07_QA_AND_PROVENANCE\OnboardingSnapshots' 'HISTORICAL_SCREENSHOTS' $false '^(?i:screen_onboarding|screen_post_onboarding)'
Import-Group 'K:\KNOUX-Phone-Exports\M08-RUNTIME-20261113' '07_QA_AND_PROVENANCE\M08Historical' 'M08_HISTORY' $false '(?i:runtime.*png|M08_QA_EVIDENCE)'
# Historical standard project sources are explicitly segregated. Do not import its source code or apply its old directives.
$hist=Join-Path $downloads 'KNOUX_AI_UNIFIED_PROJECT_SOURCES_AND_AGENT_INSTRUCTIONS_20261113\KNOUX_AI_PROJECT_KNOWLEDGE'
Add-Knowledge (Join-Path $hist '00_START_HERE.md') '08_RELATED_HISTORY_NOT_CANONICAL\HistoricalStandardSources' 'STANDARD_HISTORY'
foreach($sub in @('01_AGENT_RULES','02_CANONICAL','03_ORIGINAL_SPECS','04_TASKS','06_EVIDENCE_AND_SOURCES')) {
 Import-Group (Join-Path $hist $sub) ('08_RELATED_HISTORY_NOT_CANONICAL\HistoricalStandardSources\'+$sub) 'STANDARD_HISTORY' $true
}
foreach($sub in @('docs\architecture','docs\design','docs\pro','docs\visual','docs\workflows')) {
 Import-Group (Join-Path 'K:\KNOUX-AI-NATIVE-UI-20261114' $sub) ('08_RELATED_HISTORY_NOT_CANONICAL\NativeUiDocs\'+$sub) 'OLDER_NATIVE_UI' $true
}
Import-Group 'D:\KNOUX-Intelligence-Knowledge-Pack' '08_RELATED_HISTORY_NOT_CANONICAL\SharedBrandDocs' 'COMPANY_CONTEXT' $false
# Additional exact Pro/dual-repo docs from recent Downloads. Do not pull unrelated Pasted text or session logs.
foreach($n in @('KNOUX_DUAL_REPO_UI_AUDIT_20261115.md','KNOUX_PC_UI_SAFE_MERGE_AND_VISUAL_EXECUTION_PROMPT_20261115.md')) {
 Add-Knowledge (Join-Path $downloads $n) '08_RELATED_HISTORY_NOT_CANONICAL\RecentCrossRepoAudit' 'CROSS_REPO_HISTORY'
}
# Build local-only provenance and a readable catalog.
$manifestPath=Join-Path $vault '90_INDEX\IMPORT_MANIFEST.local.csv'
$manifest | Export-Csv -LiteralPath $manifestPath -NoTypeInformation -Encoding UTF8
$lines=New-Object 'System.Collections.Generic.List[string]'
$lines.Add('# KNOUX AI Pro - Knowledge Catalog')
$lines.Add('')
$lines.Add('Generated on 2026-11-15 from read-only local sources. Files marked historical are contextual, not current source/runtime truth.')
$lines.Add('')
$lines.Add('The original sources were preserved. SHA256 of originals is recorded in the local-only manifest. No APK, TAR, ZIP, Gradle output, or sensitive OAuth credential was copied.')
$lines.Add('')
foreach($group in ($manifest | Where-Object {$_.Stored} | Group-Object Group | Sort-Object Name)) {
 $lines.Add('## '+$group.Name+' ('+$group.Count+')')
 $lines.Add('')
 foreach($row in ($group.Group | Sort-Object Stored)) {
   $rel=$row.Stored.Replace('PROJECT_KNOWLEDGE/','')
   $href=('../'+$rel).Replace(' ','%20')
   $lines.Add('- ['+[IO.Path]::GetFileName($rel)+']('+$href+') - '+$row.Status+' - source SHA '+$row.SHA256.Substring(0,12))
 }
 $lines.Add('')
}
$lines.Add('## Live repository references (no duplicate copying)')
$lines.Add('')
foreach($dir in @('design','docs','prompts','provenance','qa-evidence')) {$lines.Add('- '+$dir+' - see the existing repository folder at K:/KNOUX-AI-Pro/'+$dir)}
$lines.Add('')
$lines.Add('Additional filename/content hits from all three drives are indexed in 90_INDEX/discovery_candidates.local.csv for local use only; review uncertain candidates on demand.')
[IO.File]::WriteAllLines((Join-Path $vault '90_INDEX\KNOWLEDGE_CATALOG.md'),$lines,[Text.UTF8Encoding]::new($true))
Write-Output ('RESULT imported_new='+$importCount+' verified_written_bytes='+$verifiedBytes+' total_manifest='+$manifest.Count)
$manifest | Group-Object Status | Sort-Object Name | ForEach-Object {Write-Output ($_.Name+'='+$_.Count)}
