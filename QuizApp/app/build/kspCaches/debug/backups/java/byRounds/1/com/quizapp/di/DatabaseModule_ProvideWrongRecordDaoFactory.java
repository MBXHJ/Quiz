package com.quizapp.di;

import com.quizapp.data.db.AppDatabase;
import com.quizapp.data.db.dao.WrongRecordDao;
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
public final class DatabaseModule_ProvideWrongRecordDaoFactory implements Factory<WrongRecordDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideWrongRecordDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public WrongRecordDao get() {
    return provideWrongRecordDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideWrongRecordDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideWrongRecordDaoFactory(dbProvider);
  }

  public static WrongRecordDao provideWrongRecordDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideWrongRecordDao(db));
  }
}
