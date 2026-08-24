package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Student
import com.example.ui.WaliKelasViewModel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OnNavyContainer
import com.example.ui.theme.RedDanger
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.TealSecondary
import com.example.util.StudentQrCard
import com.example.util.WhatsAppHelper

@Composable
fun StudentManagementScreen(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val students by viewModel.students.collectAsState()
    val school by viewModel.schoolProfile.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedGenderFilter by remember { mutableStateOf("ALL") } // "ALL", "L", "P"

    var showAddDialog by remember { mutableStateOf(false) }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var viewingQrStudent by remember { mutableStateOf<Student?>(null) }
    var studentToDelete by remember { mutableStateOf<Student?>(null) }

    val filteredStudents = students.filter { st ->
        val matchesSearch = st.fullName.contains(searchQuery, ignoreCase = true) ||
                st.nisn.contains(searchQuery) ||
                st.nis.contains(searchQuery)
        val matchesGender = when (selectedGenderFilter) {
            "L" -> st.gender == "L"
            "P" -> st.gender == "P"
            else -> true
        }
        matchesSearch && matchesGender
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & Add Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Data Peserta Didik",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Total ${students.size} Siswa (Daftar Foto Kelas VI)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            viewModel.syncStudentsWithPhotoRoster(context)
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sinkron", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset 29 Siswa", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            editingStudent = null
                            showAddDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_student_btn"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah", fontSize = 12.sp)
                    }
                }
            }
        }

        // Search & Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_search_input"),
                placeholder = { Text("Cari berdasarkan nama atau NISN...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedGenderFilter == "ALL",
                    onClick = { selectedGenderFilter = "ALL" },
                    label = { Text("Semua (${students.size})") }
                )
                FilterChip(
                    selected = selectedGenderFilter == "L",
                    onClick = { selectedGenderFilter = "L" },
                    label = { Text("Laki-laki (${students.count { it.gender == "L" }})") }
                )
                FilterChip(
                    selected = selectedGenderFilter == "P",
                    onClick = { selectedGenderFilter = "P" },
                    label = { Text("Perempuan (${students.count { it.gender == "P" }})") }
                )
            }
        }

        // Student List Items
        itemsIndexed(filteredStudents, key = { _, student -> student.id }) { index, student ->
            StudentItemCard(
                index = index + 1,
                student = student,
                onViewQr = { viewingQrStudent = student },
                onEdit = {
                    editingStudent = student
                    showAddDialog = true
                },
                onDelete = { studentToDelete = student },
                onSendWhatsApp = {
                    WhatsAppHelper.sendWhatsAppMessage(
                        context = context,
                        phoneNumber = student.parentPhone,
                        message = "Halo Bapak/Ibu ${student.parentName}, berikut informasi terkait ananda ${student.fullName} di Kelas 6 SD."
                    )
                }
            )
        }

        if (filteredStudents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada data siswa yang cocok.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddDialog) {
        AddEditStudentDialog(
            initialStudent = editingStudent,
            onDismiss = { showAddDialog = false },
            onSave = { st ->
                viewModel.saveStudent(st) {
                    showAddDialog = false
                }
            }
        )
    }

    // QR Code Badge Dialog
    if (viewingQrStudent != null) {
        val st = viewingQrStudent!!
        AlertDialog(
            onDismissRequest = { viewingQrStudent = null },
            title = {
                Text("Kartu Pelajar & QR Presensi", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = NavyContainer),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = school?.schoolName?.uppercase() ?: "SD NEGERI 01 MERDEKA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnNavyContainer,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "KARTU TANDA PESERTA DIDIK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyPrimary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // QR Matrix Canvas
                            StudentQrCard(
                                qrCodeId = st.qrCodeId,
                                modifier = Modifier.size(160.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = st.fullName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = OnNavyContainer,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "NISN: ${st.nisn} | NIS: ${st.nis}",
                                fontSize = 11.sp,
                                color = OnNavyContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "Kode QR: ${st.qrCodeId}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewingQrStudent = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text("Tutup")
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (studentToDelete != null) {
        val st = studentToDelete!!
        AlertDialog(
            onDismissRequest = { studentToDelete = null },
            title = { Text("Hapus Data Siswa?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Apakah Anda yakin ingin menghapus '${st.fullName}'? Data nilai dan absensi terkait siswa ini juga akan terhapus.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteStudent(st)
                        studentToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDanger)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { studentToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun StudentItemCard(
    index: Int,
    student: Student,
    onViewQr: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSendWhatsApp: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("student_card_${student.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Index & Gender-colored avatar
            Surface(
                color = if (student.gender == "L") Color(0xFFE3F2FD) else Color(0xFFFCE7F3),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "#$index",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (student.gender == "L") NavyPrimary else Color(0xFFDB2777)
                        )
                        Text(
                            text = if (student.gender == "L") "LK" else "PR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (student.gender == "L") Color(0xFF0369A1) else Color(0xFFBE185D)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Student Info
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = student.fullName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "NISN: ${student.nisn} | NIS: ${student.nis}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Ortu: ${student.parentName} (${student.parentPhone})",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onViewQr, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.QrCode, contentDescription = "QR Code", tint = NavyPrimary)
                }
                IconButton(onClick = onSendWhatsApp, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Send, contentDescription = "WA", tint = GreenSuccess)
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = RedDanger.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
fun AddEditStudentDialog(
    initialStudent: Student?,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    var nis by remember { mutableStateOf(initialStudent?.nis ?: "6013") }
    var nisn by remember { mutableStateOf(initialStudent?.nisn ?: "0138910234") }
    var fullName by remember { mutableStateOf(initialStudent?.fullName ?: "") }
    var nickname by remember { mutableStateOf(initialStudent?.nickname ?: "") }
    var gender by remember { mutableStateOf(initialStudent?.gender ?: "L") }
    var birthPlace by remember { mutableStateOf(initialStudent?.birthPlace ?: "Jakarta") }
    var birthDate by remember { mutableStateOf(initialStudent?.birthDate ?: "2013-05-15") }
    var religion by remember { mutableStateOf(initialStudent?.religion ?: "Islam") }
    var parentName by remember { mutableStateOf(initialStudent?.parentName ?: "") }
    var parentPhone by remember { mutableStateOf(initialStudent?.parentPhone ?: "628") }
    var parentJob by remember { mutableStateOf(initialStudent?.parentJob ?: "Karyawan") }
    var address by remember { mutableStateOf(initialStudent?.address ?: "") }
    var extracurricular by remember { mutableStateOf(initialStudent?.extracurricular ?: "Pramuka") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialStudent == null) "Tambah Peserta Didik Baru" else "Ubah Data Peserta Didik",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nama Lengkap Siswa *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = nisn,
                            onValueChange = { nisn = it },
                            label = { Text("NISN (10 Digit) *") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = nis,
                            onValueChange = { nis = it },
                            label = { Text("NIS *") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Jenis Kelamin:", style = MaterialTheme.typography.bodySmall)
                        FilterChip(
                            selected = gender == "L",
                            onClick = { gender = "L" },
                            label = { Text("Laki-laki (L)") }
                        )
                        FilterChip(
                            selected = gender == "P",
                            onClick = { gender = "P" },
                            label = { Text("Perempuan (P)") }
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = birthPlace,
                            onValueChange = { birthPlace = it },
                            label = { Text("Tempat Lahir") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = birthDate,
                            onValueChange = { birthDate = it },
                            label = { Text("Tgl Lahir (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = parentName,
                        onValueChange = { parentName = it },
                        label = { Text("Nama Orang Tua / Wali *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = parentPhone,
                        onValueChange = { parentPhone = it },
                        label = { Text("No. WhatsApp Ortu (e.g. 62812xxx) *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Alamat Tempat Tinggal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = extracurricular,
                        onValueChange = { extracurricular = it },
                        label = { Text("Kegiatan Ekstrakurikuler") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && nisn.isNotBlank()) {
                        val qrCode = initialStudent?.qrCodeId ?: "STD-6A-${nis.takeLast(3)}"
                        val updated = initialStudent?.copy(
                            nis = nis,
                            nisn = nisn,
                            fullName = fullName,
                            nickname = if (nickname.isBlank()) fullName.split(" ").first() else nickname,
                            gender = gender,
                            birthPlace = birthPlace,
                            birthDate = birthDate,
                            religion = religion,
                            parentName = parentName,
                            parentPhone = parentPhone,
                            parentJob = parentJob,
                            address = address,
                            extracurricular = extracurricular,
                            qrCodeId = qrCode
                        ) ?: Student(
                            id = 0,
                            nis = nis,
                            nisn = nisn,
                            fullName = fullName,
                            nickname = if (nickname.isBlank()) fullName.split(" ").first() else nickname,
                            gender = gender,
                            birthPlace = birthPlace,
                            birthDate = birthDate,
                            religion = religion,
                            parentName = parentName,
                            parentPhone = parentPhone,
                            parentJob = parentJob,
                            address = address,
                            extracurricular = extracurricular,
                            qrCodeId = qrCode
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Simpan Data Siswa")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
