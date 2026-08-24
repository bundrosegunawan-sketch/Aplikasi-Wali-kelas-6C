package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecord
import com.example.data.model.Student
import com.example.ui.WaliKelasViewModel
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OnGreenContainer
import com.example.ui.theme.OnNavyContainer
import com.example.ui.theme.OnRedContainer
import com.example.ui.theme.PurpleContainer
import com.example.ui.theme.PurpleInfo
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TealSecondary

@Composable
fun AttendanceScreen(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsState()
    val school by viewModel.schoolProfile.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailyAttendance by viewModel.dailyAttendance.collectAsState()

    var showScannerDialog by remember { mutableStateOf(false) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    // Metrics for selected date
    val hadirCount = dailyAttendance.count { it.status == "HADIR" }
    val sakitCount = dailyAttendance.count { it.status == "SAKIT" }
    val izinCount = dailyAttendance.count { it.status == "IZIN" }
    val alpaCount = dailyAttendance.count { it.status == "ALPA" }
    val terlambatCount = dailyAttendance.count { it.status == "TERLAMBAT" }
    val totalRecorded = dailyAttendance.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Date Navigation & Scanner Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                // Switch to previous day
                                val cur = selectedDate.takeLast(2).toIntOrNull() ?: 15
                                val newDay = (cur - 1).coerceAtLeast(1)
                                viewModel.selectedDate.value = "2025-10-${if (newDay < 10) "0$newDay" else "$newDay"}"
                            }
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Hari Sebelumnya", tint = NavyPrimary)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tanggal: $selectedDate",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "Presensi Harian Kelas VI-A",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val cur = selectedDate.takeLast(2).toIntOrNull() ?: 15
                                val newDay = (cur + 1).coerceAtMost(31)
                                viewModel.selectedDate.value = "2025-10-${if (newDay < 10) "0$newDay" else "$newDay"}"
                            }
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Hari Berikutnya", tint = NavyPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Scanner & Broadcast Action Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showScannerDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_qr_scanner_btn")
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan QR Siswa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showBroadcastDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("broadcast_wa_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kirim WA Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Summary Counters (Pills)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AttendanceCounterPill("Hadir", hadirCount, GreenContainer, OnGreenContainer, Modifier.weight(1f))
                AttendanceCounterPill("Sakit", sakitCount, AmberContainer, Color(0xFFB45309), Modifier.weight(1f))
                AttendanceCounterPill("Izin", izinCount, PurpleContainer, Color(0xFF6D28D9), Modifier.weight(1f))
                AttendanceCounterPill("Alpa", alpaCount, RedContainer, OnRedContainer, Modifier.weight(1f))
                AttendanceCounterPill("Telat", terlambatCount, Color(0xFFFEF08A), Color(0xFF854D0E), Modifier.weight(1f))
            }
        }

        // Student Attendance Rows
        items(students, key = { it.id }) { student ->
            val attendanceRecord = dailyAttendance.find { it.studentId == student.id }
            val currentStatus = attendanceRecord?.status ?: "HADIR"

            AttendanceStudentCard(
                student = student,
                record = attendanceRecord,
                currentStatus = currentStatus,
                onStatusChange = { newStatus ->
                    viewModel.setAttendanceStatus(student.id, newStatus, method = "MANUAL")
                },
                onSendWhatsApp = {
                    val record = attendanceRecord ?: AttendanceRecord(
                        id = 0,
                        studentId = student.id,
                        date = selectedDate,
                        status = currentStatus,
                        time = "07:00 WIB",
                        method = "MANUAL"
                    )
                    viewModel.sendAttendanceWhatsApp(context, student, record)
                }
            )
        }
    }

    // QR Code Live Scanner Module Dialog
    if (showScannerDialog) {
        QrScannerModuleDialog(
            students = students,
            onDismiss = { showScannerDialog = false },
            onScanCode = { code ->
                viewModel.handleQrScan(code) { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Broadcast Dialog Confirmation
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Kirim Laporan WA Kehadiran Massal?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Sistem akan memformat dan menyiapkan pesan laporan kehadiran harian ($selectedDate) untuk seluruh orang tua siswa (${students.size} kontak) sesuai standar kemendikdasmen."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBroadcastDialog = false
                        Toast.makeText(context, "Membuka antrean notifikasi WhatsApp untuk ${students.size} siswa...", Toast.LENGTH_LONG).show()
                        if (students.isNotEmpty()) {
                            val firstSt = students.first()
                            val rec = dailyAttendance.find { it.studentId == firstSt.id } ?: AttendanceRecord(
                                id = 0, studentId = firstSt.id, date = selectedDate, status = "HADIR"
                            )
                            viewModel.sendAttendanceWhatsApp(context, firstSt, rec)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess)
                ) {
                    Text("Kirim Sekarang")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBroadcastDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun AttendanceCounterPill(
    label: String,
    count: Int,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.height(52.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "$count", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textColor)
            Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = textColor.copy(alpha = 0.85f))
        }
    }
}

@Composable
fun AttendanceStudentCard(
    student: Student,
    record: AttendanceRecord?,
    currentStatus: String,
    onStatusChange: (String) -> Unit,
    onSendWhatsApp: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("attendance_row_${student.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (student.gender == "L") Color(0xFFE3F2FD) else Color(0xFFFCE7F3),
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = student.fullName.take(1),
                                fontWeight = FontWeight.Bold,
                                color = if (student.gender == "L") NavyPrimary else Color(0xFFDB2777),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = student.fullName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "QR: ${student.qrCodeId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                            if (record != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (record.method == "QR_SCAN") Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = if (record.method == "QR_SCAN") "📷 QR ${record.time}" else "✍️ Manual",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (record.method == "QR_SCAN") NavyPrimary else Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onSendWhatsApp,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Kirim WA Kehadiran",
                        tint = GreenSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5 Status Toggle Buttons (Hadir, Sakit, Izin, Alpa, Terlambat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                StatusButton("HADIR", "H", currentStatus == "HADIR", GreenSuccess, Modifier.weight(1f)) {
                    onStatusChange("HADIR")
                }
                StatusButton("SAKIT", "S", currentStatus == "SAKIT", AmberWarning, Modifier.weight(1f)) {
                    onStatusChange("SAKIT")
                }
                StatusButton("IZIN", "I", currentStatus == "IZIN", PurpleInfo, Modifier.weight(1f)) {
                    onStatusChange("IZIN")
                }
                StatusButton("ALPA", "A", currentStatus == "ALPA", RedDanger, Modifier.weight(1f)) {
                    onStatusChange("ALPA")
                }
                StatusButton("TERLAMBAT", "T", currentStatus == "TERLAMBAT", Color(0xFFCA8A04), Modifier.weight(1f)) {
                    onStatusChange("TERLAMBAT")
                }
            }
        }
    }
}

