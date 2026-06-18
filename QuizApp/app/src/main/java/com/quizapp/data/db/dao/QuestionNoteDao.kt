package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.QuestionNoteEntity

@Dao
interface QuestionNoteDao {
    @Query("SELECT * FROM question_notes WHERE questionId = :questionId LIMIT 1")
    suspend fun getNote(questionId: Long): QuestionNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: QuestionNoteEntity)

    @Query("DELETE FROM question_notes WHERE questionId = :questionId")
    suspend fun deleteNote(questionId: Long)
}
