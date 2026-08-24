package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LearningObjective
import com.example.data.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY orderIndex ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects ORDER BY orderIndex ASC")
    suspend fun getAllSubjectsDirect(): List<Subject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>)

    @Query("SELECT * FROM learning_objectives WHERE subjectId = :subjectId ORDER BY tpCode ASC")
    fun getObjectivesBySubject(subjectId: String): Flow<List<LearningObjective>>

    @Query("SELECT * FROM learning_objectives ORDER BY subjectId ASC, tpCode ASC")
    suspend fun getAllObjectivesDirect(): List<LearningObjective>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjectives(objectives: List<LearningObjective>)
}
