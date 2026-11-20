package com.knoux.ai.developer.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knoux.ai.ui.v2.KText
import com.knoux.ai.ui.v2.V2Amber
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2GlassCard
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2TopBar
import com.knoux.ai.ui.v2.V2TopMode
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

private const val WORKSPACE_PREFS = "knoux_dev_workspace"
private const val WORKSPACE_URI_KEY = "tree_uri"
private const val MAX_FILES = 500
private const val MAX_DEPTH = 8
private const val MAX_EDIT_BYTES = 1_500_000

private data class WorkspaceEntry(
    val name: String,
    val relativePath: String,
    val uri: Uri,
    val sizeBytes: Long,
)

private data class EditorState(
    val entry: WorkspaceEntry,
    val text: String,
    val original: String,
    val saving: Boolean = false,
    val error: String? = null,
)

private fun loadWorkspaceUri(context: Context): Uri? {
    val prefs = context.getSharedPreferences(WORKSPACE_PREFS, Context.MODE_PRIVATE)
    val raw = prefs.getString(WORKSPACE_URI_KEY, null) ?: return null
    val uri = runCatching { Uri.parse(raw) }.getOrNull() ?: return null
    val persisted = context.contentResolver.persistedUriPermissions.any {
        it.uri == uri && it.isReadPermission && it.isWritePermission
    }
    if (!persisted) {
        prefs.edit().remove(WORKSPACE_URI_KEY).commit()
        return null
    }
    return uri
}

private fun persistWorkspaceUri(context: Context, uri: Uri) {
    context.contentResolver.takePersistableUriPermission(
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
    )
    context.getSharedPreferences(WORKSPACE_PREFS, Context.MODE_PRIVATE)
        .edit().putString(WORKSPACE_URI_KEY, uri.toString()).commit()
}

private fun clearWorkspaceUri(context: Context) {
    context.getSharedPreferences(WORKSPACE_PREFS, Context.MODE_PRIVATE)
        .edit().remove(WORKSPACE_URI_KEY).commit()
}

private fun isTextLike(name: String, mime: String): Boolean {
    if (mime.startsWith("text/")) return true
    if (mime in setOf(
            "application/json", "application/xml", "application/javascript",
            "application/x-javascript", "application/x-yaml", "application/yaml",
        )
    ) {
        return true
    }
    val extension = name.substringAfterLast('.', "").lowercase()
    return extension in setOf(
        "kt", "kts", "java", "xml", "json", "md", "txt", "yml", "yaml", "toml",
        "gradle", "properties", "js", "ts", "tsx", "jsx", "html", "css", "scss",
        "c", "h", "cpp", "hpp", "py", "rb", "rs", "go", "swift", "sql", "sh",
        "bat", "ps1", "gitignore", "pro", "cfg", "ini", "csv", "log",
    )
}

private fun listWorkspace(context: Context, treeUri: Uri): List<WorkspaceEntry> {
    val resolver = context.contentResolver
    val rootId = DocumentsContract.getTreeDocumentId(treeUri)
    val output = mutableListOf<WorkspaceEntry>()
    val skippedDirs = setOf("build", "node_modules", ".git", ".gradle", "intermediates", "generated")

    fun walk(parentId: String, prefix: String, depth: Int) {
        if (depth > MAX_DEPTH || output.size >= MAX_FILES) return
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
        )
        val cursor = resolver.query(childrenUri, projection, null, null, null) ?: return
        cursor.use { rows ->
            while (rows.moveToNext() && output.size < MAX_FILES) {
                val documentId = rows.getString(0) ?: continue
                val name = rows.getString(1) ?: continue
                val mime = rows.getString(2).orEmpty()
                val size = if (rows.isNull(3)) 0L else rows.getLong(3)
                val relative = if (prefix.isEmpty()) name else "$prefix/$name"
                if (mime == DocumentsContract.Document.MIME_TYPE_DIR) {
                    if (name.startsWith(".") || name in skippedDirs) continue
                    walk(documentId, relative, depth + 1)
                } else if (isTextLike(name, mime)) {
                    output += WorkspaceEntry(
                        name = name,
                        relativePath = relative,
                        uri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId),
                        sizeBytes = size,
                    )
                }
            }
        }
    }

    walk(rootId, "", 0)
    return output.sortedBy { it.relativePath.lowercase() }
}

