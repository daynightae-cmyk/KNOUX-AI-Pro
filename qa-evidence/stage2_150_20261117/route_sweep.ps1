$ErrorActionPreference = 'Continue'
$routes = Get-Content 'qa-evidence\stage2_150_20261117\sweep_routes.txt' | Where-Object { $_.Trim().Length -gt 0 }
$results = @()
$index = 0
foreach ($route in $routes) {
    $index++
    adb logcat -c 2>$null | Out-Null
    adb shell am force-stop com.knoux.ai | Out-Null
    adb shell am start -n com.knoux.ai/.MainActivity --es route $route | Out-Null
    Start-Sleep -Milliseconds 5000
    $fatal = adb logcat -d -s AndroidRuntime:E 2>$null
    $pidText = adb shell pidof com.knoux.ai
    $crashed = ($fatal -join "`n") -match 'FATAL EXCEPTION'
    $results += [pscustomobject]@{
        Index  = $index
        Route  = $route
        Pid    = ($pidText -join '').Trim()
        Crashed = $crashed
        Fatal  = if ($crashed) { ($fatal -join ' | ') } else { '' }
    }
    Write-Output ("{0}/{1} {2} pid={3} crash={4}" -f $index, $routes.Count, $route, (($pidText -join '').Trim()), $crashed)
}
$results | Export-Csv 'qa-evidence\stage2_150_20261117\sweep_results.csv' -NoTypeInformation -Encoding UTF8
$bad = @($results | Where-Object { $_.Crashed })
Write-Output ("DONE failures={0}" -f $bad.Count)
if ($bad.Count -gt 0) { $bad | Format-Table -AutoSize | Out-String | Write-Output }