@Composable
fun StatusButton(
    statusKey: String,
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) activeColor else Color(0xFFF1F5F9),
        modifier = modifier.height(34.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.DarkGray
            )
        }
    }
}

@Composable
fun QrScannerModuleDialog(
    students: List<Student>,
    onDismiss: () -> Unit,
    onScanCode: (String) -> Unit
) {
    var manualQrInput by remember { mutableStateOf("") }
    var lastScannedMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = NavyPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pemindai QR Presensi Kelas", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Camera Scanner View Simulation with Laser Grid
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F172A)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .border(2.dp, GreenSuccess, RoundedCornerShape(12.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Arahkan Kamera ke Kartu Pelajar QR Siswa",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Fast Tap Simulation Buttons for any student
                Text(
                    text = "Simulasi Tap Cepat Kartu Siswa:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(students) { st ->
                        Surface(
                            onClick = {
                                onScanCode(st.qrCodeId)
                                lastScannedMsg = "Berhasil scan: ${st.fullName}"
                            },
                            color = NavyContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "📷 ${st.nickname}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = OnNavyContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Manual Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = manualQrInput,
                        onValueChange = { manualQrInput = it },
                        label = { Text("Input ID / Scan Text", fontSize = 12.sp) },
                        placeholder = { Text("e.g. STD-6A-001") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (manualQrInput.isNotBlank()) {
                                onScanCode(manualQrInput)
                                manualQrInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Catat")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Selesai")
            }
        }
    )
}
