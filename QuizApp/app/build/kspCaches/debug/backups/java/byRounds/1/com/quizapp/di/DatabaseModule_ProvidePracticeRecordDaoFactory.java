package com.quizapp.di;

import com.quizapp.data.db.AppDatabase;
import com.quizapp.data.db.dao.PracticeRecordDao;
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
public final class DatabaseModule_ProvidePracticeRecordDaoFactory implements Factory<PracticeRecordDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvidePracticeRecordDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PracticeRecordDao get() {
    return providePracticeRecordDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePracticeRecordDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvidePracticeRecordDaoFactory(dbProvider);
  }

  public static PracticeRecordDao providePracticeRecordDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePracticeRecordDao(db));
  }
}
