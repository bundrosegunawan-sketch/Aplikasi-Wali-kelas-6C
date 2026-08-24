package com.example.util

import com.example.data.model.FormativeScore
import com.example.data.model.LearningObjective
import com.example.data.model.Subject
import com.example.data.model.SummativeScore

data class SubjectGradeSummary(
    val subject: Subject,
    val formativeScores: List<FormativeScore>,
    val summativeScores: List<SummativeScore>,
    val formativeAverage: Double,
    val summativeAverage: Double,
    val finalScore: Int, // 0 - 100
    val predicate: String, // "Sangat Baik" (A), "Baik" (B), "Cukup" (C), "Perlu Bimbingan" (D)
    val highestCompetence: String,
    val lowestCompetence: String,
    val reportDescription: String
)

data class StudentReportData(
    val studentId: Long,
    val studentName: String,
    val nisn: String,
    val subjectGrades: List<SubjectGradeSummary>,
    val overallAverage: Double,
    val rank: Int = 0,
    val totalHadir: Int = 0,
    val totalSakit: Int = 0,
    val totalIzin: Int = 0,
    val totalAlpa: Int = 0,
    val extracurricularNote: String = "Sangat aktif dalam kegiatan kepramukaan dan menunjukkan jiwa kepemimpinan.",
    val teacherNotes: String = "Pertahankan prestasi belajar dan tingkatkan terus keaktifan dalam diskusi kelompok."
)

object GradeCalculator {

    fun calculateSubjectSummary(
        subject: Subject,
        tps: List<LearningObjective>,
        formatives: List<FormativeScore>,
        summatives: List<SummativeScore>
    ): SubjectGradeSummary {
        val formAvg = if (formatives.isNotEmpty()) {
            formatives.map { it.score }.average()
        } else {
            75.0
        }

        val sumAvg = if (summatives.isNotEmpty()) {
            summatives.map { it.score }.average()
        } else {
            75.0
        }

        // Nilai Akhir (NA) Kurikulum Merdeka (Pembobotan 40% Formatif, 60% Sumatif)
        val finalScore = (formAvg * 0.4 + sumAvg * 0.6).toInt().coerceIn(0, 100)

        val predicate = when {
            finalScore >= 90 -> "Sangat Baik (A)"
            finalScore >= 80 -> "Baik (B)"
            finalScore >= 70 -> "Cukup (C)"
            else -> "Perlu Bimbingan (D)"
        }

        // Capaian Kompetensi Tertinggi & Terendah
        val sortedFormative = formatives.sortedByDescending { it.score }
        val highestTp = sortedFormative.firstOrNull()?.tpCode
        val lowestTp = sortedFormative.lastOrNull()?.tpCode

        val highestDesc = tps.find { it.tpCode == highestTp }?.title ?: "pemahaman konsep dasar ${subject.name}"
        val lowestDesc = tps.find { it.tpCode == lowestTp }?.title ?: "penerapan lanjutan materi"

        val reportNarrative = buildString {
            append("Menunjukkan penguasaan yang sangat baik dalam $highestDesc.")
            if (formatives.size > 1 && sortedFormative.first().score > sortedFormative.last().score) {
                append(" Perlu bimbingan dan latihan lebih lanjut dalam $lowestDesc.")
            } else {
                append(" Mampu mempertahankan konsistensi capaian pembelajaran dengan sangat baik.")
            }
        }

        return SubjectGradeSummary(
            subject = subject,
            formativeScores = formatives,
            summativeScores = summatives,
            formativeAverage = formAvg,
            summativeAverage = sumAvg,
            finalScore = finalScore,
            predicate = predicate,
            highestCompetence = highestDesc,
            lowestCompetence = lowestDesc,
            reportDescription = reportNarrative
        )
    }

    fun getPerformanceColor(score: Int): Long {
        return when {
            score >= 85 -> 0xFF10B981 // Green
            score >= 75 -> 0xFF0D47A1 // Blue
            score >= 65 -> 0xFFF59E0B // Amber
            else -> 0xFFEF4444 // Red
        }
    }
}
