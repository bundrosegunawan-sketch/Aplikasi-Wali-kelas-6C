package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WaliKelasViewModel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.TealSecondary

@Composable
fun DashboardScreen(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val school by viewModel.schoolProfile.collectAsState()
    val students by viewModel.students.collectAsState()
    val attendance by viewModel.dailyAttendance.collectAsState()
    val gradeMap by viewModel.gradeMap.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val lastSync by viewModel.lastSyncTimestamp.collectAsState()

    // Analytics calculations
    val totalStudents = students.size
    val hadirCount = attendance.count { it.status == "HADIR" || it.status == "TERLAMBAT" }
    val attendancePercent = if (totalStudents > 0) (hadirCount.toFloat() / totalStudents * 100).toInt() else 100

    val allStudentAverages = students.mapNotNull { st ->
        val grades = gradeMap[st.id]
        if (!grades.isNullOrEmpty()) grades.map { it.finalScore }.average() else null
    }
    val classAverageScore = if (allStudentAverages.isNotEmpty()) allStudentAverages.average() else 82.5

    // Students needing attention (remedial candidates - score < 75 in any subject)
    val remedialStudents = students.mapNotNull { st ->
        val grades = gradeMap[st.id] ?: emptyList()
        val lowSubjects = grades.filter { it.finalScore < 75 }
        if (lowSubjects.isNotEmpty()) {
            st to lowSubjects
        } else null
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Kemendikdasmen Banner
        item {
            HeroWelcomeCard(
                schoolName = school?.schoolName ?: "SD NEGERI 01 MERDEKA",
                teacherName = school?.teacherName ?: "Wali Kelas 6",
                className = school?.className ?: "Kelas VI",
                academicYear = school?.academicYear ?: "2025/2026",
                semester = school?.semester ?: "1 (Ganjil)",
                onQuickAbsen = { viewModel.activeTab.value = 2 },
                onQuickLeger = { viewModel.generateAndOpenClassLegerPdf(context) }
            )
        }

        // 2. Metric Overview Highlights (4 cards grid)
        item {
            Text(
                text = "Ringkasan Statistik Kelas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(
                    title = "Total Siswa",
                    value = "$totalStudents",
                    subtitle = "${students.count { it.gender == "L" }} L / ${students.count { it.gender == "P" }} P",
                    icon = Icons.Default.Group,
                    color = NavyLight,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Kehadiran Hari Ini",
                    value = "$attendancePercent%",
                    subtitle = "$hadirCount/$totalStudents Masuk",
                    icon = Icons.Default.CheckCircle,
                    color = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricCard(
                    title = "Rata-rata Nilai",
                    value = String.format("%.1f", classAverageScore),
                    subtitle = "Target KKM: 75.0",
                    icon = Icons.Default.Assessment,
                    color = GoldAccent,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Dapodik Pusat",
                    value = "Tersinkron",
                    subtitle = lastSync.take(10),
                    icon = Icons.Default.CloudDone,
                    color = TealSecondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Quick Action Hub
        item {
            Text(
                text = "Aksi Cepat Administrasi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    label = "Scan QR Presensi",
                    icon = Icons.Default.QrCodeScanner,
                    backgroundColor = Color(0xFFE3F2FD),
                    iconColor = NavyPrimary,
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.activeTab.value = 2
                }
                QuickActionButton(
                    label = "Input Nilai Harian",
                    icon = Icons.Default.Calculate,
                    backgroundColor = Color(0xFFFEF3C7),
                    iconColor = Color(0xFFD97706),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.activeTab.value = 3
                }
                QuickActionButton(
                    label = "Cetak Rapor PDF",
                    icon = Icons.Default.Print,
                    backgroundColor = Color(0xFFD1FAE5),
                    iconColor = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.activeTab.value = 4
                }
                QuickActionButton(
                    label = "Sinkron Dapodik",
                    icon = Icons.Default.CloudSync,
                    backgroundColor = Color(0xFFEDE9FE),
                    iconColor = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                ) {
                    viewModel.activeTab.value = 5
                }
            }
        }

        // 4. Subject Performance Bar Chart (Visual Analytics)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Analitik Performa Mata Pelajaran",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fase C - Kurikulum Merdeka",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { viewModel.activeTab.value = 3 }) {
                            Icon(Icons.Default.MenuBook, contentDescription = "Lihat Detail", tint = NavyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    subjects.forEach { subject ->
                        val subScores = students.mapNotNull { st ->
                            gradeMap[st.id]?.find { it.subject.id == subject.id }?.finalScore
                        }
                        val avg = if (subScores.isNotEmpty()) subScores.average() else 75.0
                        val progress = (avg / 100f).toFloat().coerceIn(0f, 1f)

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = subject.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${String.format("%.1f", avg)} / 100",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (avg >= 75) NavyPrimary else RedDanger
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (avg >= 85) GreenSuccess else if (avg >= 75) NavyLight else RedDanger,
                                trackColor = Color(0xFFE2E8F0)
                            )
                        }
                    }
                }
            }
        }

        // 5. Remedial Attention Alert Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (remedialStudents.isNotEmpty()) Color(0xFFFFFBEB) else Color(0xFFF0FDF4)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (remedialStudents.isNotEmpty()) Color(0xFFFDE68A) else Color(0xFFBBF7D0)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (remedialStudents.isNotEmpty()) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (remedialStudents.isNotEmpty()) Color(0xFFD97706) else GreenSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (remedialStudents.isNotEmpty()) "Perhatian Khusus / Pembimbingan Remedial (${remedialStudents.size} Siswa)" else "Semua Siswa Telah Mencapai Target KKTP!",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (remedialStudents.isNotEmpty()) Color(0xFF92400E) else Color(0xFF166534)
                        )
                    }

                    if (remedialStudents.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Siswa di bawah ini memerlukan bimbingan tambahan pada materi tertentu. Anda dapat mengirim notifikasi langsung ke orang tua via WhatsApp:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF78350F)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        remedialStudents.forEach { (student, lowSubs) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = student.fullName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Perlu remidi di: ${lowSubs.joinToString { "${it.subject.shortName} (${it.finalScore})" }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RedDanger
                                    )
                                }
                                Button(
                                    onClick = {
                                        viewModel.sendGradeWhatsApp(
                                            context = context,
                                            student = student,
                                            subjectName = lowSubs.first().subject.name,
                                            assessmentTitle = "Program Remedial Semester 1",
                                            score = lowSubs.first().finalScore,
                                            notes = "Ananda memerlukan bimbingan tambahan agar mencapai Kriteria Ketercapaian Tujuan Pembelajaran."
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("wa_remidi_${student.id}")
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "WA", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WA Ortu", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroWelcomeCard(
    schoolName: String,
    teacherName: String,
    className: String,
    academicYear: String,
    semester: String,
    onQuickAbsen: () -> Unit,
    onQuickLeger: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(NavyDark, NavyPrimary, Color(0xFF1E88E5))
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "KURIKULUM MERDEKA • FASE C",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = schoolName,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Wali Kelas: $teacherName",
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Surface(
                        color = GoldAccent,
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = NavyDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$className | Sem. $semester | TA $academicYear",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onQuickAbsen,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = NavyDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_absen_btn")
                    ) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Presensi Hari Ini", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onQuickLeger,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.White, Color.White))),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_leger_btn")
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cetak Leger", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    color = color.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun QuickActionButton(
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = backgroundColor,
        modifier = modifier.height(84.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = iconColor,
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontSize = 10.sp
            )
        }
    }
}
