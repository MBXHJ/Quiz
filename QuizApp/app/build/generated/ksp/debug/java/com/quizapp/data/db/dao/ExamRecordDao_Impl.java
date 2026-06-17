package com.quizapp.data.db.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.quizapp.data.db.entity.ExamRecordEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ExamRecordDao_Impl implements ExamRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExamRecordEntity> __insertionAdapterOfExamRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecordById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecordsByBank;

  public ExamRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExamRecordEntity = new EntityInsertionAdapter<ExamRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `exam_records` (`id`,`bankId`,`score`,`totalCount`,`correctCount`,`questionDetails`,`examDate`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExamRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBankId());
        statement.bindLong(3, entity.getScore());
        statement.bindLong(4, entity.getTotalCount());
        statement.bindLong(5, entity.getCorrectCount());
        statement.bindString(6, entity.getQuestionDetails());
        statement.bindLong(7, entity.getExamDate());
      }
    };
    this.__preparedStmtOfDeleteRecordById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM exam_records WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteRecordsByBank = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM exam_records WHERE bankId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRecord(final ExamRecordEntity record,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfExamRecordEntity.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecordById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecordById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRecordById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecordsByBank(final long bankId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecordsByBank.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, bankId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRecordsByBank.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExamRecordEntity>> getRecordsByBank(final long bankId) {
    final String _sql = "SELECT * FROM exam_records WHERE bankId = ? ORDER BY examDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"exam_records"}, new Callable<List<ExamRecordEntity>>() {
      @Override
      @NonNull
      public List<ExamRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfTotalCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfQuestionDetails = CursorUtil.getColumnIndexOrThrow(_cursor, "questionDetails");
          final int _cursorIndexOfExamDate = CursorUtil.getColumnIndexOrThrow(_cursor, "examDate");
          final List<ExamRecordEntity> _result = new ArrayList<ExamRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExamRecordEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpTotalCount;
            _tmpTotalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final String _tmpQuestionDetails;
            _tmpQuestionDetails = _cursor.getString(_cursorIndexOfQuestionDetails);
            final long _tmpExamDate;
            _tmpExamDate = _cursor.getLong(_cursorIndexOfExamDate);
            _item = new ExamRecordEntity(_tmpId,_tmpBankId,_tmpScore,_tmpTotalCount,_tmpCorrectCount,_tmpQuestionDetails,_tmpExamDate);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
