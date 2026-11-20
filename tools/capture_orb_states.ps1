# Capture the universal K orb in idle and running states on the real device.
# Usage: powershell -ExecutionPolicy Bypass -File tools\capture_orb_states.ps1
param(
    [string]$Package = "com.knoux.ai.debug",
    [string]$Activity = "com.knoux.ai.debug/com.knoux.ai.MainActivity",
    [string]$OutDir = "qa-evidence/recovery_20261119/orb_states",
    [int]$WaitSeconds = 7
)

$ErrorActionPreference = "Continue"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root
if (!(Test-Path $OutDir)) { New-Item -ItemType Directory -Path $OutDir -Force | Out-Null }

adb wait-for-device | Out-Null
adb shell svc power stayon true | Out-Null
adb shell input keyevent KEYCODE_WAKEUP | Out-Null
adb shell wm dismiss-keyguard | Out-Null

# API 30 reads media through READ_EXTERNAL_STORAGE. Granting it up front keeps
# the capture deterministic: the tap then starts the scan instead of opening a
# system permission dialog mid-sequence.
adb shell pm grant $Package android.permission.READ_EXTERNAL_STORAGE 2>$null | Out-Null

function Save-Shot([string]$Name) {
    $remote = "/sdcard/knoux_orb_$Name.png"
    $local = Join-Path $OutDir "$Name.png"
    adb shell screencap -p $remote | Out-Null
    $pull = adb pull $remote $local 2>&1
    adb shell rm -f $remote | Out-Null
    if ($pull -match "1 file pulled") {
        Write-Host "  $Name ($((Get-Item $local).Length) bytes)"
    } else {
        Write-Host "  FAILED $Name :: $pull"
    }
}

function Get-OrbCenter([string]$Route) {
    $remote = "/sdcard/knoux_ui_$Route.xml"
    $local = Join-Path $OutDir "ui_$Route.xml"
    adb shell uiautomator dump $remote 2>$null | Out-Null
    adb pull $remote $local 2>$null | Out-Null
    adb shell rm -f $remote | Out-Null
    if (!(Test-Path $local)) { return $null }

    [xml]$doc = Get-Content $local -Encoding UTF8
    # The orb is the only large circular clickable node on a service screen.
    # Selecting it by geometry avoids depending on Arabic literals, which
    # Windows PowerShell 5.1 misreads in BOM-less UTF-8 scripts.
    $best = $null
    $bestArea = 0
    foreach ($node in $doc.SelectNodes("//node[@bounds]")) {
        if ($node.GetAttribute("clickable") -ne "true") { continue }
        $bounds = $node.GetAttribute("bounds")
        if ($bounds -match '\[(\d+),(\d+)\]\[(\d+),(\d+)\]') {
            $x1 = [int]$Matches[1]; $y1 = [int]$Matches[2]
            $x2 = [int]$Matches[3]; $y2 = [int]$Matches[4]
            $width = $x2 - $x1
            $height = $y2 - $y1
            $area = $width * $height
            if ($width -ge 180 -and $height -ge 180 -and $area -gt $bestArea) {
                $bestArea = $area
                $desc = $node.GetAttribute("content-desc")
                $best = @{ X = [int](($x1 + $x2) / 2); Y = [int](($y1 + $y2) / 2); Desc = $desc }
            }
        }
    }
    return $best
}

$routes = @("storage", "duplicates", "deepscan", "smartclean")
foreach ($route in $routes) {
    Write-Host "[$route]"
    adb shell am force-stop $Package | Out-Null
    Start-Sleep -Milliseconds 400
    adb shell am start -W -n $Activity --es route $route 2>&1 | Out-Null
    Start-Sleep -Seconds $WaitSeconds

    Save-Shot "orb_$route-idle"

    $orb = Get-OrbCenter $route
    if ($orb -eq $null) {
        Write-Host "  orb not found in accessibility tree; skipping tap"
        continue
    }
    Write-Host "  orb at $($orb.X),$($orb.Y) :: $($orb.Desc)"
    adb shell input tap $orb.X $orb.Y | Out-Null
    Start-Sleep -Milliseconds 900
    Save-Shot "orb_$route-running-1"
    Start-Sleep -Milliseconds 1600
    Save-Shot "orb_$route-running-2"
    Start-Sleep -Milliseconds 2600
    Save-Shot "orb_$route-running-3"
    Start-Sleep -Seconds 4
    Save-Shot "orb_$route-after"
}

Write-Host "DONE: orb states in $OutDir"
