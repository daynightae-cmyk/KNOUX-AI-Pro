with open('app/src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt', 'r', encoding='utf-8') as f:
    content = f.read()

start = content.index('fun V2PrivacyScreen')
box_start = content.index('Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.statusBars)) {', start)

brace_count = 0
i = box_start
while i < len(content):
    if content[i] == '{':
        brace_count += 1
    elif content[i] == '}':
        brace_count -= 1
        if brace_count == 0:
            break
    i += 1
func_end = i + 1

before = content[:box_start]
after = content[func_end:]

replacement = '''    // Royal stage: fixed header with live instrument + measured ribbon
    FixedServiceScreen(
        spec = ServiceSpecs.privacy,
        status = if (facts.computedAt > 0L) "PackageManager" else "جاري القراءة",
        statusColor = if (facts.computedAt > 0L) V2Green else V2Amber,
        metric = "/ إذن",
        scene = { sceneModifier -> ServiceArtScene(ServiceSpecs.privacy, sceneModifier) },
        ribbonSlots = listOf(
            HeroRibbonSlot(Icons.Rounded.Shield, declared.toString(), "إذن حساس", V2Pink),
            HeroRibbonSlot(Icons.Rounded.Apps, formatCount(facts.installedCount), "تطبيق مثبت", V2Cyan),
            HeroRibbonSlot(Icons.Rounded.Memory, formatBytes(facts.ownCacheBytes), "كاش KNOUX", V2Violet),
        ),
        action = {
            V2BrightGoldButton(
                "مراجعة الأذونات في الإعدادات",
                onClick = { openAllAppSettings(context) },
                icon = Icons.Rounded.Shield,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
            )
        },
        content = {
            // The reference puts the stag on the opposite shoulder for privacy.
            // Stag is rendered by FixedServiceScreen/ServiceHero; no duplicate header needed.
            KnouxRunOrbBlock(
                progress = when {
                    refreshing -> null
                    facts.computedAt > 0L -> 1f
                    else -> 0f
                },
                running = refreshing,
                enabled = !refreshing,
                label = when {
                    refreshing -> "جارٍ قراءة الصلاحيات..."
                    facts.computedAt > 0L -> "اضغط لإعادة قراءة الصلاحيات"
                    else -> "اضغط لتشغيل الخدمة"
                },
                accent = V2Violet,
                accent2 = V2Cyan,
                onTap = {
                    if (!refreshing) {
                        refreshing = true
                        bump()
                    }
                },
            )
            PrivacyHeroCard(
                score = score,
                sensitiveApps = facts.sensitiveAppCount,
                granted = granted,
                declared = declared,
            )
            V2SectionLabel("مجالات الخصوصية", trailing = "قراءة مباشرة")
            PrivacyDomainGrid(groups) { openAllAppSettings(context) }
            SensitiveAppsCard(sensitiveApps) { packageName -> openAppInfo(context, packageName) }
            V2SectionLabel("ملاحظة الخصوصية", trailing = "محلية")
            V2GlassCard(
                Modifier.fillMaxWidth(),
                padding = 12.dp,
                radius = 16.dp,
                glow = V2Violet,
                borderAlpha = .30f,
            ) {
                Column {
                    OutlinedTextField(
                        value = privacyNote,
                        onValueChange = { privacyNote = it },
                        label = {
                            KText(
                                "ملاحظة خصوصية تُحفظ محليًا فقط",
                                size = 9.sp,
                                color = V2Soft,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        V2GradientButton(
                            "حفظ الملاحظة",
                            {
                                if (!privacyNote.isBlank()) {
                                    scope.launch {
                                        val text = privacyNote.trim()
                                        savePrivacyNote(context, text)
                                        withContext(Dispatchers.IO) {
                                        repository.recordPermissionEvent(
                                            id = UUID.randomUUID().toString(),
                                            permission = "privacy_note",
                                            granted = true,
                                            eventAt = System.currentTimeMillis(),
                                            source = "user",
                                        )
                                        }
                                        noteSaved = true
                                        privacyNote = ""
                                        bump()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                        )
                        if (noteSaved) {
                            KText("حُفظ في السجل المحلي", size = 9.sp, color = V2Green)
                        }
                    }
                }
            }
            ServiceSubGrid(ServiceSpecs.privacy)
            PrivacyPreviewNote()
        },
    )
}'''

new_content = before + replacement + after

with open('app/src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt', 'w', encoding='utf-8') as f:
    f.write(new_content)

print('Done')
