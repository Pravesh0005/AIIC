package com.aiic.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO contract for Resumes.
 * Prepared for future offline-first architecture.
 */
@Dao
interface ResumeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResumes(resumes: List<ResumeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResume(resume: ResumeEntity)

    @Query("SELECT * FROM resumes WHERE userId = :userId ORDER BY resumeVersion DESC")
    fun observeResumes(userId: String): Flow<List<ResumeEntity>>

    @Query("SELECT * FROM resumes WHERE userId = :userId ORDER BY resumeVersion DESC")
    suspend fun getResumes(userId: String): List<ResumeEntity>

    @Query("SELECT * FROM resumes WHERE resumeId = :resumeId")
    suspend fun getResumeById(resumeId: String): ResumeEntity?

    @Query("SELECT * FROM resumes WHERE userId = :userId AND activeResume = 1 LIMIT 1")
    suspend fun getActiveResume(userId: String): ResumeEntity?

    @Query("DELETE FROM resumes WHERE resumeId = :resumeId")
    suspend fun deleteResume(resumeId: String)

    @Query("UPDATE resumes SET activeResume = 0 WHERE userId = :userId")
    suspend fun deactivateAllResumes(userId: String)
    
    @Query("UPDATE resumes SET activeResume = 1 WHERE resumeId = :resumeId")
    suspend fun activateResume(resumeId: String)
    
    @Query("DELETE FROM resumes")
    suspend fun clearAll()
}