private suspend fun readWorkspaceText(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
    val resolver = context.contentResolver
    val buffer = java.io.ByteArrayOutputStream()
    resolver.openInputStream(uri)?.use { stream ->
        val chunk = ByteArray(8192)
        var total = 0
        while (true) {
            val read = stream.read(chunk)
            if (read < 0) break
            total += read
            if (total > MAX_EDIT_BYTES) throw IOException("الملف أكبر من حد التحرير (1.5 MB)")
            buffer.write(chunk, 0, read)
        }
    } ?: throw IOException("تعذر فتح الملف")
    String(buffer.toByteArray(), Charsets.UTF_8)
}

private suspend fun writeWorkspaceText(context: Context, uri: Uri, text: String) = withContext(Dispatchers.IO) {
    val resolver = context.contentResolver
    resolver.openOutputStream(uri, "wt")?.use { stream ->
        stream.write(text.toByteArray(Charsets.UTF_8))
        stream.flush()
    } ?: throw IOException("المجلد للقراءة فقط أو انتهى التفويض")
}

/**
 * Mobile Developer Workspace (DEV-06).
 *
 * Real Storage Access Framework workspace: the user grants a folder, the app
 * persists the grant, enumerates text files, opens and saves them through the
 * provider, and reports revoked grants / read-only folders honestly. Android
 * gives no terminal or Git process here; those tools are marked unsupported in
 * the agent tool screen instead of being simulated.
 */
