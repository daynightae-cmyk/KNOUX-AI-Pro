with open('app/src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Fix the double closing brace at the end of V2PrivacyScreen
content = content.replace('        },\n    )\n}\n}\n\n@Composable\nprivate fun PrivacyTitle()', '        },\n    )\n}\n\n@Composable\nprivate fun PrivacyTitle()')

with open('app/src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt', 'w', encoding='utf-8') as f:
    f.write(content)

print('Fixed')
