package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.FormativeScore
import com.example.data.model.SummativeScore
import kotlinx.coroutines.flow.Flow

@Dao
interface AssessmentDao {
    // Formative (Harian per TP)
    @Query("SELECT * FROM formative_scores WHERE studentId = :studentId AND subjectId = :subjectId")
    fun getFormativeScores(studentId: Long, subjectId: String): Flow<List<FormativeScore>>

    @Query("SELECT * FROM formative_scores WHERE studentId = :studentId")
    suspend fun getAllFormativeForStudent(studentId: Long): List<FormativeScore>

    @Query("SELECT * FROM formative_scores WHERE subjectId = :subjectId")
    fun getFormativeBySubject(subjectId: String): Flow<List<FormativeScore>>

    @Query("SELECT * FROM formative_scores")
    suspend fun getAllFormativeScoresDirect(): List<FormativeScore>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFormativeScore(score: FormativeScore)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFormative(scores: List<FormativeScore>)

    // Summative (Ulangan Harian 1, 2, 3, STS, SAS)
    @Query("SELECT * FROM summative_scores WHERE studentId = :studentId AND subjectId = :subjectId")
    fun getSummativeScores(studentId: Long, subjectId: String): Flow<List<SummativeScore>>

    @Query("SELECT * FROM summative_scores WHERE studentId = :studentId")
    suspend fun getAllSummativeForStudent(studentId: Long): List<SummativeScore>

    @Query("SELECT * FROM summative_scores WHERE subjectId = :subjectId")
    fun getSummativeBySubject(subjectId: String): Flow<List<SummativeScore>>

    @Query("SELECT * FROM summative_scores")
    suspend fun getAllSummativeScoresDirect(): List<SummativeScore>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummativeScore(score: SummativeScore)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSummative(scores: List<SummativeScore>)

    @Query("DELETE FROM formative_scores")
    suspend fun deleteAllFormative()

    @Query("DELETE FROM summative_scores")
    suspend fun deleteAllSummative()
}