@Composable
fun MobileDeveloperWorkspaceScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var workspaceUri by remember { mutableStateOf<Uri?>(null) }
    var files by remember { mutableStateOf(listOf<WorkspaceEntry>()) }
    var query by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var editor by remember { mutableStateOf<EditorState?>(null) }

    fun refresh() {
        val uri = workspaceUri ?: run {
            files = emptyList()
            return
        }
        scope.launch {
            loading = true
            try {
                files = withContext(Dispatchers.IO) { listWorkspace(context, uri) }
                message = if (files.isEmpty()) {
                    "لم يُعثر على ملفات نصية داخل المجلد المصرح به."
                } else {
                    null
                }
            } catch (revoked: SecurityException) {
                clearWorkspaceUri(context)
                workspaceUri = null
                files = emptyList()
                message = "انتهى إذن المجلد. اختر المجلد مرة أخرى."
            } catch (failure: Exception) {
                message = failure.message ?: "تعذر قراءة المجلد"
            } finally {
                loading = false
            }
        }
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            try {
                persistWorkspaceUri(context, uri)
                workspaceUri = uri
                message = "تم ربط المجلد. الإذن محفوظ لعمليات إعادة التشغيل."
                refresh()
            } catch (failure: SecurityException) {
                message = "تعذر حفظ إذن المجلد: ${failure.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        workspaceUri = loadWorkspaceUri(context)
        refresh()
    }

    val filtered = if (query.isBlank()) {
        files
    } else {
        files.filter { it.relativePath.contains(query, ignoreCase = true) }
    }

    editor?.let { state ->
        AlertDialog(
            onDismissRequest = { if (!state.saving) editor = null },
            title = { KText(state.entry.relativePath, size = 13.sp, weight = FontWeight.Bold, color = Color.White) },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.text,
                        onValueChange = { updated ->
                            editor = state.copy(text = updated, error = null)
                        },
                        modifier = Modifier.fillMaxWidth().height(360.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp,
                            color = Color.White,
                        ),
                        label = { KText("UTF-8", size = 9.sp, color = V2Soft) },
                    )
                    state.error?.let { error ->
                        Spacer(Modifier.height(6.dp))
                        KText(error, size = 9.4.sp, color = V2Red, maxLines = 3)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !state.saving && state.text != state.original,
                    onClick = {
                        val current = editor ?: return@TextButton
                        editor = current.copy(saving = true, error = null)
                        scope.launch {
                            try {
                                writeWorkspaceText(context, current.entry.uri, current.text)
                                editor = current.copy(saving = false)
                                editor = null
                                message = "تم حفظ ${current.entry.relativePath}"
                                refresh()
                            } catch (failure: Exception) {
                                editor = current.copy(
                                    saving = false,
                                    error = failure.message ?: "فشل الحفظ",
                                )
                            }
                        }
                    },
                ) {
                    KText(if (state.saving) "جارٍ الحفظ…" else "حفظ", size = 10.sp, color = V2Green)
                }
            },
            dismissButton = {
                TextButton(onClick = { editor = null }, enabled = !state.saving) {
                    KText("إغلاق", size = 10.sp, color = V2Soft)
                }
            },
        )
    }

    Column(Modifier.fillMaxSize()) {
        V2TopBar(V2TopMode.INTERIOR, onBack = onBack, onPro = onPro)
        V2LuxuryIdentity(
            title = "مساحة عمل المطور",
            english = "MOBILE DEVELOPER WORKSPACE",
            subtitle = "تحرير ملفات حقيقية من الهاتف عبر Storage Access Framework",
            accent = V2Blue,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            item {
                V2GlassCard(
                    Modifier.fillMaxWidth(),
                    padding = 13.dp,
                    radius = 18.dp,
                    glow = V2Blue,
                    borderAlpha = .38f,
                ) {
                    Column {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                                    .background(V2Blue.copy(alpha = .16f))
                                    .border(1.dp, V2Blue.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Rounded.FolderOpen,
                                    contentDescription = null,
                                    tint = V2Blue,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                KText("مجلد المشروع", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                                KText(
                                    workspaceUri?.lastPathSegment ?: "لم يتم اختيار مجلد بعد",
                                    size = 9.sp,
                                    color = V2Soft,
                                    maxLines = 1,
                                )
                            }
                            TextButton(onClick = { picker.launch(null) }) {
                                KText(if (workspaceUri == null) "اختيار مجلد" else "تغيير", size = 9.4.sp, color = V2Cyan)
                            }
                        }
                        message?.let { text ->
                            Spacer(Modifier.height(6.dp))
                            KText(text, size = 9.2.sp, color = V2GoldText, maxLines = 3)
                        }
                    }
                }
            }

            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                            .background(V2Green.copy(alpha = .16f))
                            .border(1.dp, V2Green.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Rounded.Folder,
                            contentDescription = null,
                            tint = V2Green,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        KText("مستكشف الملفات", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                        KText(
                            when {
                                loading -> "جارٍ القراءة…"
                                workspaceUri == null -> "اختر مجلدًا أولًا"
                                else -> "${filtered.size} من ${files.size} ملفًا نصيًا"
                            },
                            size = 9.sp,
                            color = V2Soft,
                            maxLines = 1,
                        )
                    }
                    TextButton(onClick = { refresh() }, enabled = workspaceUri != null && !loading) {
                        KText("تحديث", size = 9.4.sp, color = V2Blue)
                    }
                }
            }

            if (workspaceUri != null) {
                item {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { KText("بحث في المسارات", size = 9.sp, color = V2Soft) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = Color.White),
                    )
                }
            }

            if (filtered.isEmpty() && !loading) {
                item {
                    V2GlassCard(
                        Modifier.fillMaxWidth(),
                        padding = 12.dp,
                        radius = 14.dp,
                        glow = V2Soft,
                        borderAlpha = .24f,
                    ) {
                        KText(
                            when {
                                workspaceUri == null -> "لم يُمنح أي مجلد بعد. اضغط «اختيار مجلد» لبدء العمل."
                                query.isNotBlank() -> "لا ملف يطابق «$query»."
                                else -> "لا ملفات نصية في هذا المجلد."
                            },
                            size = 9.6.sp,
                            color = V2Soft,
                            maxLines = 3,
                        )
                    }
                }
            }

            items(filtered, key = { it.relativePath }) { file ->
                V2GlassCard(
                    Modifier.fillMaxWidth().clickable {
                        scope.launch {
                            try {
                                val text = readWorkspaceText(context, file.uri)
                                editor = EditorState(entry = file, text = text, original = text)
                            } catch (failure: Exception) {
                                message = failure.message ?: "تعذر فتح الملف"
                            }
                        }
                    },
                    padding = 10.dp,
                    radius = 12.dp,
                    glow = V2Soft,
                    borderAlpha = .28f,
                ) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(30.dp).clip(RoundedCornerShape(8.dp))
                                .background(V2Pink.copy(alpha = .16f))
                                .border(1.dp, V2Pink.copy(alpha = .42f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Rounded.InsertDriveFile,
                                contentDescription = null,
                                tint = V2Pink,
                                modifier = Modifier.size(15.dp),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            KText(file.relativePath, size = 10.6.sp, weight = FontWeight.SemiBold, color = Color.White, maxLines = 1)
                            KText(
                                "${formatFileSize(file.sizeBytes)} · اضغط للتحرير",
                                size = 9.sp,
                                color = V2Soft,
                                maxLines = 1,
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = null,
                            tint = V2Cyan,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String = when {
    bytes <= 0 -> "حجم غير معروف"
    bytes < 1024 -> "$bytes بايت"
    bytes < 1024 * 1024 -> "${bytes / 1024} ك.ب"
    else -> "${bytes / (1024 * 1024)} م.ب"
}
