package com.quizapp.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.quizapp.data.db.entity.QuestionEntity;
import com.quizapp.data.db.entity.WrongRecordEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class WrongRecordDao_Impl implements WrongRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<WrongRecordEntity> __insertionAdapterOfWrongRecordEntity;

  private final SharedSQLiteStatement __preparedStmtOfRemoveWrongRecord;

  private final SharedSQLiteStatement __preparedStmtOfClearWrongRecordsByBank;

  public WrongRecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfWrongRecordEntity = new EntityInsertionAdapter<WrongRecordEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `wrong_records` (`id`,`questionId`,`wrongCount`,`lastWrongTime`,`isRemoved`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WrongRecordEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getQuestionId());
        statement.bindLong(3, entity.getWrongCount());
        statement.bindLong(4, entity.getLastWrongTime());
        final int _tmp = entity.isRemoved() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__preparedStmtOfRemoveWrongRecord = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE wrong_records SET isRemoved = 1 WHERE questionId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearWrongRecordsByBank = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM wrong_records WHERE questionId IN (SELECT id FROM questions WHERE bankId = ?)";
        return _query;
      }
    };
  }

  @Override
  public Object upsertWrongRecord(final WrongRecordEntity record,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfWrongRecordEntity.insert(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object removeWrongRecord(final long questionId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveWrongRecord.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, questionId);
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
          __preparedStmtOfRemoveWrongRecord.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearWrongRecordsByBank(final long bankId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearWrongRecordsByBank.acquire();
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
          __preparedStmtOfClearWrongRecordsByBank.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<WrongWithQuestion>> getWrongRecordsByBank(final long bankId) {
    final String _sql = "SELECT id, questionId, wrongCount, lastWrongTime, isRemoved FROM wrong_records WHERE questionId IN (SELECT id FROM questions WHERE bankId = ?) AND isRemoved = 0 ORDER BY lastWrongTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions",
        "wrong_records"}, new Callable<List<WrongWithQuestion>>() {
      @Override
      @NonNull
      public List<WrongWithQuestion> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfQuestionId = 1;
          final int _cursorIndexOfWrongCount = 2;
          final int _cursorIndexOfLastWrongTime = 3;
          final int _cursorIndexOfIsRemoved = 4;
          final LongSparseArray<QuestionEntity> _collectionQuestion = new LongSparseArray<QuestionEntity>();
          while (_cursor.moveToNext()) {
            final long _tmpKey;
            _tmpKey = _cursor.getLong(_cursorIndexOfQuestionId);
            _collectionQuestion.put(_tmpKey, null);
          }
          _cursor.moveToPosition(-1);
          __fetchRelationshipquestionsAscomQuizappDataDbEntityQuestionEntity(_collectionQuestion);
          final List<WrongWithQuestion> _result = new ArrayList<WrongWithQuestion>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WrongWithQuestion _item;
            final WrongRecordEntity _tmpRecord;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpQuestionId;
            _tmpQuestionId = _cursor.getLong(_cursorIndexOfQuestionId);
            final int _tmpWrongCount;
            _tmpWrongCount = _cursor.getInt(_cursorIndexOfWrongCount);
            final long _tmpLastWrongTime;
            _tmpLastWrongTime = _cursor.getLong(_cursorIndexOfLastWrongTime);
            final boolean _tmpIsRemoved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRemoved);
            _tmpIsRemoved = _tmp != 0;
            _tmpRecord = new WrongRecordEntity(_tmpId,_tmpQuestionId,_tmpWrongCount,_tmpLastWrongTime,_tmpIsRemoved);
            final QuestionEntity _tmpQuestion;
            final long _tmpKey_1;
            _tmpKey_1 = _cursor.getLong(_cursorIndexOfQuestionId);
            _tmpQuestion = _collectionQuestion.get(_tmpKey_1);
            _item = new WrongWithQuestion(_tmpRecord,_tmpQuestion);
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
  public Object getWrongRecordsByBankOnce(final long bankId,
      final Continuation<? super List<WrongWithQuestion>> $completion) {
    final String _sql = "SELECT id, questionId, wrongCount, lastWrongTime, isRemoved FROM wrong_records WHERE questionId IN (SELECT id FROM questions WHERE bankId = ?) AND isRemoved = 0 ORDER BY lastWrongTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<WrongWithQuestion>>() {
      @Override
      @NonNull
      public List<WrongWithQuestion> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfQuestionId = 1;
          final int _cursorIndexOfWrongCount = 2;
          final int _cursorIndexOfLastWrongTime = 3;
          final int _cursorIndexOfIsRemoved = 4;
          final LongSparseArray<QuestionEntity> _collectionQuestion = new LongSparseArray<QuestionEntity>();
          while (_cursor.moveToNext()) {
            final long _tmpKey;
            _tmpKey = _cursor.getLong(_cursorIndexOfQuestionId);
            _collectionQuestion.put(_tmpKey, null);
          }
          _cursor.moveToPosition(-1);
          __fetchRelationshipquestionsAscomQuizappDataDbEntityQuestionEntity(_collectionQuestion);
          final List<WrongWithQuestion> _result = new ArrayList<WrongWithQuestion>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final WrongWithQuestion _item;
            final WrongRecordEntity _tmpRecord;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpQuestionId;
            _tmpQuestionId = _cursor.getLong(_cursorIndexOfQuestionId);
            final int _tmpWrongCount;
            _tmpWrongCount = _cursor.getInt(_cursorIndexOfWrongCount);
            final long _tmpLastWrongTime;
            _tmpLastWrongTime = _cursor.getLong(_cursorIndexOfLastWrongTime);
            final boolean _tmpIsRemoved;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRemoved);
            _tmpIsRemoved = _tmp != 0;
            _tmpRecord = new WrongRecordEntity(_tmpId,_tmpQuestionId,_tmpWrongCount,_tmpLastWrongTime,_tmpIsRemoved);
            final QuestionEntity _tmpQuestion;
            final long _tmpKey_1;
            _tmpKey_1 = _cursor.getLong(_cursorIndexOfQuestionId);
            _tmpQuestion = _collectionQuestion.get(_tmpKey_1);
            _item = new WrongWithQuestion(_tmpRecord,_tmpQuestion);
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

  @Override
  public Object getWrongCount(final long questionId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT wrongCount FROM wrong_records WHERE questionId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, questionId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getInt(0);
            }
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

  private void __fetchRelationshipquestionsAscomQuizappDataDbEntityQuestionEntity(
      @NonNull final LongSparseArray<QuestionEntity> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipquestionsAscomQuizappDataDbEntityQuestionEntity(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`bankId`,`questionType`,`content`,`options`,`answer`,`analysis` FROM `questions` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfBankId = 1;
      final int _cursorIndexOfQuestionType = 2;
      final int _cursorIndexOfContent = 3;
      final int _cursorIndexOfOptions = 4;
      final int _cursorIndexOfAnswer = 5;
      final int _cursorIndexOfAnalysis = 6;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final QuestionEntity _item_1;
          final long _tmpId;
          _tmpId = _cursor.getLong(_cursorIndexOfId);
          final long _tmpBankId;
          _tmpBankId = _cursor.getLong(_cursorIndexOfBankId);
          final String _tmpQuestionType;
          _tmpQuestionType = _cursor.getString(_cursorIndexOfQuestionType);
          final String _tmpContent;
          _tmpContent = _cursor.getString(_cursorIndexOfContent);
          final String _tmpOptions;
          _tmpOptions = _cursor.getString(_cursorIndexOfOptions);
          final String _tmpAnswer;
          _tmpAnswer = _cursor.getString(_cursorIndexOfAnswer);
          final String _tmpAnalysis;
          _tmpAnalysis = _cursor.getString(_cursorIndexOfAnalysis);
          _item_1 = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}
