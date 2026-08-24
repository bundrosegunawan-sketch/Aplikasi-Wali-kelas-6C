package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "formative_scores",
    indices = [Index(value = ["studentId", "subjectId", "tpCode"], unique = true)]
)
data class FormativeScore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subjectId: String,
    val tpCode: String, // e.g. "TP 6.1"
    val score: Int = 80, // 0 - 100
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "summative_scores",
    indices = [Index(value = ["studentId", "subjectId", "type"], unique = true)]
)
data class SummativeScore(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subjectId: String,
    val type: String, // "SUMATIF_1", "SUMATIF_2", "SUMATIF_3", "STS" (Tengah Semester), "SAS" (Akhir Semester)
    val score: Int = 80, // 0 - 100
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
