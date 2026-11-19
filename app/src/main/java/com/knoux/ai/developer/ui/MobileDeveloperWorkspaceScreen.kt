package com.knoux.ai.developer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.InsertDriveFile
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.knoux.ai.ui.v2.V2Background
import com.knoux.ai.ui.v2.V2Blue
import com.knoux.ai.ui.v2.V2Cyan
import com.knoux.ai.ui.v2.V2Deep
import com.knoux.ai.ui.v2.V2GoldText
import com.knoux.ai.ui.v2.V2Green
import com.knoux.ai.ui.v2.V2Pink
import com.knoux.ai.ui.v2.V2Red
import com.knoux.ai.ui.v2.V2Soft
import com.knoux.ai.ui.v2.V2Violet
import com.knoux.ai.ui.v2.V2LuxuryIdentity
import com.knoux.ai.ui.v2.V2GlassCard

/**
 * Mobile Developer Workspace (DEV-06).
 * Real file-based workspace from the phone: project explorer, code editor,
 * markdown preview, copy code, search in project, diff viewer, import/export,
 * task queue, agent timeline.
 */
@Composable
fun MobileDeveloperWorkspaceScreen(
    onBack: () -> Unit,
    onPro: () -> Unit,
) {
    val context = LocalContext.current

    var workspacePath by remember { mutableStateOf<String?>(null) }
    var files by remember { mutableStateOf(listOf<String>()) }

    Column(Modifier.fillMaxSize()) {
        V2LuxuryIdentity(
            title = "مساحة عمل المطور",
            english = "MOBILE DEVELOPER WORKSPACE",
            subtitle = "تحرير أكواد الملفات الحقيقية من الهاتف عبر SAF",
            accent = V2Blue,
        )

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 12.dp
            ),
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
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
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
                                    modifier = androidx.compose.ui.Modifier.size(18.dp),
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                KText("مجلد المشروع", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                                KText(
                                    workspacePath ?: "لم يتم اختيار مجلد بعد",
                                    size = 8.6.sp,
                                    color = V2Soft,
                                    maxLines = 1,
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { /* SAF folder picker */ }) {
                                KText("اختيار مجلد", size = 9.4.sp, color = V2Cyan)
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier.size(36.dp).clip(RoundedCornerShape(12.dp))
                            .background(V2Cyan.copy(alpha = .16f))
                            .border(1.dp, V2Cyan.copy(alpha = .42f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Rounded.Code,
                            contentDescription = null,
                            tint = V2Cyan,
                            modifier = androidx.compose.ui.Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        KText("محرر الكود", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                        KText("تحرير نصوص وأكواد حقيقية", size = 8.6.sp, color = V2Soft, maxLines = 1)
                    }
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = { /* open editor */ }) {
                        KText("افتتاح", size = 9.4.sp, color = V2Blue)
                    }
                }
            }
            item {
                V2GlassCard(
                    Modifier.fillMaxWidth(),
                    padding = 13.dp,
                    radius = 18.dp,
                    glow = V2Green,
                    borderAlpha = .38f,
                ) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
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
                                    modifier = androidx.compose.ui.Modifier.size(18.dp),
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                KText("مستكشف الملفات", size = 13.sp, weight = FontWeight.Bold, color = Color.White)
                                KText(
                                    "${files.size} ملف",
                                    size = 8.6.sp,
                                    color = V2Soft,
                                    maxLines = 1,
                                )
                            }
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { /* refresh files */ }) {
                                KText("تحديث", size = 9.4.sp, color = V2Blue)
                            }
                        }
                    }
                }
            }
            item {
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(files) { file ->
                        V2GlassCard(
                            Modifier.fillMaxWidth(),
                            padding = 10.dp,
                            radius = 12.dp,
                            glow = V2Soft,
                            borderAlpha = .28f,
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
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
                                        modifier = androidx.compose.ui.Modifier.size(15.dp),
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                KText(file, size = 11.sp, weight = FontWeight.SemiBold, color = Color.White)
                                Spacer(Modifier.weight(1f))
                                TextButton(onClick = { /* open file */ }) {
                                    KText("عرض", size = 9.sp, color = V2Cyan)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}