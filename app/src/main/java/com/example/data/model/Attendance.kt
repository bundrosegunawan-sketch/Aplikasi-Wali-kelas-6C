package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance_records",
    indices = [Index(value = ["studentId", "date"], unique = true)]
)
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val date: String, // "YYYY-MM-DD" e.g. "2025-10-15"
    val status: String, // "HADIR", "SAKIT", "IZIN", "ALPA", "TERLAMBAT"
    val time: String = "07:05 WIB",
    val method: String = "QR_SCAN", // "QR_SCAN" or "MANUAL"
    val notes: String = "",
    val notifiedWa: Boolean = false
)

data class AttendanceSummary(
    val hadir: Int = 0,
    val sakit: Int = 0,
    val izin: Int = 0,
    val alpa: Int = 0,
    val terlambat: Int = 0,
    val totalDays: Int = 0
)
