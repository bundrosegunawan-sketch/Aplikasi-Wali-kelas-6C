package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.AttendanceRecord
import com.example.data.model.SchoolProfile
import com.example.data.model.Student
import com.example.data.model.Subject
import java.io.File
import java.io.FileOutputStream

object PdfReportGenerator {

    /**
     * Generates Official Student Report Card (Rapor Kurikulum Merdeka Semester 1)
     */
    fun generateStudentReportCardPdf(
        context: Context,
        school: SchoolProfile,
        student: Student,
        grades: List<SubjectGradeSummary>,
        attendanceList: List<AttendanceRecord>
    ): File? {
        try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard (595x842 pt)
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = Color.DKGRAY
                strokeWidth = 1f
            }
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            var y = 40f

            // --- HEADER (Kop Rapor Kemendikdasmen) ---
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 12f
            paint.isFakeBoldText = true
            paint.color = Color.rgb(13, 71, 161) // Navy Blue
            canvas.drawText("PEMERINTAH PROVINSI ${school.province.uppercase()}", 297.5f, y, paint)
            y += 15f
            canvas.drawText("DINAS PENDIDIKAN ${school.regency.uppercase()}", 297.5f, y, paint)
            y += 16f
            paint.textSize = 14f
            paint.color = Color.BLACK
            canvas.drawText(school.schoolName.uppercase(), 297.5f, y, paint)
            y += 13f
            paint.textSize = 8.5f
            paint.isFakeBoldText = false
            paint.color = Color.DKGRAY
            canvas.drawText("Alamat: ${school.schoolAddress} | NPSN: ${school.npsn} | Email: ${school.email}", 297.5f, y, paint)
            y += 8f

            // Header Separator Line
            strokePaint.strokeWidth = 2f
            strokePaint.color = Color.BLACK
            canvas.drawLine(40f, y, 555f, y, strokePaint)
            strokePaint.strokeWidth = 0.5f
            canvas.drawLine(40f, y + 2, 555f, y + 2, strokePaint)
            y += 18f

            // Title
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 11.5f
            paint.isFakeBoldText = true
            paint.color = Color.BLACK
            canvas.drawText("LAPORAN HASIL BELAJAR (RAPOR SISWA)", 297.5f, y, paint)
            y += 13f
            paint.textSize = 9.5f
            paint.isFakeBoldText = false
            canvas.drawText("Semester 1 (Ganjil) - Tahun Ajaran ${school.academicYear} - ${school.curriculum}", 297.5f, y, paint)
            y += 18f

            // --- STUDENT IDENTITY SECTION ---
            paint.textAlign = Paint.Align.LEFT
            paint.textSize = 8.5f
            val col1 = 45f
            val col2 = 130f
            val col3 = 330f
            val col4 = 415f

            paint.isFakeBoldText = true
            canvas.drawText("Nama Peserta Didik", col1, y, paint)
            paint.isFakeBoldText = false
            canvas.drawText(": ${student.fullName}", col2, y, paint)

            paint.isFakeBoldText = true
            canvas.drawText("Kelas", col3, y, paint)
            paint.isFakeBoldText = false
            canvas.drawText(": ${school.className}", col4, y, paint)
            y += 12f

            paint.isFakeBoldText = true
            canvas.drawText("NIS / NISN", col1, y, paint)
            paint.isFakeBoldText = false
            canvas.drawText(": ${student.nis} / ${student.nisn}", col2, y, paint)

            paint.isFakeBoldText = true
            canvas.drawText("Fase", col3, y, paint)
            paint.isFakeBoldText = false
            canvas.drawText(": ${school.phase}", col4, y, paint)
            y += 15f

            // --- ACADEMIC TABLE ---
            // Header
            val tableLeft = 40f
            val tableRight = 555f
            val colNoW = 25f
            val colMapelW = 140f
            val colNilaiW = 45f
            val colDescW = tableRight - tableLeft - colNoW - colMapelW - colNilaiW

            fillPaint.color = Color.rgb(227, 242, 253) // Light Navy
            canvas.drawRect(tableLeft, y, tableRight, y + 20, fillPaint)
            strokePaint.strokeWidth = 1f
            strokePaint.color = Color.BLACK
            canvas.drawRect(tableLeft, y, tableRight, y + 20, strokePaint)

            paint.textSize = 8.5f
            paint.isFakeBoldText = true
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("No", tableLeft + colNoW / 2, y + 13, paint)
            canvas.drawText("Mata Pelajaran", tableLeft + colNoW + colMapelW / 2, y + 13, paint)
            canvas.drawText("Nilai Akhir", tableLeft + colNoW + colMapelW + colNilaiW / 2, y + 13, paint)
            canvas.drawText("Capaian Kompetensi (Deskripsi)", tableLeft + colNoW + colMapelW + colNilaiW + colDescW / 2, y + 13, paint)

