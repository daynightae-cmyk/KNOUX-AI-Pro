# KNOUX AI Pro - full route capture matrix.
#
# One cold start per route, 15 s settle (the reference handset needs 7-12 s for the
# first Compose frame). Each capture is written to a dated folder together with an
# index.csv row carrying byte size and SHA-256, so a later reviewer can prove which
# binary produced which frame.
#
# The handset sleeps between cold starts and `screencap` on a sleeping screen writes a
# pure black PNG, so the device is woken and the keyguard dismissed before every launch
# and again before every capture.
#
# TalkBack is enabled on the owner's phone; its focus ring would be mistaken for an app
# defect. The script parks the accessibility service for the run and restores the exact
# previous values in `finally`. It never writes the device identifier to disk or to the
# repository.
param(
    [string]$Serial = $env:KNOUX_TEST_DEVICE_SERIAL,
    [string]$Out = "D:\Temp\opencode\knoux-pro-routes-$(Get-Date -Format yyyyMMdd-HHmm)",
    [int]$SettleSeconds = 15,
    [string]$Apk = 'K:\KNOUX-AI-Pro\app\build\outputs\apk\debug\app-debug.apk',
    [string]$Routes = 'home,clean,tools,reports,pro,privacy,duplicates,deepscan,residue,appmanager,app-detail,installed-apps,storage,battery,ai,smartclean,settings,privacy-risk,duplicate-group,deep-scan-summary,performance-report,residue-item,storage-breakdown,battery-diagnostics,ai-recommendations'
)
$ErrorActionPreference = 'Stop'
$adb = 'D:\Android\sdk\platform-tools\adb.exe'
$pkg = 'com.knoux.ai.debug'
$activity = "$pkg/com.knoux.ai.MainActivity"

if (-not $Serial) {
    # Discover the one authorised device instead of storing its identifier in the
    # repository. Fails closed when zero or several devices are attached.
    $found = @(& $adb devices | Select-String -Pattern "`tdevice$" | ForEach-Object { ($_.Line -split "`t")[0] })
    if ($found.Count -ne 1) { throw "Set KNOUX_TEST_DEVICE_SERIAL or connect exactly one device (found $($found.Count))." }
    $Serial = $found[0]
}
if (-not (Test-Path $Apk)) { throw "APK not found: $Apk (build :app:assembleDebug first)." }
New-Item -ItemType Directory -Force -Path $Out | Out-Null

$install = & $adb -s $Serial install -r -t $Apk 2>&1 | Out-String
if ($LASTEXITCODE -ne 0) { throw "install failed (exit $LASTEXITCODE): $install" }
$apkHash = (Get-FileHash $Apk -Algorithm SHA256).Hash

$talkback = (& $adb -s $Serial shell settings get secure enabled_accessibility_services | Out-String).Trim()
$touch = (& $adb -s $Serial shell settings get secure touch_exploration_enabled | Out-String).Trim()
$rows = New-Object System.Collections.Generic.List[string]
$rows.Add('route,file,bytes,sha256')

try {
    & $adb -s $Serial shell settings put secure enabled_accessibility_services null | Out-Null
    & $adb -s $Serial shell settings put secure touch_exploration_enabled 0 | Out-Null
    Start-Sleep -Seconds 3

    foreach ($route in ($Routes -split ',')) {
        $file = "$route.png"
        $remote = "/sdcard/qa-pro-$route.png"
        & $adb -s $Serial shell am force-stop $pkg | Out-Null
        & $adb -s $Serial shell input keyevent KEYCODE_WAKEUP | Out-Null
        & $adb -s $Serial shell wm dismiss-keyguard | Out-Null
        & $adb -s $Serial shell am start -n $activity -e route $route | Out-Null
        Start-Sleep -Seconds $SettleSeconds
        & $adb -s $Serial shell input keyevent KEYCODE_WAKEUP | Out-Null
        & $adb -s $Serial shell screencap -p $remote | Out-Null
        & $adb -s $Serial pull $remote "$Out\$file" | Out-Null
        $item = Get-Item "$Out\$file"
        $hash = (Get-FileHash $item.FullName -Algorithm SHA256).Hash
        $rows.Add("$route,$file,$($item.Length),$hash")
        Write-Output ("{0,-22} {1,9} bytes  {2}" -f $route, $item.Length, $hash.Substring(0, 12))
    }
} finally {
    if ($talkback -and $talkback -ne 'null') {
        & $adb -s $Serial shell settings put secure enabled_accessibility_services $talkback | Out-Null
        & $adb -s $Serial shell settings put secure touch_exploration_enabled $touch | Out-Null
    }
    $rows | Set-Content -Path "$Out\index.csv" -Encoding utf8
    "APK_SHA256=$apkHash" | Set-Content -Path "$Out\apk.txt" -Encoding utf8
}
