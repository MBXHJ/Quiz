package com.quizapp.data.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.quizapp.data.db.entity.QuestionEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class QuestionDao_Impl implements QuestionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QuestionEntity> __insertionAdapterOfQuestionEntity;

  public QuestionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuestionEntity = new EntityInsertionAdapter<QuestionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `questions` (`id`,`bankId`,`questionType`,`content`,`options`,`answer`,`analysis`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuestionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBankId());
        statement.bindString(3, entity.getQuestionType());
        statement.bindString(4, entity.getContent());
        statement.bindString(5, entity.getOptions());
        statement.bindString(6, entity.getAnswer());
        statement.bindString(7, entity.getAnalysis());
      }
    };
  }

  @Override
  public Object insertQuestion(final QuestionEntity question,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfQuestionEntity.insertAndReturnId(question);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuestions(final List<QuestionEntity> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuestionEntity.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<QuestionEntity>> getQuestionsByBank(final long bankId) {
    final String _sql = "SELECT * FROM questions WHERE bankId = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
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
            _item = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
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
  public Flow<List<QuestionEntity>> getQuestionsByType(final long bankId, final String type) {
    final String _sql = "SELECT * FROM questions WHERE bankId = ? AND questionType = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    _argIndex = 2;
    _statement.bindString(_argIndex, type);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
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
            _item = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
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
  public Flow<List<QuestionEntity>> getQuestionsByIds(final List<Long> ids) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM questions WHERE id IN (");
    final int _inputSize = ids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (long _item : ids) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
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
            _result.add(_item_1);
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
  public Object getQuestionsByIdsOnce(final List<Long> ids,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM questions WHERE id IN (");
    final int _inputSize = ids.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (long _item : ids) {
      _statement.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
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
            _result.add(_item_1);
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
  public Object getQuestionById(final long id,
      final Continuation<? super QuestionEntity> $completion) {
    final String _sql = "SELECT * FROM questions WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QuestionEntity>() {
      @Override
      @Nullable
      public QuestionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final QuestionEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
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
  public Flow<Integer> getCountByBank(final long bankId) {
    final String _sql = "SELECT COUNT(*) FROM questions WHERE bankId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<Integer>() {
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
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getRandomQuestionsByType(final long bankId, final String type, final int limit,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE bankId = ? AND questionType = ? ORDER BY RANDOM() LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    _argIndex = 2;
    _statement.bindString(_argIndex, type);
    _argIndex = 3;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
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
            _item = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
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
  public Object getRandomQuestions(final long bankId, final int limit,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE bankId = ? ORDER BY RANDOM() LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
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
            _item = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
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
  public Object getAllQuestionsRandom(final long bankId,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE bankId = ? ORDER BY RANDOM()";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
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
            _item = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
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
  public Object getAllQuestionsOnce(final long bankId,
      final Continuation<? super List<QuestionEntity>> $completion) {
    final String _sql = "SELECT * FROM questions WHERE bankId = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, bankId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<QuestionEntity>>() {
      @Override
      @NonNull
      public List<QuestionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfBankId = CursorUtil.getColumnIndexOrThrow(_cursor, "bankId");
          final int _cursorIndexOfQuestionType = CursorUtil.getColumnIndexOrThrow(_cursor, "questionType");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfOptions = CursorUtil.getColumnIndexOrThrow(_cursor, "options");
          final int _cursorIndexOfAnswer = CursorUtil.getColumnIndexOrThrow(_cursor, "answer");
          final int _cursorIndexOfAnalysis = CursorUtil.getColumnIndexOrThrow(_cursor, "analysis");
          final List<QuestionEntity> _result = new ArrayList<QuestionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuestionEntity _item;
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
            _item = new QuestionEntity(_tmpId,_tmpBankId,_tmpQuestionType,_tmpContent,_tmpOptions,_tmpAnswer,_tmpAnalysis);
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