            // Vertical header divider
            canvas.drawLine(tableLeft + colNoW, y, tableLeft + colNoW, y + 20, strokePaint)
            canvas.drawLine(tableLeft + colNoW + colMapelW, y, tableLeft + colNoW + colMapelW, y + 20, strokePaint)
            canvas.drawLine(tableLeft + colNoW + colMapelW + colNilaiW, y, tableLeft + colNoW + colMapelW + colNilaiW, y + 20, strokePaint)
            y += 20f

            // Rows
            grades.forEachIndexed { index, grade ->
                val rowHeight = 32f

                // Row background (alternating)
                if (index % 2 == 1) {
                    fillPaint.color = Color.rgb(248, 250, 252)
                    canvas.drawRect(tableLeft, y, tableRight, y + rowHeight, fillPaint)
                }

                canvas.drawRect(tableLeft, y, tableRight, y + rowHeight, strokePaint)
                canvas.drawLine(tableLeft + colNoW, y, tableLeft + colNoW, y + rowHeight, strokePaint)
                canvas.drawLine(tableLeft + colNoW + colMapelW, y, tableLeft + colNoW + colMapelW, y + rowHeight, strokePaint)
                canvas.drawLine(tableLeft + colNoW + colMapelW + colNilaiW, y, tableLeft + colNoW + colMapelW + colNilaiW, y + rowHeight, strokePaint)

                // No
                paint.textAlign = Paint.Align.CENTER
                paint.isFakeBoldText = false
                paint.textSize = 8.5f
                canvas.drawText("${index + 1}", tableLeft + colNoW / 2, y + 15, paint)

                // Mapel
                paint.textAlign = Paint.Align.LEFT
                paint.isFakeBoldText = true
                canvas.drawText(grade.subject.name, tableLeft + colNoW + 4, y + 14, paint)

                // Nilai
                paint.textAlign = Paint.Align.CENTER
                paint.isFakeBoldText = true
                paint.textSize = 9.5f
                paint.color = if (grade.finalScore >= 75) Color.rgb(13, 71, 161) else Color.RED
                canvas.drawText("${grade.finalScore}", tableLeft + colNoW + colMapelW + colNilaiW / 2, y + 15, paint)
                paint.color = Color.BLACK
                paint.textSize = 8f

                // Capaian Deskripsi
                paint.textAlign = Paint.Align.LEFT
                paint.isFakeBoldText = false
                val descLines = wrapText(grade.reportDescription, 72)
                var descY = y + 11
                descLines.take(3).forEach { line ->
                    canvas.drawText(line, tableLeft + colNoW + colMapelW + colNilaiW + 5, descY, paint)
                    descY += 9.5f
                }

                y += rowHeight
            }

            y += 10f

            // --- EKSTRAKURIKULER & ABSENSI SECTION ---
            val boxWidth = (tableRight - tableLeft - 10f) / 2
            val box1Left = tableLeft
            val box2Left = tableLeft + boxWidth + 10f

            // Ekstrakurikuler Box
            fillPaint.color = Color.rgb(241, 245, 249)
            canvas.drawRect(box1Left, y, box1Left + boxWidth, y + 60, fillPaint)
            canvas.drawRect(box1Left, y, box1Left + boxWidth, y + 60, strokePaint)

            paint.textAlign = Paint.Align.LEFT
            paint.isFakeBoldText = true
            paint.textSize = 8.5f
            canvas.drawText("Ekstrakurikuler:", box1Left + 6, y + 13, paint)
            paint.isFakeBoldText = false
            canvas.drawText("• ${student.extracurricular}", box1Left + 6, y + 26, paint)
            canvas.drawText("Predikat: Sangat Baik / Aktif", box1Left + 6, y + 38, paint)
            canvas.drawText("Catatan: Menunjukkan kedisiplinan tinggi", box1Left + 6, y + 50, paint)

            // Absensi Box
            var sakit = 0
            var izin = 0
            var alpa = 0
            attendanceList.filter { it.studentId == student.id }.forEach {
                when (it.status) {
                    "SAKIT" -> sakit++
                    "IZIN" -> izin++
                    "ALPA" -> alpa++
                }
            }

            canvas.drawRect(box2Left, y, box2Left + boxWidth, y + 60, fillPaint)
            canvas.drawRect(box2Left, y, box2Left + boxWidth, y + 60, strokePaint)

