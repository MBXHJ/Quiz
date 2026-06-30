package com.quizapp.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.data.db.entity.QuestionTagEntity
import com.quizapp.data.db.entity.TagEntity
import kotlinx.coroutines.flow.Flow

data class TagQuestionCount(
    val tagId: Long,
    val count: Int
)

@Dao
interface QuestionTagDao {

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN question_tags qt ON t.id = qt.tagId
        WHERE qt.questionId = :questionId
        ORDER BY t.name
    """)
    fun getTagsForQuestion(questionId: Long): Flow<List<TagEntity>>

    @Query("""
        SELECT t.* FROM tags t
        INNER JOIN question_tags qt ON t.id = qt.tagId
        WHERE qt.questionId = :questionId
        ORDER BY t.name
    """)
    suspend fun getTagsForQuestionOnce(questionId: Long): List<TagEntity>

    @Query("SELECT tagId FROM question_tags WHERE questionId = :questionId")
    suspend fun getTagIdsForQuestion(questionId: Long): List<Long>

    @Query("""
        SELECT DISTINCT q.* FROM questions q
        INNER JOIN question_tags qt ON q.id = qt.questionId
        WHERE qt.tagId IN (:tagIds) AND q.bankId = :bankId
    """)
    suspend fun getQuestionsForTags(tagIds: List<Long>, bankId: Long): List<com.quizapp.data.db.entity.QuestionEntity>

    @Query("""
        SELECT tagId, COUNT(*) as `count` FROM question_tags
        WHERE questionId IN (SELECT id FROM questions WHERE bankId = :bankId)
        GROUP BY tagId
    """)
    suspend fun getTagQuestionCounts(bankId: Long): List<TagQuestionCount>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTagToQuestion(qt: QuestionTagEntity)

    @Query("DELETE FROM question_tags WHERE questionId = :questionId AND tagId = :tagId")
    suspend fun removeTagFromQuestion(questionId: Long, tagId: Long)

    @Query("DELETE FROM question_tags WHERE tagId = :tagId")
    suspend fun removeAllForTag(tagId: Long)
}
