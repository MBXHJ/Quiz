package com.quizapp.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.quizapp.data.db.entity.PracticeProgressEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class PracticeProgressDao_Impl implements PracticeProgressDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PracticeProgressEntity> __insertionAdapterOfPracticeProgressEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearProgress;

  private final SharedSQLiteStatement __preparedStmtOfClearAllProgressByBank;

  public PracticeProgressDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPracticeProgressEntity = new EntityInsertionAdapter<PracticeProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `practice_progress` (`id`,`bankId`,`mode`,`currentIndex`,`totalQuestions`,`answeredCount`,`correctCount`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PracticeProgressEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBankId());
        statement.bindString(3, entity.getMode());
        statement.bindLong(4, entity.getCurrentIndex());
        statement.bindLong(5, entity.getTotalQuestions());
        statement.bindLong(6, entity.getAnsweredCount());
        statement.bindLong(7, entity.getCorrectCount());
        statement.bindLong(8, entity.getUpdatedAt());
      }
    };
    this.__preparedStmtOfClearProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM practice_progress WHERE bankId = ? AND mode = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearAllProgressByBank = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM practice_progress WHERE bankId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object saveProgress(final PracticeProgressEntity progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPracticeProgressEntity.insert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearProgress(final long bankId, final String mode,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, bankId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, mode);
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
          __preparedStmtOfClearProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllProgressByBank(final long bankId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllProgressByBank.acquire();
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
          __preparedStmtOfClearAllProgressByBank.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getProgress(final long bankId, final String mode,
      final Continuation<? super PracticeProgressEntity> $completion) {
    final String _sql = "SELECT * FROM practice_progress WHERE bankId = ? AND mode = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    _argIndex = 2;
    _statement.bindString(_argIndex, mode);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PracticeProgressEntity>() {
      @Override
      @Nullable
      public PracticeProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCurrentIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "currentIndex");
          final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
          final int _cursorIndexOfAnsweredCount = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final PracticeProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final int _tmpCurrentIndex;
            _tmpCurrentIndex = _cursor.getInt(_cursorIndexOfCurrentIndex);
            final int _tmpTotalQuestions;
            _tmpTotalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final int _tmpAnsweredCount;
            _tmpAnsweredCount = _cursor.getInt(_cursorIndexOfAnsweredCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new PracticeProgressEntity(_tmpId,_tmpBankId,_tmpMode,_tmpCurrentIndex,_tmpTotalQuestions,_tmpAnsweredCount,_tmpCorrectCount,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<PracticeProgressEntity> getProgressFlow(final long bankId, final String mode) {
    final String _sql = "SELECT * FROM practice_progress WHERE bankId = ? AND mode = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    _argIndex = 2;
    _statement.bindString(_argIndex, mode);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"practice_progress"}, new Callable<PracticeProgressEntity>() {
      @Override
      @Nullable
      public PracticeProgressEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCurrentIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "currentIndex");
          final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
          final int _cursorIndexOfAnsweredCount = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final PracticeProgressEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final int _tmpCurrentIndex;
            _tmpCurrentIndex = _cursor.getInt(_cursorIndexOfCurrentIndex);
            final int _tmpTotalQuestions;
            _tmpTotalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final int _tmpAnsweredCount;
            _tmpAnsweredCount = _cursor.getInt(_cursorIndexOfAnsweredCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new PracticeProgressEntity(_tmpId,_tmpBankId,_tmpMode,_tmpCurrentIndex,_tmpTotalQuestions,_tmpAnsweredCount,_tmpCorrectCount,_tmpUpdatedAt);
          } else {
            _result = null;
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

  @Override
  public Flow<List<PracticeProgressEntity>> getAllProgressByBank(final long bankId) {
    final String _sql = "SELECT * FROM practice_progress WHERE bankId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"practice_progress"}, new Callable<List<PracticeProgressEntity>>() {
      @Override
      @NonNull
      public List<PracticeProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCurrentIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "currentIndex");
          final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
          final int _cursorIndexOfAnsweredCount = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<PracticeProgressEntity> _result = new ArrayList<PracticeProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PracticeProgressEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final int _tmpCurrentIndex;
            _tmpCurrentIndex = _cursor.getInt(_cursorIndexOfCurrentIndex);
            final int _tmpTotalQuestions;
            _tmpTotalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final int _tmpAnsweredCount;
            _tmpAnsweredCount = _cursor.getInt(_cursorIndexOfAnsweredCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new PracticeProgressEntity(_tmpId,_tmpBankId,_tmpMode,_tmpCurrentIndex,_tmpTotalQuestions,_tmpAnsweredCount,_tmpCorrectCount,_tmpUpdatedAt);
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

  @Override
  public Object getAllProgressByBankOnce(final long bankId,
      final Continuation<? super List<PracticeProgressEntity>> $completion) {
    final String _sql = "SELECT * FROM practice_progress WHERE bankId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PracticeProgressEntity>>() {
      @Override
      @NonNull
      public List<PracticeProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfCurrentIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "currentIndex");
          final int _cursorIndexOfTotalQuestions = CursorUtil.getColumnIndexOrThrow(_cursor, "totalQuestions");
          final int _cursorIndexOfAnsweredCount = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<PracticeProgressEntity> _result = new ArrayList<PracticeProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PracticeProgressEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final int _tmpCurrentIndex;
            _tmpCurrentIndex = _cursor.getInt(_cursorIndexOfCurrentIndex);
            final int _tmpTotalQuestions;
            _tmpTotalQuestions = _cursor.getInt(_cursorIndexOfTotalQuestions);
            final int _tmpAnsweredCount;
            _tmpAnsweredCount = _cursor.getInt(_cursorIndexOfAnsweredCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new PracticeProgressEntity(_tmpId,_tmpBankId,_tmpMode,_tmpCurrentIndex,_tmpTotalQuestions,_tmpAnsweredCount,_tmpCorrectCount,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