            paint.isFakeBoldText = true
            canvas.drawText("Ketidakhadiran (Semester 1):", box2Left + 6, y + 13, paint)
            paint.isFakeBoldText = false
            canvas.drawText("1. Sakit (S)               : $sakit hari", box2Left + 6, y + 26, paint)
            canvas.drawText("2. Izin (I)                 : $izin hari", box2Left + 6, y + 38, paint)
            canvas.drawText("3. Tanpa Ket. (A)   : $alpa hari", box2Left + 6, y + 50, paint)

            y += 70f

            // --- CATATAN WALI KELAS ---
            canvas.drawRect(tableLeft, y, tableRight, y + 30, fillPaint)
            canvas.drawRect(tableLeft, y, tableRight, y + 30, strokePaint)
            paint.isFakeBoldText = true
            canvas.drawText("Catatan Wali Kelas:", tableLeft + 6, y + 12, paint)
            paint.isFakeBoldText = false
            canvas.drawText(student.studentNotes, tableLeft + 6, y + 23, paint)

            y += 40f

            // --- OFFICIAL SIGNATURES ---
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 8.5f
            val sigCol1 = 110f
            val sigCol2 = 460f

            paint.isFakeBoldText = false
            canvas.drawText("Mengetahui,", sigCol1, y, paint)
            canvas.drawText("${school.placeAndDateOfReport}", sigCol2, y, paint)
            y += 12f
            canvas.drawText("Orang Tua / Wali Siswa", sigCol1, y, paint)
            canvas.drawText("Wali Kelas VI", sigCol2, y, paint)

            y += 45f
            paint.isFakeBoldText = true
            canvas.drawText("( ${student.parentName} )", sigCol1, y, paint)
            canvas.drawText(school.teacherName, sigCol2, y, paint)
            y += 11f
            paint.isFakeBoldText = false
            canvas.drawText("NIP. ${school.teacherNip}", sigCol2, y, paint)

            y += 18f
            canvas.drawText("Mengetahui,", 297.5f, y, paint)
            y += 11f
            canvas.drawText("Kepala Sekolah ${school.schoolName}", 297.5f, y, paint)
            y += 40f
            paint.isFakeBoldText = true
            canvas.drawText(school.headmasterName, 297.5f, y, paint)
            y += 11f
            paint.isFakeBoldText = false
            canvas.drawText("NIP. ${school.headmasterNip}", 297.5f, y, paint)

            pdfDoc.finishPage(page)

