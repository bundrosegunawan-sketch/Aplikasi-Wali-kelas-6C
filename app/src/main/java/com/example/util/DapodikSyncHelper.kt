package com.example.util

import com.example.data.model.AttendanceRecord
import com.example.data.model.FormativeScore
import com.example.data.model.SchoolProfile
import com.example.data.model.Student
import com.example.data.model.SummativeScore
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DapodikValidationResult(
    val isValid: Boolean,
    val warnings: List<String>,
    val errors: List<String>,
    val totalStudents: Int,
    val validNisnCount: Int
)

data class SyncLogEntry(
    val timestamp: String,
    val message: String,
    val isSuccess: Boolean
)

object DapodikSyncHelper {

    fun validateDapodikCompliance(
        school: SchoolProfile?,
        students: List<Student>
    ): DapodikValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        var validNisn = 0

        if (school == null) {
            errors.add("Data Sekolah belum diatur.")
            return DapodikValidationResult(false, warnings, errors, 0, 0)
        }

        // Validate NPSN (must be 8 numeric digits)
        if (school.npsn.length != 8 || !school.npsn.all { it.isDigit() }) {
            errors.add("NPSN Sekolah harus terdiri dari 8 digit angka resmi Kemendikdasmen.")
        }

        // Validate Guru NIP (18 digits)
        if (school.teacherNip.length != 18 || !school.teacherNip.all { it.isDigit() }) {
            warnings.add("NIP Wali Kelas disarankan 18 digit format BKN.")
        }

        // Validate Kepsek NIP (18 digits)
        if (school.headmasterNip.length != 18 || !school.headmasterNip.all { it.isDigit() }) {
            warnings.add("NIP Kepala Sekolah disarankan 18 digit format BKN.")
        }

        // Validate Students
        students.forEach { st ->
            if (st.nisn.length == 10 && st.nisn.all { it.isDigit() }) {
                validNisn++
            } else {
                warnings.add("Siswa ${st.fullName}: NISN '${st.nisn}' belum standar 10 digit Dapodik.")
            }
            if (st.parentPhone.isBlank()) {
                warnings.add("Siswa ${st.fullName}: Nomor WhatsApp orang tua belum terisi.")
            }
        }

        val isValid = errors.isEmpty()
        return DapodikValidationResult(
            isValid = isValid,
            warnings = warnings,
            errors = errors,
            totalStudents = students.size,
            validNisnCount = validNisn
        )
    }

    /**
     * Exports full school data as encrypted/standard Kemendikdasmen Dapodik JSON payload
     */
    fun generateDapodikJsonExport(
        school: SchoolProfile,
        students: List<Student>,
        formatives: List<FormativeScore>,
        summatives: List<SummativeScore>,
        attendance: List<AttendanceRecord>
    ): String {
        val root = JSONObject()
        root.put("dapodik_version", "2026.a.Kemendikdasmen")
        root.put("export_timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("server_endpoint", "https://dapodik.kemdikbud.go.id/sync/v2")

        // School
        val schoolObj = JSONObject()
        schoolObj.put("npsn", school.npsn)
        schoolObj.put("nama_sekolah", school.schoolName)
        schoolObj.put("alamat", school.schoolAddress)
        schoolObj.put("kecamatan", school.district)
        schoolObj.put("kabupaten_kota", school.regency)
        schoolObj.put("provinsi", school.province)
        schoolObj.put("kode_pos", school.postalCode)
        schoolObj.put("wali_kelas", school.teacherName)
        schoolObj.put("nip_wali_kelas", school.teacherNip)
        schoolObj.put("kepala_sekolah", school.headmasterName)
        schoolObj.put("nip_kepala_sekolah", school.headmasterNip)
        schoolObj.put("rombongan_belajar", school.className)
        schoolObj.put("tahun_ajaran", school.academicYear)
        schoolObj.put("semester", school.semester)
        root.put("data_sekolah", schoolObj)

        // Students Array
        val studentsArr = JSONArray()
        students.forEach { s ->
            val sObj = JSONObject()
            sObj.put("nis", s.nis)
            sObj.put("nisn", s.nisn)
            sObj.put("nama_lengkap", s.fullName)
            sObj.put("nama_panggilan", s.nickname)
            sObj.put("jenis_kelamin", s.gender)
            sObj.put("tempat_lahir", s.birthPlace)
            sObj.put("tanggal_lahir", s.birthDate)
            sObj.put("agama", s.religion)
            sObj.put("nama_orang_tua", s.parentName)
            sObj.put("no_whatsapp_ortu", s.parentPhone)
            sObj.put("pekerjaan_ortu", s.parentJob)
            sObj.put("alamat_siswa", s.address)
            sObj.put("qr_code_id", s.qrCodeId)
            sObj.put("tinggi_badan_cm", s.heightCm)
            sObj.put("berat_badan_kg", s.weightKg)
            sObj.put("ekstrakurikuler", s.extracurricular)
            studentsArr.put(sObj)
        }
        root.put("peserta_didik", studentsArr)

        // Metadata Counts
        val meta = JSONObject()
        meta.put("total_siswa", students.size)
        meta.put("total_nilai_formatif", formatives.size)
        meta.put("total_nilai_sumatif", summatives.size)
        meta.put("total_rekam_presensi", attendance.size)
        root.put("rekapitulasi_metadata", meta)

        return root.toString(2)
    }
}
