package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    fun getAttendanceByDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date = :date")
    suspend fun getAttendanceByDateDirect(date: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId")
    fun getAttendanceByStudent(studentId: Long): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId")
    suspend fun getAttendanceByStudentDirect(studentId: Long): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records")
    fun getAllAttendance(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records")
    suspend fun getAllAttendanceDirect(): List<AttendanceRecord>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Update
    suspend fun updateAttendance(record: AttendanceRecord)

    @Query("DELETE FROM attendance_records WHERE studentId = :studentId AND date = :date")
    suspend fun deleteAttendance(studentId: Long, date: String)

    @Query("DELETE FROM attendance_records")
    suspend fun deleteAllAttendance()
}
