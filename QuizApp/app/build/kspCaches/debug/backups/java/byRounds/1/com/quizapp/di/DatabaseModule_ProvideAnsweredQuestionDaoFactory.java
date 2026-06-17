package com.quizapp.di;

import com.quizapp.data.db.AppDatabase;
import com.quizapp.data.db.dao.AnsweredQuestionDao;
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
public final class DatabaseModule_ProvideAnsweredQuestionDaoFactory implements Factory<AnsweredQuestionDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideAnsweredQuestionDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AnsweredQuestionDao get() {
    return provideAnsweredQuestionDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideAnsweredQuestionDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideAnsweredQuestionDaoFactory(dbProvider);
  }

  public static AnsweredQuestionDao provideAnsweredQuestionDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideAnsweredQuestionDao(db));
  }
}
