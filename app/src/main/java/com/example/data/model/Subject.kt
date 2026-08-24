package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey val id: String, // e.g. "PAIBP", "PANCASILA", "BINDO", "MTK", "IPAS", "SBdP", "PJOK", "BING", "MULOK"
    val name: String,
    val shortName: String,
    val category: String = "Wajib",
    val orderIndex: Int = 0,
    val kkm: Int = 75,
    val iconName: String = "menu_book"
)

@Entity(tableName = "learning_objectives")
data class LearningObjective(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: String,
    val tpCode: String, // e.g. "TP 6.1", "TP 6.2"
    val title: String,
    val description: String,
    val semester: Int = 1
)
