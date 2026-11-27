import re

with open('app/src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# Find the Icons.Rounded imports section and add Apps and Memory
# Find the line with "import androidx.compose.material.icons.rounded."
# Add Apps and Memory after the existing imports
content = content.replace(
    'import androidx.compose.material.icons.rounded.Person',
    'import androidx.compose.material.icons.rounded.Person\nimport androidx.compose.material.icons.rounded.Apps\nimport androidx.compose.material.icons.rounded.Memory'
)

with open('app/src/main/java/com/knoux/ai/ui/v2/CleanDetailScreensV2.kt', 'w', encoding='utf-8') as f:
    f.write(content)

print('Done')
