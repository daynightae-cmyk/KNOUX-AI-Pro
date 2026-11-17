# KNOUX AI PRO - safe one-shot archive import from current user's Downloads
# Does not delete, overwrite, or touch existing app source.
$ErrorActionPreference = 'Stop'
$Name = 'KNOUX_AI_PRO_VISUAL_REFERENCES_DEDUP_20261115.zip'
$ExpectedSha = '10940bad1f0375ed33c68aed4e296b24cd81243360dd806b8bd280aa1df63670'
$Source = Join-Path (Join-Path $env:USERPROFILE 'Downloads') $Name
$Destination = Join-Path $PSScriptRoot $Name
if (-not (Test-Path -LiteralPath $Source -PathType Leaf)) {
    Write-Host "MISSING_DOWNLOAD: Download $Name from ChatGPT first to Downloads."
    exit 2
}
$Actual = (Get-FileHash -LiteralPath $Source -Algorithm SHA256).Hash.ToLowerInvariant()
if ($Actual -ne $ExpectedSha) {
    Write-Host "HASH_MISMATCH. Expected $ExpectedSha but got $Actual. No file copied."
    exit 3
}
if (Test-Path -LiteralPath $Destination) {
    $DestHash = (Get-FileHash -LiteralPath $Destination -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($DestHash -ne $ExpectedSha) { throw "A different archive exists at destination. Never overwrite automatically." }
    Write-Host 'ALREADY_PRESENT_AND_VERIFIED'
} else {
    Copy-Item -LiteralPath $Source -Destination $Destination -ErrorAction Stop
    $DestHash = (Get-FileHash -LiteralPath $Destination -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($DestHash -ne $ExpectedSha) { throw "Copy verification failed. Do not use destination archive." }
    Write-Host 'ARCHIVE_COPIED_AND_SHA256_VERIFIED'
}
Add-Type -AssemblyName System.IO.Compression.FileSystem
$Z = [System.IO.Compression.ZipFile]::OpenRead($Destination)
try {
    if ($Z.Entries.Count -lt 44) { throw "Archive entries incomplete." }
    Write-Host ("ARCHIVE_ENTRIES=" + $Z.Entries.Count)
} finally { $Z.Dispose() }
Write-Host "READY_FOR_CODEX: $Destination"
