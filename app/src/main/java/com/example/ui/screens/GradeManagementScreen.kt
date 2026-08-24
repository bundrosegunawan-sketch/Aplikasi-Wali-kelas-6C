package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.data.model.Subject
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
fun GradeManagementScreen(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val subjects by viewModel.subjects.collectAsState()
    val students by viewModel.students.collectAsState()
    val gradeMap by viewModel.gradeMap.collectAsState()

    var selectedSubjectId by remember { mutableStateOf("PAIBP") }
    var selectedGradeMode by remember { mutableStateOf(0) } // 0: Formatif (Harian), 1: Sumatif (Ulangan/STS/SAS)

    val currentSubject = subjects.find { it.id == selectedSubjectId } ?: subjects.firstOrNull()

    // Editing Score Dialog State
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var editingAssessmentType by remember { mutableStateOf("") } // "TP 6.1", "SUMATIF_1", etc.
    var currentScoreVal by remember { mutableStateOf(80) }

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
                    text = "Pengolahan Nilai Kurikulum Merdeka",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Asesmen Formatif & Sumatif Fase C Kelas 6 SD",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Subject Horizontal Selector
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { sub ->
                    val isSelected = sub.id == selectedSubjectId
                    Surface(
                        onClick = { selectedSubjectId = sub.id },
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
                                text = sub.shortName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Active Subject Banner & Mode Switcher
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentSubject?.name ?: "Mata Pelajaran",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnNavyContainer
                            )
                            Text(
                                text = "Kategori: ${currentSubject?.category} • KKM/KKTP: ${currentSubject?.kkm ?: 75}",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnNavyContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TabRow(
                        selectedTabIndex = selectedGradeMode,
                        containerColor = Color.White,
                        contentColor = NavyPrimary,
                        modifier = Modifier.background(Color.White, RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedGradeMode == 0,
                            onClick = { selectedGradeMode = 0 },
                            text = { Text("Nilai Formatif (Harian)", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                        )
                        Tab(
                            selected = selectedGradeMode == 1,
                            onClick = { selectedGradeMode = 1 },
                            text = { Text("Nilai Sumatif (Ulangan & SAS)", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Student Grade Cards
        items(students, key = { it.id }) { student ->
            val studentGrades = gradeMap[student.id] ?: emptyList()
            val subjectSummary = studentGrades.find { it.subject.id == selectedSubjectId }

            GradeStudentCard(
                student = student,
                subjectSummary = subjectSummary,
                gradeMode = selectedGradeMode,
                onEditScore = { tpOrType, score ->
                    editingStudent = student
                    editingAssessmentType = tpOrType
                    currentScoreVal = score
                },
                onSendWhatsApp = {
                    val finalScore = subjectSummary?.finalScore ?: 80
                    viewModel.sendGradeWhatsApp(
                        context = context,
                        student = student,
                        subjectName = currentSubject?.name ?: "Mata Pelajaran",
                        assessmentTitle = if (selectedGradeMode == 0) "Hasil Nilai Asesmen Formatif Harian" else "Hasil Ulangan Sumatif & SAS",
                        score = finalScore,
                        notes = subjectSummary?.reportDescription ?: "Siswa menunjukkan perkembangan yang sangat baik."
                    )
                }
            )
        }
    }

    // Edit Score Dialog
    if (editingStudent != null && currentSubject != null) {
        val st = editingStudent!!
        AlertDialog(
            onDismissRequest = { editingStudent = null },
            title = {
                Text(
                    text = "Input Nilai: $editingAssessmentType",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Siswa: ${st.fullName}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Mapel: ${currentSubject.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Skor Nilai: $currentScoreVal / 100",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (currentScoreVal >= 75) NavyPrimary else RedDanger
                    )
                    Slider(
                        value = currentScoreVal.toFloat(),
                        onValueChange = { currentScoreVal = it.toInt() },
                        valueRange = 0f..100f,
                        steps = 100
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedGradeMode == 0) {
                            // Formative (TP)
                            viewModel.updateFormativeScore(
                                studentId = st.id,
                                subjectId = currentSubject.id,
                                tpCode = editingAssessmentType,
                                score = currentScoreVal
                            )
                        } else {
                            // Summative (SUMATIF_1, SUMATIF_2, STS, SAS)
                            viewModel.updateSummativeScore(
                                studentId = st.id,
                                subjectId = currentSubject.id,
                                type = editingAssessmentType,
                                score = currentScoreVal
                            )
                        }
                        editingStudent = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Simpan Nilai")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { editingStudent = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun GradeStudentCard(
    student: Student,
    subjectSummary: SubjectGradeSummary?,
    gradeMode: Int,
    onEditScore: (String, Int) -> Unit,
    onSendWhatsApp: () -> Unit
) {
    val finalScore = subjectSummary?.finalScore ?: 80
    val predicate = subjectSummary?.predicate ?: "Baik (B)"

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("grade_card_${student.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.fullName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "NISN: ${student.nisn}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (finalScore >= 75) Color(0xFFE0F2FE) else Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Nilai Akhir: $finalScore ($predicate)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (finalScore >= 75) NavyPrimary else RedDanger,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onSendWhatsApp, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Send, contentDescription = "WA Ortu", tint = GreenSuccess)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scores Grid
            if (gradeMode == 0) {
                // Formatif per TP (TP 6.1, TP 6.2, TP 6.3)
                val tp1 = subjectSummary?.formativeScores?.find { it.tpCode == "TP 6.1" }?.score ?: 80
                val tp2 = subjectSummary?.formativeScores?.find { it.tpCode == "TP 6.2" }?.score ?: 82
                val tp3 = subjectSummary?.formativeScores?.find { it.tpCode == "TP 6.3" }?.score ?: 85

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScoreBadgeButton("TP 6.1", tp1, Modifier.weight(1f)) { onEditScore("TP 6.1", tp1) }
                    ScoreBadgeButton("TP 6.2", tp2, Modifier.weight(1f)) { onEditScore("TP 6.2", tp2) }
                    ScoreBadgeButton("TP 6.3", tp3, Modifier.weight(1f)) { onEditScore("TP 6.3", tp3) }
                }
            } else {
                // Sumatif (Sumatif 1, Sumatif 2, STS, SAS)
                val s1 = subjectSummary?.summativeScores?.find { it.type == "SUMATIF_1" }?.score ?: 80
                val s2 = subjectSummary?.summativeScores?.find { it.type == "SUMATIF_2" }?.score ?: 82
                val sts = subjectSummary?.summativeScores?.find { it.type == "STS" }?.score ?: 84
                val sas = subjectSummary?.summativeScores?.find { it.type == "SAS" }?.score ?: 86

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ScoreBadgeButton("UH 1", s1, Modifier.weight(1f)) { onEditScore("SUMATIF_1", s1) }
                    ScoreBadgeButton("UH 2", s2, Modifier.weight(1f)) { onEditScore("SUMATIF_2", s2) }
                    ScoreBadgeButton("STS", sts, Modifier.weight(1f)) { onEditScore("STS", sts) }
                    ScoreBadgeButton("SAS", sas, Modifier.weight(1f)) { onEditScore("SAS", sas) }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Capaian Kompetensi Narrative Preview
            if (subjectSummary != null) {
                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📖 ${subjectSummary.reportDescription}",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreBadgeButton(
    label: String,
    score: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF1F5F9),
        modifier = modifier.height(44.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(
                text = "$score",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (score >= 75) NavyPrimary else RedDanger
            )
        }
    }
}
