package com.quizapp.di;

import com.quizapp.data.db.AppDatabase;
import com.quizapp.data.db.dao.QuestionBankDao;
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
public final class DatabaseModule_ProvideQuestionBankDaoFactory implements Factory<QuestionBankDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideQuestionBankDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public QuestionBankDao get() {
    return provideQuestionBankDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideQuestionBankDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideQuestionBankDaoFactory(dbProvider);
  }

  public static QuestionBankDao provideQuestionBankDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideQuestionBankDao(db));
  }
}
