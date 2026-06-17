package com.quizapp.di;

import com.quizapp.data.db.AppDatabase;
import com.quizapp.data.db.dao.PracticeProgressDao;
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
public final class DatabaseModule_ProvidePracticeProgressDaoFactory implements Factory<PracticeProgressDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvidePracticeProgressDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PracticeProgressDao get() {
    return providePracticeProgressDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePracticeProgressDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvidePracticeProgressDaoFactory(dbProvider);
  }

  public static PracticeProgressDao providePracticeProgressDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePracticeProgressDao(db));
  }
}
