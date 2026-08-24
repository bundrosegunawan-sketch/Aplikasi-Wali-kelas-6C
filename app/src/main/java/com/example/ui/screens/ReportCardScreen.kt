package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OnNavyContainer
import com.example.ui.theme.RedDanger
import com.example.ui.theme.TealSecondary
import com.example.util.SubjectGradeSummary

@Composable
fun ReportCardScreen(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val school by viewModel.schoolProfile.collectAsState()
    val students by viewModel.students.collectAsState()
    val gradeMap by viewModel.gradeMap.collectAsState()
    val allAttendance by viewModel.allAttendance.collectAsState()

    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.id ?: 1L) }
    val currentStudent = students.find { it.id == selectedStudentId } ?: students.firstOrNull()
    val currentGrades = if (currentStudent != null) gradeMap[currentStudent.id] ?: emptyList() else emptyList()

    val averageScore = if (currentGrades.isNotEmpty()) currentGrades.map { it.finalScore }.average() else 80.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Laporan Hasil Belajar (Rapor Semester 1)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Format Resmi Kurikulum Merdeka Kemendikdasmen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Global Leger Download & Print Header Action
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buku Leger Nilai Kelas VI",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnNavyContainer
                        )
                        Text(
                            text = "Rekapitulasi lengkap seluruh siswa dan mapel",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnNavyContainer.copy(alpha = 0.8f)
                        )
                    }

                    Button(
                        onClick = { viewModel.generateAndOpenClassLegerPdf(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("print_leger_btn")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Cetak Leger", fontSize = 11.sp)
                    }
                }
            }
        }

        // Student Horizontal Selector
        item {
            Text(
                text = "Pilih Peserta Didik:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students) { st ->
                    val isSelected = st.id == selectedStudentId
                    Surface(
                        onClick = { selectedStudentId = st.id },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NavyPrimary else MaterialTheme.colorScheme.surface,
                        shadowElevation = if (isSelected) 3.dp else 1.dp,
                        modifier = Modifier.height(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Text(
                                text = st.nickname,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // PDF Generation Action Bar
        if (currentStudent != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.generateAndOpenStudentReportPdf(context, currentStudent) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("generate_pdf_report_btn")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buka PDF Rapor", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.generateAndShareStudentReportPdf(context, currentStudent) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_pdf_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bagikan PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.sendReportAnnouncementWhatsApp(context, currentStudent) },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wa_report_btn")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WA Ortu", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Interactive Rapor Document Sheet Preview
        if (currentStudent != null && school != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Kop Dokumen
                        Text(
                            text = "DINAS PENDIDIKAN ${school?.regency?.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = school?.schoolName?.uppercase() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "LAPORAN HASIL BELAJAR PESERTA DIDIK (RAPOR)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 2.dp, color = NavyPrimary)

                        // Identitas Siswa
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Nama: ${currentStudent.fullName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("NISN / NIS: ${currentStudent.nisn} / ${currentStudent.nis}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Kelas: ${school?.className}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Fase: ${school?.phase} | Sem: ${school?.semester}", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Nilai Akademik List
                        Text(
                            text = "Capaian Hasil Belajar Peserta Didik:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NavyDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        currentGrades.forEachIndexed { index, grade ->
                            Surface(
                                color = if (index % 2 == 1) Color(0xFFF8FAFC) else Color.White,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${index + 1}. ${grade.subject.name}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "Nilai Akhir: ${grade.finalScore}",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
                                            color = if (grade.finalScore >= 75) NavyPrimary else RedDanger
                                        )
                                    }
                                    Text(
                                        text = grade.reportDescription,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF475569),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Ekstrakurikuler & Absensi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Ekskul
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Ekstrakurikuler:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("• ${currentStudent.extracurricular}", fontSize = 10.sp)
                                    Text("Predikat: Sangat Baik", fontSize = 10.sp, color = GreenSuccess, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Absensi
                            var s = 0
                            var i = 0
                            var a = 0
                            allAttendance.filter { it.studentId == currentStudent.id }.forEach {
                                when (it.status) {
                                    "SAKIT" -> s++
                                    "IZIN" -> i++
                                    "ALPA" -> a++
                                }
                            }
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("Ketidakhadiran:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Text("Sakit: $s hari | Izin: $i hari", fontSize = 10.sp)
                                    Text("Tanpa Keterangan: $a hari", fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Catatan Wali Kelas
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Catatan Wali Kelas:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                Text(currentStudent.studentNotes, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tanda Tangan
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Orang Tua / Wali", fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(28.dp))
                                Text("( ${currentStudent.parentName} )", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Wali Kelas VI", fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(28.dp))
                                Text(school?.teacherName ?: "", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                Text("NIP. ${school?.teacherNip}", fontSize = 9.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
