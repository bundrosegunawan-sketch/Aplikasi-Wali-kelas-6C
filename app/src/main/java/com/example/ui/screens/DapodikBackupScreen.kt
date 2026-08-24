package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WaliKelasViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OnNavyContainer
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TealSecondary
import com.example.util.DapodikSyncHelper

@Composable
fun DapodikBackupScreen(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val school by viewModel.schoolProfile.collectAsState()
    val students by viewModel.students.collectAsState()
    val validation by viewModel.dapodikValidation.collectAsState()
    val isSyncing by viewModel.isSyncingDapodik.collectAsState()
    val syncLogs by viewModel.dapodikSyncLogs.collectAsState()
    val lastSync by viewModel.lastSyncTimestamp.collectAsState()
    val isBackingUp by viewModel.isBackingUpCloud.collectAsState()
    val lastBackup by viewModel.lastBackupTimestamp.collectAsState()

    var showJsonDialog by remember { mutableStateOf(false) }
    var generatedJson by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "Sinkronisasi Dapodik & Backup Cloud",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Integrasi Data Pokok Pendidikan Kemendikdasmen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 1. Dapodik Sync Module Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = NavyContainer,
                                shape = CircleShape,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = NavyPrimary)
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Server Dapodik Pusat",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Terakhir Sinkron: $lastSync",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Surface(
                            color = if (validation?.isValid == true) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (validation?.isValid == true) "SIAP SYNC" else "PERLU CEK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (validation?.isValid == true) GreenSuccess else Color(0xFFD97706),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Sinkronisasi otomatis mengirim rekapitulasi data profil rombel 6 SD, data 10 digit NISN peserta didik, nilai capaian pembelajaran semester 1, dan absensi ke server Kemendikdasmen.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.triggerDapodikSync(context) },
                        enabled = !isSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sync_dapodik_btn")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Menghubungkan ke Server Dapodik...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Mulai Sinkronisasi Sekarang", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Real-time Sync Console Terminal
                    if (syncLogs.isNotEmpty() || isSyncing) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = ">> Dapodik Sync Terminal v2026.a",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                syncLogs.forEach { log ->
                                    Text(
                                        text = "[${log.timestamp}] ${log.message}",
                                        color = if (log.isSuccess) Color(0xFF4ADE80) else Color(0xFFF87171),
                                        fontSize = 9.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Data Validation Checklist
        if (validation != null) {
            val v = validation!!
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = TealSecondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Validasi Standar Data Dapodik", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        ChecklistRow(
                            label = "Kelengkapan Profil Satuan Pendidikan & NPSN (8 Digit)",
                            isPassed = school?.npsn?.length == 8
                        )
                        ChecklistRow(
                            label = "Validitas NIP Wali Kelas & Kepala Sekolah",
                            isPassed = school?.teacherNip?.length == 18 && school?.headmasterNip?.length == 18
                        )
                        ChecklistRow(
                            label = "Validitas 10 Digit NISN Peserta Didik (${v.validNisnCount}/${v.totalStudents})",
                            isPassed = v.validNisnCount == v.totalStudents
                        )

                        if (v.warnings.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Catatan Verifikasi:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF92400E))
                                    v.warnings.take(3).forEach { w ->
                                        Text("• $w", fontSize = 9.5.sp, color = Color(0xFF78350F))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Automated Cloud Backup Module
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = CircleShape,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, tint = GreenSuccess)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Backup Cloud Otomatis",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Terakhir Backup: $lastBackup",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Pencadangan cloud menjaga seluruh data sekolah, nilai harian/sumatif, riwayat absensi, dan profil siswa tersimpan aman terenkripsi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.triggerCloudBackup(context) },
                            enabled = !isBackingUp,
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("cloud_backup_btn")
                        ) {
                            if (isBackingUp) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan Snapshot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                if (school != null) {
                                    val json = DapodikSyncHelper.generateDapodikJsonExport(
                                        school = school!!,
                                        students = students,
                                        formatives = emptyList(),
                                        summatives = emptyList(),
                                        attendance = emptyList()
                                    )
                                    generatedJson = json
                                    showJsonDialog = true
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("export_json_btn")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ekspor JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // JSON Dialog
    if (showJsonDialog) {
        AlertDialog(
            onDismissRequest = { showJsonDialog = false },
            title = { Text("Ekspor Data Dapodik JSON", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Format Kemendikdasmen Payload:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn {
                            item {
                                Text(
                                    text = generatedJson,
                                    color = Color(0xFF38BDF8),
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Dapodik JSON", generatedJson)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "JSON berhasil disalin ke Clipboard!", Toast.LENGTH_SHORT).show()
                        showJsonDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Salin JSON")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showJsonDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
fun ChecklistRow(label: String, isPassed: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isPassed) GreenSuccess else AmberWarning,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 11.5.sp,
            color = if (isPassed) Color(0xFF1E293B) else Color(0xFFB45309),
            fontWeight = if (isPassed) FontWeight.Normal else FontWeight.SemiBold
        )
    }
}
