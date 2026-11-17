# KNOUX AI Pro knowledge-only discovery. Read-only on source drives; no archive extraction, deletion, or Git actions.
$ErrorActionPreference = 'Continue'
$vault = 'K:\KNOUX-AI-Pro\PROJECT_KNOWLEDGE'
$out = Join-Path $vault '90_INDEX\discovery_candidates.local.csv'
$roots = @(
  'C:\Users\day night\Desktop',
  'C:\Users\day night\Downloads',
  'C:\Users\day night\Documents',
  'C:\Users\day night\AppData\Roaming\opencode',
  'C:\Users\day night\AppData\Local\opencode',
  'D:\',
  'K:\'
)
$skipDir = [regex]'^(?i:\.git|\.gradle|\.kotlin|node_modules|build|dist|out|target|\.next|\.venv|venv|__pycache__|Windows|Program Files(?: \(x86\))?|ProgramData|System Volume Information|\$RECYCLE\.BIN|Temp|tmp|npm-cache|\.cache|Cache|Caches|Android|gradle|AndroidStudio|\.idea|\.vscode|\.vs|\.terraform|\.rustup|\.cargo|\.nuget|sdk|generated|obj|bin)$'
$skipPath = [regex]'^(?i:K:\\KNOUX-AI-Pro\\|D:\\KNOUX-AI\\|C:\\Users\\day night\\Documents\\KNOUX_AI_AUTH_PRIVATE_|K:\\KX-Private-VPN\\|K:\\KX-Private-VPN-task07\\)'
$secret = [regex]'(?i:client_secret|service.role|service_role|private.key|\.p12$|\.pem$|\.jks$|\.keystore$|access.?token|refresh.?token|password|credential|secrets?|auth_private|\.env(?:\.|$)|wireless.adb|ssh.hostkey|ssh.private)'
$relevantName = [regex]'(?i:knoux.{0,5}(?:ai|pro|mobile|phone|android)|(?:andcode|opencode|codex).{0,24}(?:mobile|phone|android|knoux)|(?:mobile|phone|android).{0,24}(?:andcode|opencode|knoux))'
$proRegex = [regex]'(?i:knoux[\s_-]*ai[\s_-]*pro|knoux.ai.pro|KNOUX-AI-Pro)'
$ctxRegex = [regex]'(?i:knoux[\s_-]*ai|/root/workspace/KNOUX-AI|KNOUX AI Pro)'
$docsExt = @('.md','.markdown','.txt','.json','.csv','.jsonl','.pdf','.docx','.doc','.rtf','.html','.htm')
$imagesExt = @('.png','.jpg','.jpeg','.webp','.svg')
$found = New-Object 'System.Collections.Generic.List[object]'
$seen = New-Object 'System.Collections.Generic.HashSet[string]' ([StringComparer]::OrdinalIgnoreCase)
$stack = New-Object 'System.Collections.Generic.Stack[string]'
$roots | Where-Object { Test-Path -LiteralPath $_ -PathType Container } | ForEach-Object { $stack.Push($_) }
$dirs = 0; $files=0; $secretSkipped=0; $errors=0
while ($stack.Count -gt 0) {
  $dir = $stack.Pop()
  if ($skipPath.IsMatch($dir)) { continue }
  if (-not $seen.Add($dir)) { continue }
  $dirs++
  try { $items = [System.IO.Directory]::GetFileSystemEntries($dir) } catch { $errors++; continue }
  foreach ($f in $items) {
    try { $attrib = [System.IO.File]::GetAttributes($f) } catch { $errors++; continue }
    $name = [System.IO.Path]::GetFileName($f)
    if (($attrib -band [IO.FileAttributes]::Directory) -ne 0) {
      if (($attrib -band [IO.FileAttributes]::ReparsePoint) -ne 0) { continue }
      if ($skipDir.IsMatch($name) -or $skipPath.IsMatch($f)) { continue }
      $stack.Push($f); continue
    }
    $files++
    if ($secret.IsMatch($f)) { $secretSkipped++; continue }
    $ext = [System.IO.Path]::GetExtension($f).ToLowerInvariant()
    if (-not ($docsExt -contains $ext -or $imagesExt -contains $ext)) { continue }
    $reason = ''
    $tier = ''
    if ($proRegex.IsMatch($f)) { $tier='PRO_EXPLICIT'; $reason='filename_or_folder' }
    elseif ($relevantName.IsMatch($name)) { $tier='MOBILE_OR_KNOUX_AI'; $reason='filename' }
    elseif (($f -match '(?i:KNOUX-Phone-Exports|KNOUX_AI_UNIFIED_PROJECT_SOURCES|KNOUX-AI-NATIVE-UI|KNOUX-AI-Phone-Exports)') -and
        ($name -match '(?i:AGENT|HANDOFF|AUTH|MOBILE|MASTER|SOURCE|README|ONBOARDING|FOUNDATION|VERIFY|STATUS|VISUAL|E2E|FIGMA|PRO)')) {
      $tier='MOBILE_HISTORY'; $reason='known_source_group'
    }
    elseif (($docsExt -contains $ext) -and $ext -notin @('.pdf','.docx','.doc') -and $f -match '(?i:Downloads|Documents|Phone-Exports|KNOUX-AI-NATIVE-UI|KNOUX-Workspace)') {
      try {
        $fi = [IO.FileInfo]::new($f)
        if ($fi.Length -le 1400000) {
          $s=[IO.File]::ReadAllText($f)
          if ($proRegex.IsMatch($s)) {$tier='PRO_CONTENT';$reason='content'}
          elseif (($ctxRegex.IsMatch($s)) -and ($name -match '(?i:master|contract|source|handoff|pasted|conversation|android|mobile|opencode|agent|prompt|project|auth|readme|knowledge|guide)')) {
            $tier='RELATED_CONTENT';$reason='content'
          }
        }
      } catch { }
    }
    if ($tier) {
      try {
        $fi = [IO.FileInfo]::new($f)
        $found.Add([pscustomobject]@{Tier=$tier;Reason=$reason;Extension=$ext;Bytes=$fi.Length;Modified=$fi.LastWriteTime.ToString('yyyy-MM-dd HH:mm:ss');Source=$f})
      } catch {$errors++}
    }
  }
}
$found | Sort-Object Tier,Source | Export-Csv -NoTypeInformation -Encoding UTF8 -LiteralPath $out
Write-Output ('DONE dirs='+$dirs+' files='+$files+' candidates='+$found.Count+' secret_name_skips='+$secretSkipped+' io_errors='+$errors)
$found | Group-Object Tier | Sort-Object Name | ForEach-Object { Write-Output ($_.Name+'='+$_.Count) }
$found | Where-Object {$_.Tier -match 'PRO_'} | Select-Object -First 70 Tier,Bytes,Source | Format-Table -AutoSize | Out-String -Width 250