            // Save to Cache / Documents file
            val fileDir = File(context.cacheDir, "pdf_reports")
            if (!fileDir.exists()) fileDir.mkdirs()
            val file = File(fileDir, "Rapor_${student.nisn}_${student.nickname}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDoc.writeTo(outputStream)
            outputStream.close()
            pdfDoc.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Generates Class Grade Ledger (Buku Leger Nilai Kelas 6 SD)
     */
    fun generateClassLegerPdf(
        context: Context,
        school: SchoolProfile,
        students: List<Student>,
        subjects: List<Subject>,
        gradeSummaries: Map<Long, List<SubjectGradeSummary>>
    ): File? {
        try {
            val pdfDoc = PdfDocument()
            // Landscape A4 (842x595 pt)
            val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = Color.BLACK
                strokeWidth = 0.8f
            }
            val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            var y = 35f

            // Title
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 13f
            paint.isFakeBoldText = true
            paint.color = Color.rgb(13, 71, 161)
            canvas.drawText("BUKU LEGER NILAI HASIL BELAJAR PESERTA DIDIK", 421f, y, paint)
            y += 15f
            paint.textSize = 10f
            paint.color = Color.BLACK
            paint.isFakeBoldText = false
            canvas.drawText("${school.schoolName} | ${school.className} | Semester 1 | Tahun Ajaran ${school.academicYear}", 421f, y, paint)
            y += 20f

            val tableLeft = 30f
            val tableRight = 812f
            val colNoW = 25f
            val colNisnW = 65f
            val colNameW = 150f
            val remainW = tableRight - tableLeft - colNoW - colNisnW - colNameW - 50f - 50f
            val colMapelW = remainW / subjects.size

            // Table Header
            fillPaint.color = Color.rgb(227, 242, 253)
            canvas.drawRect(tableLeft, y, tableRight, y + 25, fillPaint)
            canvas.drawRect(tableLeft, y, tableRight, y + 25, strokePaint)

            paint.textSize = 8f
            paint.isFakeBoldText = true
            paint.textAlign = Paint.Align.CENTER
            canvas.drawText("No", tableLeft + colNoW / 2, y + 16, paint)
            canvas.drawText("NISN", tableLeft + colNoW + colNisnW / 2, y + 16, paint)
            canvas.drawText("Nama Siswa", tableLeft + colNoW + colNisnW + colNameW / 2, y + 16, paint)

            var curX = tableLeft + colNoW + colNisnW + colNameW
            subjects.forEach { sub ->
                canvas.drawText(sub.shortName, curX + colMapelW / 2, y + 16, paint)
                curX += colMapelW
            }
            canvas.drawText("Rerata", curX + 25f, y + 16, paint)
            canvas.drawText("Rank", curX + 75f, y + 16, paint)
            y += 25f

            // Table Rows
            val studentAverages = students.map { st ->
                val list = gradeSummaries[st.id] ?: emptyList()
                val avg = if (list.isNotEmpty()) list.map { it.finalScore }.average() else 0.0
                st.id to avg
            }.sortedByDescending { it.second }

            val rankMap = studentAverages.mapIndexed { idx, pair -> pair.first to (idx + 1) }.toMap()

            students.forEachIndexed { index, student ->
                val rowH = 18f
                if (index % 2 == 1) {
                    fillPaint.color = Color.rgb(248, 250, 252)
                    canvas.drawRect(tableLeft, y, tableRight, y + rowH, fillPaint)
                }
                canvas.drawRect(tableLeft, y, tableRight, y + rowH, strokePaint)

                paint.isFakeBoldText = false
                paint.textAlign = Paint.Align.CENTER
                canvas.drawText("${index + 1}", tableLeft + colNoW / 2, y + 12, paint)
                canvas.drawText(student.nisn, tableLeft + colNoW + colNisnW / 2, y + 12, paint)

                paint.textAlign = Paint.Align.LEFT
                canvas.drawText(student.fullName.take(24), tableLeft + colNoW + colNisnW + 4, y + 12, paint)

                val studentGrades = gradeSummaries[student.id] ?: emptyList()
                var subX = tableLeft + colNoW + colNisnW + colNameW
                paint.textAlign = Paint.Align.CENTER
                subjects.forEach { sub ->
                    val grade = studentGrades.find { it.subject.id == sub.id }?.finalScore ?: 0
                    canvas.drawText("$grade", subX + colMapelW / 2, y + 12, paint)
                    subX += colMapelW
                }

                val avg = studentAverages.find { it.first == student.id }?.second ?: 0.0
                val rank = rankMap[student.id] ?: (index + 1)
                paint.isFakeBoldText = true
                canvas.drawText(String.format("%.1f", avg), subX + 25f, y + 12, paint)
                canvas.drawText("$rank", subX + 75f, y + 12, paint)

                y += rowH
            }

            y += 30f
            // Signatures
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 8.5f
            paint.isFakeBoldText = false
            canvas.drawText("Mengetahui,", 180f, y, paint)
            canvas.drawText("${school.placeAndDateOfReport}", 660f, y, paint)
            y += 12f
            canvas.drawText("Kepala Sekolah", 180f, y, paint)
            canvas.drawText("Wali Kelas VI", 660f, y, paint)
            y += 40f
            paint.isFakeBoldText = true
            canvas.drawText(school.headmasterName, 180f, y, paint)
            canvas.drawText(school.teacherName, 660f, y, paint)
            y += 11f
            paint.isFakeBoldText = false
            canvas.drawText("NIP. ${school.headmasterNip}", 180f, y, paint)
            canvas.drawText("NIP. ${school.teacherNip}", 660f, y, paint)

            pdfDoc.finishPage(page)

            val fileDir = File(context.cacheDir, "pdf_reports")
            if (!fileDir.exists()) fileDir.mkdirs()
            val file = File(fileDir, "Leger_Nilai_Kelas_6_${school.academicYear.replace("/", "_")}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDoc.writeTo(outputStream)
            outputStream.close()
            pdfDoc.close()

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Opens or shares the generated PDF
     */
    fun openPdfFile(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "com.aistudio.walikelas6sd.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "Buka Dokumen PDF Rapor"))
        } catch (e: Exception) {
            Toast.makeText(context, "Tidak ada aplikasi pembaca PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun sharePdfFile(context: Context, file: File, subjectText: String) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "com.aistudio.walikelas6sd.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, subjectText)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "Bagikan Dokumen Rapor PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal membagikan PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun wrapText(text: String, maxCharsPerLine: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            if (currentLine.length + word.length + 1 > maxCharsPerLine) {
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            } else {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }
}
