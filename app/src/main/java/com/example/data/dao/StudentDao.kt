package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY fullName ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students ORDER BY fullName ASC")
    suspend fun getAllStudentsDirect(): List<Student>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Long): Flow<Student?>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentByIdDirect(id: Long): Student?

    @Query("SELECT * FROM students WHERE qrCodeId = :qrCodeId LIMIT 1")
    suspend fun getStudentByQrCode(qrCodeId: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>

    @Query("DELETE FROM students")
    suspend fun deleteAllStudents()
}
