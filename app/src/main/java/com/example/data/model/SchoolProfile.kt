package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school_profile")
data class SchoolProfile(
    @PrimaryKey val id: Int = 1,
    val schoolName: String = "SD NEGERI 01 MERDEKA BELAJAR",
    val npsn: String = "20108923",
    val schoolAddress: String = "Jl. Pendidikan Nasional No. 45, Kebayoran Baru",
    val district: String = "Kebayoran Baru",
    val regency: String = "Jakarta Selatan",
    val province: String = "DKI Jakarta",
    val postalCode: String = "12180",
    val email: String = "sdn01merdeka@kemdikbud.go.id",
    val headmasterName: String = "Dr. H. Bambang Suryono, M.Pd.",
    val headmasterNip: String = "197204151998031004",
    val teacherName: String = "Siti Rahmawati, S.Pd., M.Pd.",
    val teacherNip: String = "198509122010012023",
    val teacherPhone: String = "6281234567890",
    val className: String = "Kelas VI-A (Enam A)",
    val phase: String = "Fase C (Kelas 5-6)",
    val academicYear: String = "2025/2026",
    val semester: String = "1 (Ganjil)",
    val curriculum: String = "Kurikulum Merdeka (Kemendikdasmen)",
    val placeAndDateOfReport: String = "Jakarta, 20 Desember 2025"
)
