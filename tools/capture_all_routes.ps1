# Capture real device screenshots for every KNOUX AI Pro route.
# Usage: powershell -ExecutionPolicy Bypass -File tools\capture_all_routes.ps1
param(
    [string]$Package = "com.knoux.ai.debug",
    [string]$Activity = "com.knoux.ai.debug/com.knoux.ai.MainActivity",
    [string]$OutDir = "qa-evidence/recovery_20261119/screens",
    [int]$WaitSeconds = 8
)

$ErrorActionPreference = "Continue"
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

if (!(Test-Path $OutDir)) { New-Item -ItemType Directory -Path $OutDir -Force | Out-Null }

# Keep the panel lit for the whole run; a sleeping device silently yields
# black screenshots that look like real captures.
adb wait-for-device | Out-Null
adb shell svc power stayon true | Out-Null
adb shell input keyevent KEYCODE_WAKEUP | Out-Null
adb shell wm dismiss-keyguard | Out-Null
Start-Sleep -Milliseconds 800

# --- Extract catalog routes from Kotlin sources -----------------------------
function Get-CatalogRoutes {
    $catalog = Get-Content "app\src\main\java\com\knoux\ai\ui\v2\ToolCatalogV2.kt" -Raw
    $routes = [regex]::Matches($catalog, '(?m)^\s{8}d\(\s*\r?\n\s*"[^"]+",\s*"([^"]+)"') | ForEach-Object { $_.Groups[1].Value }
    $expansion = Get-Content "app\src\main\java\com\knoux\ai\ui\v2\ToolCatalogExpansionV2.kt" -Raw
    $expRoutes = [regex]::Matches($expansion, '(?m)^\s{8}x\(\s*\r?\n\s*"[^"]+",\s*"([^"]+)"') | ForEach-Object { $_.Groups[1].Value }
    return @($routes) + @($expRoutes)
}

# Top-level shell screens first, then all catalog routes.
$topRoutes = @(
    "home", "clean", "tools", "reports", "pro", "settings",
    "duplicates", "residue", "privacy", "deepscan", "appmanager",
    "storage", "battery", "ai", "smartclean", "installed-apps"
)
$catalogRoutes = Get-CatalogRoutes
$allRoutes = @($topRoutes) + @($catalogRoutes) | Select-Object -Unique

Write-Host "Total routes to capture: $($allRoutes.Count)"

$index = 0
$ok = 0
$fail = 0
$rows = New-Object System.Collections.Generic.List[object]
$log = Join-Path $root "qa-evidence/recovery_20261119/capture_progress.log"
"KNOUX route capture - $(Get-Date -Format o)" | Set-Content $log -Encoding UTF8

function Invoke-Capture([string]$Route, [string]$LocalPath, [int]$Wait) {
    $remote = "/sdcard/knoux_qa_$([guid]::NewGuid().ToString('N')).png"
    adb shell am force-stop $Package | Out-Null
    Start-Sleep -Milliseconds 400

    $startOut = adb shell am start -W -n $Activity --es route "$Route" 2>&1
    if ($startOut -match "Error|Exception") {
        return @{ Ok = $false; Error = "START: $startOut" }
    }

    Start-Sleep -Seconds $Wait
    adb shell screencap -p $remote | Out-Null
    $pullOut = adb pull $remote $LocalPath 2>&1
    adb shell rm -f $remote | Out-Null
    if ($pullOut -notmatch "1 file pulled") {
        return @{ Ok = $false; Error = "PULL: $pullOut" }
    }
    $length = (Get-Item $LocalPath).Length
    if ($length -lt 15000) {
        return @{ Ok = $false; Error = "EMPTY: $length bytes" }
    }
    return @{ Ok = $true; Bytes = $length }
}

foreach ($route in $allRoutes) {
    $index++
    $safeName = ($route -replace '[^A-Za-z0-9_-]', '_')
    $local = Join-Path $OutDir "$safeName.png"

    # Resume support: skip routes already captured in a previous run.
    if (Test-Path $local) {
        Write-Host "[$index/$($allRoutes.Count)] $route (skipped, already captured)"
        continue
    }

    Write-Host "[$index/$($allRoutes.Count)] $route"

    $result = Invoke-Capture $route $local $WaitSeconds
    if (-not $result.Ok) {
        # A transient ADB hiccup or a slow cold start is retried once with a
        # longer settle window before the route is marked failed.
        Start-Sleep -Seconds 2
        $result = Invoke-Capture $route $local ($WaitSeconds + 4)
    }

    if ($result.Ok) {
        $ok++
        $hash = (Get-FileHash $local -Algorithm SHA256).Hash
        $rows.Add([pscustomobject]@{
            Route = $route
            File = "$safeName.png"
            Bytes = $result.Bytes
            Sha256 = $hash
        })
        $line = "OK   [$index/$($allRoutes.Count)] $route ($($result.Bytes) bytes) $hash"
        Write-Host "  saved $local ($($result.Bytes) bytes)"
    } else {
        $fail++
        $line = "FAIL [$index/$($allRoutes.Count)] $route :: $($result.Error)"
        Write-Host "  FAILED: $($result.Error)"
    }
    $line | Add-Content $log -Encoding UTF8
}

$manifest = Join-Path $root "qa-evidence/recovery_20261119/capture_manifest.csv"
$rows | Export-Csv -Path $manifest -NoTypeInformation -Encoding UTF8

# Routes that fell back to the same frame (splash, login, or home) share a
# hash. Report them so visual review starts with the real failures.
$dupes = $rows | Group-Object Sha256 | Where-Object { $_.Count -gt 1 }
Write-Host ""
if ($dupes) {
    Write-Host "IDENTICAL FRAMES (fallback suspects):"
    foreach ($group in $dupes) {
        Write-Host "  $($group.Count)x $($group.Name.Substring(0,12)) -> $($group.Group.Route -join ', ')"
    }
    "IDENTICAL FRAMES:" | Add-Content $log -Encoding UTF8
    foreach ($group in $dupes) {
        "  $($group.Count)x $($group.Name.Substring(0,12)) -> $($group.Group.Route -join ', ')" | Add-Content $log -Encoding UTF8
    }
} else {
    Write-Host "No identical screenshots - every route rendered its own frame."
}

Write-Host ""
Write-Host "DONE: $ok captured, $fail failed, total $($allRoutes.Count)"
Write-Host "Output: $OutDir"
Write-Host "Manifest: $manifest"
