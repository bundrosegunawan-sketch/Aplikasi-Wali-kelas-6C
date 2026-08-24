package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nis: String,
    val nisn: String,
    val fullName: String,
    val nickname: String,
    val gender: String, // "L" atau "P"
    val birthPlace: String,
    val birthDate: String, // "2013-05-14"
    val religion: String = "Islam",
    val parentName: String,
    val parentPhone: String, // "628xxxx" for WhatsApp
    val parentJob: String = "Wiraswasta",
    val address: String,
    val qrCodeId: String, // e.g. "STD-6A-001"
    val heightCm: Int = 145,
    val weightKg: Int = 38,
    val extracurricular: String = "Pramuka, Dokter Kecil",
    val studentNotes: String = "Menunjukkan sikap kepemimpinan yang baik dan aktif berdiskusi di kelas."
)
