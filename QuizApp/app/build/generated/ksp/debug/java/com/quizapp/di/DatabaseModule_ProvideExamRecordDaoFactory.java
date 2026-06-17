package com.quizapp.di;

import com.quizapp.data.db.AppDatabase;
import com.quizapp.data.db.dao.ExamRecordDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideExamRecordDaoFactory implements Factory<ExamRecordDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideExamRecordDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ExamRecordDao get() {
    return provideExamRecordDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideExamRecordDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideExamRecordDaoFactory(dbProvider);
  }

  public static ExamRecordDao provideExamRecordDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideExamRecordDao(db));
  }
}
