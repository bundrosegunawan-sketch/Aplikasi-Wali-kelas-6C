package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolProfile
import com.example.ui.WaliKelasViewModel
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.OnNavyContainer
import com.example.ui.theme.TealSecondary

@Composable
fun SchoolProfileScreen(
    viewModel: WaliKelasViewModel,
    modifier: Modifier = Modifier
) {
    val schoolProfile by viewModel.schoolProfile.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    val school = schoolProfile ?: SchoolProfile()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Data Induk Satuan Pendidikan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Standar Kemendikdasmen & Kurikulum Merdeka",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("edit_school_profile_btn")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ubah Data", fontSize = 12.sp)
                }
            }
        }

        // 1. Data Sekolah Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Data Profil Sekolah", icon = Icons.Default.School, color = NavyPrimary)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileItem(label = "Nama Satuan Pendidikan", value = school.schoolName, isBold = true)
                    ProfileItem(label = "NPSN (Nomor Pokok Sekolah Nasional)", value = school.npsn)
                    ProfileItem(label = "Alamat Lengkap", value = school.schoolAddress)
                    ProfileItem(label = "Kecamatan / Kab. Kota", value = "${school.district}, ${school.regency}")
                    ProfileItem(label = "Provinsi & Kode Pos", value = "${school.province} - ${school.postalCode}")
                    ProfileItem(label = "Email Resmi", value = school.email)
                    ProfileItem(label = "Kurikulum yang Digunakan", value = school.curriculum)
                    ProfileItem(label = "Tahun Ajaran & Semester", value = "TA ${school.academicYear} | Semester ${school.semester}")
                }
            }
        }

        // 2. Data Guru Wali Kelas 6 Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Data Guru / Wali Kelas VI", icon = Icons.Default.Person, color = TealSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileItem(label = "Nama Lengkap & Gelar", value = school.teacherName, isBold = true)
                    ProfileItem(label = "NIP (Nomor Induk Pegawai)", value = school.teacherNip)
                    ProfileItem(label = "Rombongan Belajar / Kelas", value = school.className)
                    ProfileItem(label = "Fase Pembelajaran", value = school.phase)
                    ProfileItem(label = "No. WhatsApp Wali Kelas", value = school.teacherPhone)
                }
            }
        }

        // 3. Data Kepala Sekolah Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeader(title = "Data Kepala Sekolah", icon = Icons.Default.AccountBalance, color = GoldAccent)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileItem(label = "Nama Lengkap Kepala Sekolah", value = school.headmasterName, isBold = true)
                    ProfileItem(label = "NIP Kepala Sekolah", value = school.headmasterNip)
                    ProfileItem(label = "Tempat & Tanggal Titimangsa Rapor", value = school.placeAndDateOfReport)
                }
            }
        }

        // 4. Kurikulum Merdeka Kemendikdasmen Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Ketetapan Kemendikdasmen Terbaru",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnNavyContainer
                        )
                        Text(
                            text = "Penilaian Kelas 6 SD Fase C berbasis Capaian Pembelajaran (CP), Asesmen Formatif, dan Asesmen Sumatif dengan laporan deskriptif naratif.",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnNavyContainer.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }
    }

    if (showEditDialog) {
        EditSchoolProfileDialog(
            currentProfile = school,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                viewModel.updateSchoolProfile(updated)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = color.copy(alpha = 0.15f),
            shape = CircleShape,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileItem(label: String, value: String, isBold: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = Color(0xFFF1F5F9))
    }
}

@Composable
fun EditSchoolProfileDialog(
    currentProfile: SchoolProfile,
    onDismiss: () -> Unit,
    onSave: (SchoolProfile) -> Unit
) {
    var schoolName by remember { mutableStateOf(currentProfile.schoolName) }
    var npsn by remember { mutableStateOf(currentProfile.npsn) }
    var schoolAddress by remember { mutableStateOf(currentProfile.schoolAddress) }
    var teacherName by remember { mutableStateOf(currentProfile.teacherName) }
    var teacherNip by remember { mutableStateOf(currentProfile.teacherNip) }
    var teacherPhone by remember { mutableStateOf(currentProfile.teacherPhone) }
    var headmasterName by remember { mutableStateOf(currentProfile.headmasterName) }
    var headmasterNip by remember { mutableStateOf(currentProfile.headmasterNip) }
    var className by remember { mutableStateOf(currentProfile.className) }
    var reportDate by remember { mutableStateOf(currentProfile.placeAndDateOfReport) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Data Sekolah & Guru", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = schoolName,
                        onValueChange = { schoolName = it },
                        label = { Text("Nama Sekolah") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = npsn,
                        onValueChange = { npsn = it },
                        label = { Text("NPSN (8 Digit)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = schoolAddress,
                        onValueChange = { schoolAddress = it },
                        label = { Text("Alamat Sekolah") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = teacherName,
                        onValueChange = { teacherName = it },
                        label = { Text("Nama Wali Kelas VI") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = teacherNip,
                        onValueChange = { teacherNip = it },
                        label = { Text("NIP Wali Kelas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = teacherPhone,
                        onValueChange = { teacherPhone = it },
                        label = { Text("No. WA Wali Kelas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = headmasterName,
                        onValueChange = { headmasterName = it },
                        label = { Text("Nama Kepala Sekolah") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = headmasterNip,
                        onValueChange = { headmasterNip = it },
                        label = { Text("NIP Kepala Sekolah") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = className,
                        onValueChange = { className = it },
                        label = { Text("Nama Rombel / Kelas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = reportDate,
                        onValueChange = { reportDate = it },
                        label = { Text("Titimangsa Rapor (Tempat, Tanggal)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentProfile.copy(
                            schoolName = schoolName,
                            npsn = npsn,
                            schoolAddress = schoolAddress,
                            teacherName = teacherName,
                            teacherNip = teacherNip,
                            teacherPhone = teacherPhone,
                            headmasterName = headmasterName,
                            headmasterNip = headmasterNip,
                            className = className,
                            placeAndDateOfReport = reportDate
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Simpan Perubahan")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
