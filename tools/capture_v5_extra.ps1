param(
    [string]$OutDir = 'D:\Temp\opencode'
)

$ErrorActionPreference = 'Stop'
$routes = @(
    'hub-developer','hub-googleservices','hub-security','hub-backup',
    'developer-info','google-services','advanced-security','backup-restore',
    'dev-build','gsc-apps','sec-lock','bkp-pick'
)

New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

foreach ($route in $routes) {
    $file = Join-Path $OutDir ("v5_" + $route.Replace('-','_') + ".png")
    adb shell input keyevent KEYCODE_WAKEUP | Out-Null
    Start-Sleep -Milliseconds 700
    adb shell wm dismiss-keyguard | Out-Null
    adb shell am force-stop com.knoux.ai.debug
    Start-Sleep -Milliseconds 500
    adb shell am start -n com.knoux.ai.debug/com.knoux.ai.MainActivity -e route $route | Out-Null
    Start-Sleep -Seconds 11
    cmd /c "adb exec-out screencap -p > `"$file`""
    $len = (Get-Item $file).Length
    Write-Host ("{0} -> {1} bytes" -f $route, $len)
}
