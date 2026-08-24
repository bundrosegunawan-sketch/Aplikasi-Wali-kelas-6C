package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.AttendanceRecord
import com.example.data.model.SchoolProfile
import com.example.data.model.Student

object WhatsAppHelper {

    fun sendWhatsAppMessage(context: Context, phoneNumber: String, message: String) {
        try {
            // Clean phone number (e.g. 0812 -> 62812)
            var cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
            if (cleanPhone.startsWith("0")) {
                cleanPhone = "62" + cleanPhone.substring(1)
            } else if (!cleanPhone.startsWith("62") && cleanPhone.isNotEmpty()) {
                cleanPhone = "62$cleanPhone"
            }

            val encodedMessage = Uri.encode(message)
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak dapat membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun buildAttendanceMessage(
        school: SchoolProfile,
        student: Student,
        attendance: AttendanceRecord
    ): String {
        val statusText = when (attendance.status) {
            "HADIR" -> "HADIR di kelas tepat waktu"
            "TERLAMBAT" -> "HADIR TERLAMBAT (Pukul ${attendance.time})"
            "SAKIT" -> "SAKIT (${if (attendance.notes.isNotBlank()) attendance.notes else "Surat dokter terlampir"})"
            "IZIN" -> "IZIN (${if (attendance.notes.isNotBlank()) attendance.notes else "Ada keperluan keluarga"})"
            "ALPA" -> "BELUM ADA KETERANGAN (ALPA)"
            else -> attendance.status
        }

        return """
*NOTIFIKASI KEHADIRAN SISWA - KELAS 6 SD*
🏛 *${school.schoolName}*
----------------------------------------
Yth. Orang Tua / Wali dari:
👤 *Nama Siswa:* ${student.fullName}
🔢 *NISN / NIS:* ${student.nisn} / ${student.nis}
🏫 *Kelas:* ${school.className}
📅 *Tanggal:* ${attendance.date} (${attendance.time})

📌 *Status Kehadiran:*
➡️ *${statusText}*

${if (attendance.notes.isNotBlank()) "📝 *Catatan:* ${attendance.notes}\n" else ""}
Demikian informasi kehadiran ananda kami sampaikan. Terima kasih atas perhatian dan kerja sama Bapak/Ibu.

Hormat kami,
*Wali Kelas 6:* ${school.teacherName}
NIP. ${school.teacherNip}
----------------------------------------
_Pesan otomatis Sistem Administrasi Wali Kelas 6 SD Kurikulum Merdeka_
        """.trimIndent()
    }

    fun buildGradeMessage(
        school: SchoolProfile,
        student: Student,
        subjectName: String,
        assessmentTitle: String,
        score: Int,
        notes: String
    ): String {
        val predicate = when {
            score >= 90 -> "Sangat Baik (A)"
            score >= 80 -> "Baik (B)"
            score >= 70 -> "Cukup (C)"
            else -> "Perlu Bimbingan (Remedial)"
        }

        return """
*LAPORAN HASIL BELAJAR SISWA*
🏛 *${school.schoolName}*
----------------------------------------
Yth. Bapak/Ibu Orang Tua dari:
👤 *Nama Siswa:* ${student.fullName}
🏫 *Kelas:* ${school.className} (Fase C)
📚 *Mata Pelajaran:* ${subjectName}
🎯 *Jenis Asesmen:* ${assessmentTitle}

📊 *Hasil Perolehan Nilai:*
⭐ *Skor:* ${score} / 100
🎖️ *Predikat:* ${predicate}

${if (notes.isNotBlank()) "📝 *Uraian / Catatan Guru:* $notes\n" else ""}
Mohon ananda tetap dimotivasi untuk giat belajar dan mendalami materi di rumah.

Salam hangat,
*Wali Kelas 6:* ${school.teacherName}
NIP. ${school.teacherNip}
        """.trimIndent()
    }

    fun buildReportAnnouncementMessage(
        school: SchoolProfile,
        student: Student,
        averageScore: Double,
        rank: Int
    ): String {
        return """
*PEMBERITAHUAN RAPOR SEMESTER 1 (KURIKULUM MERDEKA)*
🏛 *${school.schoolName}*
Tahun Ajaran ${school.academicYear}
----------------------------------------
Yth. Bapak/Ibu Orang Tua/Wali dari:
👤 *Nama Siswa:* ${student.fullName}
🔢 *NISN:* ${student.nisn}
🏫 *Kelas:* ${school.className}

Alhamdulillah, proses pembelajaran Semester 1 telah selesai. Berikut ringkasan capaian ananda:
📈 *Rerata Nilai Akhir:* ${String.format("%.1f", averageScore)}
🏆 *Peringkat Kelas:* Ke-${rank}

📄 Rapor resmi format PDF Kemendikdasmen telah diterbitkan dan dapat diunduh melalui wali kelas.
Terima kasih atas bimbingan Bapak/Ibu selama semester ganjil ini.

Salam hormat,
*Wali Kelas:* ${school.teacherName}
*Kepala Sekolah:* ${school.headmasterName}
        """.trimIndent()
    }
}
