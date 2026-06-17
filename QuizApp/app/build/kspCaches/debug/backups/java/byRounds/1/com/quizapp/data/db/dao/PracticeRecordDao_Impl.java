package com.quizapp.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.quizapp.data.db.entity.PracticeRecordEntity;
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
public final class PracticeRecordDao_Impl implements PracticeRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PracticeRecordEntity> __insertionAdapterOfPracticeRecordEntity;

  private final EntityDeletionOrUpdateAdapter<PracticeRecordEntity> __updateAdapterOfPracticeRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecordById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecordsByBank;

  public PracticeRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPracticeRecordEntity = new EntityInsertionAdapter<PracticeRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `practice_records` (`id`,`bankId`,`mode`,`modeLabel`,`totalCount`,`answeredCount`,`correctCount`,`wrongCount`,`isCompleted`,`startTime`,`endTime`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PracticeRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBankId());
        statement.bindString(3, entity.getMode());
        statement.bindString(4, entity.getModeLabel());
        statement.bindLong(5, entity.getTotalCount());
        statement.bindLong(6, entity.getAnsweredCount());
        statement.bindLong(7, entity.getCorrectCount());
        statement.bindLong(8, entity.getWrongCount());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getStartTime());
        statement.bindLong(11, entity.getEndTime());
      }
    };
    this.__updateAdapterOfPracticeRecordEntity = new EntityDeletionOrUpdateAdapter<PracticeRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `practice_records` SET `id` = ?,`bankId` = ?,`mode` = ?,`modeLabel` = ?,`totalCount` = ?,`answeredCount` = ?,`correctCount` = ?,`wrongCount` = ?,`isCompleted` = ?,`startTime` = ?,`endTime` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PracticeRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBankId());
        statement.bindString(3, entity.getMode());
        statement.bindString(4, entity.getModeLabel());
        statement.bindLong(5, entity.getTotalCount());
        statement.bindLong(6, entity.getAnsweredCount());
        statement.bindLong(7, entity.getCorrectCount());
        statement.bindLong(8, entity.getWrongCount());
        final int _tmp = entity.isCompleted() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindLong(10, entity.getStartTime());
        statement.bindLong(11, entity.getEndTime());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteRecordById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM practice_records WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteRecordsByBank = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM practice_records WHERE bankId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertRecord(final PracticeRecordEntity record,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPracticeRecordEntity.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRecord(final PracticeRecordEntity record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPracticeRecordEntity.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
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
  public Flow<List<PracticeRecordEntity>> getRecordsByBank(final long bankId) {
    final String _sql = "SELECT * FROM practice_records WHERE bankId = ? ORDER BY endTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"practice_records"}, new Callable<List<PracticeRecordEntity>>() {
      @Override
      @NonNull
      public List<PracticeRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfModeLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "modeLabel");
          final int _cursorIndexOfTotalCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCount");
          final int _cursorIndexOfAnsweredCount = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfWrongCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wrongCount");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final List<PracticeRecordEntity> _result = new ArrayList<PracticeRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PracticeRecordEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final String _tmpModeLabel;
            _tmpModeLabel = _cursor.getString(_cursorIndexOfModeLabel);
            final int _tmpTotalCount;
            _tmpTotalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            final int _tmpAnsweredCount;
            _tmpAnsweredCount = _cursor.getInt(_cursorIndexOfAnsweredCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final int _tmpWrongCount;
            _tmpWrongCount = _cursor.getInt(_cursorIndexOfWrongCount);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            _item = new PracticeRecordEntity(_tmpId,_tmpBankId,_tmpMode,_tmpModeLabel,_tmpTotalCount,_tmpAnsweredCount,_tmpCorrectCount,_tmpWrongCount,_tmpIsCompleted,_tmpStartTime,_tmpEndTime);
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
  public Flow<List<PracticeRecordEntity>> getRecentRecords(final int limit) {
    final String _sql = "SELECT * FROM practice_records ORDER BY endTime DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"practice_records"}, new Callable<List<PracticeRecordEntity>>() {
      @Override
      @NonNull
      public List<PracticeRecordEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfModeLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "modeLabel");
          final int _cursorIndexOfTotalCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCount");
          final int _cursorIndexOfAnsweredCount = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfWrongCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wrongCount");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final List<PracticeRecordEntity> _result = new ArrayList<PracticeRecordEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PracticeRecordEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final String _tmpModeLabel;
            _tmpModeLabel = _cursor.getString(_cursorIndexOfModeLabel);
            final int _tmpTotalCount;
            _tmpTotalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            final int _tmpAnsweredCount;
            _tmpAnsweredCount = _cursor.getInt(_cursorIndexOfAnsweredCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final int _tmpWrongCount;
            _tmpWrongCount = _cursor.getInt(_cursorIndexOfWrongCount);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            _item = new PracticeRecordEntity(_tmpId,_tmpBankId,_tmpMode,_tmpModeLabel,_tmpTotalCount,_tmpAnsweredCount,_tmpCorrectCount,_tmpWrongCount,_tmpIsCompleted,_tmpStartTime,_tmpEndTime);
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
  public Object getRecordById(final long id,
      final Continuation<? super PracticeRecordEntity> $completion) {
    final String _sql = "SELECT * FROM practice_records WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PracticeRecordEntity>() {
      @Override
      @Nullable
      public PracticeRecordEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfMode = CursorUtil.getColumnIndexOrThrow(_cursor, "mode");
          final int _cursorIndexOfModeLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "modeLabel");
          final int _cursorIndexOfTotalCount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalCount");
          final int _cursorIndexOfAnsweredCount = CursorUtil.getColumnIndexOrThrow(_cursor, "answeredCount");
          final int _cursorIndexOfCorrectCount = CursorUtil.getColumnIndexOrThrow(_cursor, "correctCount");
          final int _cursorIndexOfWrongCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wrongCount");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfStartTime = CursorUtil.getColumnIndexOrThrow(_cursor, "startTime");
          final int _cursorIndexOfEndTime = CursorUtil.getColumnIndexOrThrow(_cursor, "endTime");
          final PracticeRecordEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpBankId;
            _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
            final String _tmpMode;
            _tmpMode = _cursor.getString(_cursorIndexOfMode);
            final String _tmpModeLabel;
            _tmpModeLabel = _cursor.getString(_cursorIndexOfModeLabel);
            final int _tmpTotalCount;
            _tmpTotalCount = _cursor.getInt(_cursorIndexOfTotalCount);
            final int _tmpAnsweredCount;
            _tmpAnsweredCount = _cursor.getInt(_cursorIndexOfAnsweredCount);
            final int _tmpCorrectCount;
            _tmpCorrectCount = _cursor.getInt(_cursorIndexOfCorrectCount);
            final int _tmpWrongCount;
            _tmpWrongCount = _cursor.getInt(_cursorIndexOfWrongCount);
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final long _tmpStartTime;
            _tmpStartTime = _cursor.getLong(_cursorIndexOfStartTime);
            final long _tmpEndTime;
            _tmpEndTime = _cursor.getLong(_cursorIndexOfEndTime);
            _result = new PracticeRecordEntity(_tmpId,_tmpBankId,_tmpMode,_tmpModeLabel,_tmpTotalCount,_tmpAnsweredCount,_tmpCorrectCount,_tmpWrongCount,_tmpIsCompleted,_tmpStartTime,_tmpEndTime);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
