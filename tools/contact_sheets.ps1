# Build review contact sheets from real device screenshots.
# Usage: powershell -ExecutionPolicy Bypass -File tools\contact_sheets.ps1
param(
    [string]$InDir = "qa-evidence/recovery_20261119/screens",
    [string]$OutDir = "qa-evidence/recovery_20261119/contact_sheets",
    [int]$PerSheet = 16
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root
Add-Type -AssemblyName System.Drawing

if (!(Test-Path $OutDir)) { New-Item -ItemType Directory -Path $OutDir -Force | Out-Null }

$files = Get-ChildItem $InDir -Filter *.png | Where-Object { $_.Length -gt 15000 } | Sort-Object Name
$cols = 4
$thumbW = 180
$thumbH = 400
$labelH = 18
$gap = 8
$sheetW = $cols * $thumbW + ($cols + 1) * $gap
$rows = [math]::Ceiling($PerSheet / $cols)
$sheetH = $rows * ($thumbH + $labelH) + ($rows + 1) * $gap

$sheetIndex = 0
for ($offset = 0; $offset -lt $files.Count; $offset += $PerSheet) {
    $sheetIndex++
    $batch = $files[$offset..([math]::Min($offset + $PerSheet - 1, $files.Count - 1))]
    $bitmap = New-Object System.Drawing.Bitmap($sheetW, $sheetH)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.Clear([System.Drawing.Color]::FromArgb(18, 12, 30))
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $font = New-Object System.Drawing.Font("Consolas", 7)

    for ($i = 0; $i -lt $batch.Count; $i++) {
        $col = $i % $cols
        $row = [math]::Floor($i / $cols)
        $x = $gap + $col * ($thumbW + $gap)
        $y = $gap + $row * ($thumbH + $labelH + $gap)
        $image = [System.Drawing.Image]::FromFile($batch[$i].FullName)
        $graphics.DrawImage($image, $x, $y, $thumbW, $thumbH)
        $graphics.DrawString(
            $batch[$i].BaseName,
            $font,
            [System.Drawing.Brushes]::Gainsboro,
            $x,
            ($y + $thumbH + 2)
        )
        $image.Dispose()
    }

    $target = Join-Path $OutDir ("sheet_{0:D2}.png" -f $sheetIndex)
    $bitmap.Save($target, [System.Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose()
    $bitmap.Dispose()
    Write-Host "wrote $target"
}

Write-Host "DONE: $sheetIndex contact sheets from $($files.Count) screenshots"
