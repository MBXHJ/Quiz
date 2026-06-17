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
import com.quizapp.data.db.entity.QuestionBankEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class QuestionBankDao_Impl implements QuestionBankDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QuestionBankEntity> __insertionAdapterOfQuestionBankEntity;

  private final EntityDeletionOrUpdateAdapter<QuestionBankEntity> __deletionAdapterOfQuestionBankEntity;

  private final EntityDeletionOrUpdateAdapter<QuestionBankEntity> __updateAdapterOfQuestionBankEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBankById;

  private final SharedSQLiteStatement __preparedStmtOfUpdateQuestionCount;

  public QuestionBankDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuestionBankEntity = new EntityInsertionAdapter<QuestionBankEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `question_banks` (`id`,`name`,`description`,`questionCount`,`importDate`,`examConfig`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuestionBankEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getQuestionCount());
        statement.bindLong(5, entity.getImportDate());
        statement.bindString(6, entity.getExamConfig());
      }
    };
    this.__deletionAdapterOfQuestionBankEntity = new EntityDeletionOrUpdateAdapter<QuestionBankEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `question_banks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuestionBankEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfQuestionBankEntity = new EntityDeletionOrUpdateAdapter<QuestionBankEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `question_banks` SET `id` = ?,`name` = ?,`description` = ?,`questionCount` = ?,`importDate` = ?,`examConfig` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuestionBankEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindLong(4, entity.getQuestionCount());
        statement.bindLong(5, entity.getImportDate());
        statement.bindString(6, entity.getExamConfig());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteBankById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM question_banks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateQuestionCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE question_banks SET questionCount = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBank(final QuestionBankEntity bank,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfQuestionBankEntity.insertAndReturnId(bank);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBank(final QuestionBankEntity bank,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfQuestionBankEntity.handle(bank);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBank(final QuestionBankEntity bank,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfQuestionBankEntity.handle(bank);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBankById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBankById.acquire();
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
          __preparedStmtOfDeleteBankById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateQuestionCount(final long bankId, final int count,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateQuestionCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, count);
        _argIndex = 2;
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
          __preparedStmtOfUpdateQuestionCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<QuestionBankEntity>> getAllBanks() {
    final String _sql = "SELECT * FROM question_banks ORDER BY importDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"question_banks"}, new Callable<List<QuestionBankEntity>>() {
      @Override
      @NonNull
      public List<QuestionBankEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfQuestionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "questionCount");
          final int _cursorIndexOfImportDate = CursorUtil.getColumnIndexOrThrow(_cursor, "importDate");
          final int _cursorIndexOfExamConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "examConfig");
          final List<QuestionBankEntity> _result = new ArrayList<QuestionBankEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionBankEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpQuestionCount;
            _tmpQuestionCount = _cursor.getInt(_cursorIndexOfQuestionCount);
            final long _tmpImportDate;
            _tmpImportDate = _cursor.getLong(_cursorIndexOfImportDate);
            final String _tmpExamConfig;
            _tmpExamConfig = _cursor.getString(_cursorIndexOfExamConfig);
            _item = new QuestionBankEntity(_tmpId,_tmpName,_tmpDescription,_tmpQuestionCount,_tmpImportDate,_tmpExamConfig);
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
  public Flow<QuestionBankEntity> getBankById(final long id) {
    final String _sql = "SELECT * FROM question_banks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"question_banks"}, new Callable<QuestionBankEntity>() {
      @Override
      @Nullable
      public QuestionBankEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfQuestionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "questionCount");
          final int _cursorIndexOfImportDate = CursorUtil.getColumnIndexOrThrow(_cursor, "importDate");
          final int _cursorIndexOfExamConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "examConfig");
          final QuestionBankEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpQuestionCount;
            _tmpQuestionCount = _cursor.getInt(_cursorIndexOfQuestionCount);
            final long _tmpImportDate;
            _tmpImportDate = _cursor.getLong(_cursorIndexOfImportDate);
            final String _tmpExamConfig;
            _tmpExamConfig = _cursor.getString(_cursorIndexOfExamConfig);
            _result = new QuestionBankEntity(_tmpId,_tmpName,_tmpDescription,_tmpQuestionCount,_tmpImportDate,_tmpExamConfig);
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
  public Object getBankByIdOnce(final long id,
      final Continuation<? super QuestionBankEntity> $completion) {
    final String _sql = "SELECT * FROM question_banks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QuestionBankEntity>() {
      @Override
      @Nullable
      public QuestionBankEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfQuestionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "questionCount");
          final int _cursorIndexOfImportDate = CursorUtil.getColumnIndexOrThrow(_cursor, "importDate");
          final int _cursorIndexOfExamConfig = CursorUtil.getColumnIndexOrThrow(_cursor, "examConfig");
          final QuestionBankEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final int _tmpQuestionCount;
            _tmpQuestionCount = _cursor.getInt(_cursorIndexOfQuestionCount);
            final long _tmpImportDate;
            _tmpImportDate = _cursor.getLong(_cursorIndexOfImportDate);
            final String _tmpExamConfig;
            _tmpExamConfig = _cursor.getString(_cursorIndexOfExamConfig);
            _result = new QuestionBankEntity(_tmpId,_tmpName,_tmpDescription,_tmpQuestionCount,_tmpImportDate,_tmpExamConfig);
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
  public Object getBankCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM question_banks";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
