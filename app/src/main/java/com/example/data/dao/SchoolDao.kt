package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SchoolProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM school_profile WHERE id = 1")
    fun getSchoolProfile(): Flow<SchoolProfile?>

    @Query("SELECT * FROM school_profile WHERE id = 1")
    suspend fun getSchoolProfileDirect(): SchoolProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: SchoolProfile)

    @Update
    suspend fun update(profile: SchoolProfile)
}
