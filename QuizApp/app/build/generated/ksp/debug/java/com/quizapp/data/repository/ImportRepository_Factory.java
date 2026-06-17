package com.quizapp.data.repository;

import android.content.Context;
import com.quizapp.data.db.dao.QuestionBankDao;
import com.quizapp.data.db.dao.QuestionDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class ImportRepository_Factory implements Factory<ImportRepository> {
  private final Provider<Context> contextProvider;

  private final Provider<QuestionBankDao> bankDaoProvider;

  private final Provider<QuestionDao> questionDaoProvider;

  public ImportRepository_Factory(Provider<Context> contextProvider,
      Provider<QuestionBankDao> bankDaoProvider, Provider<QuestionDao> questionDaoProvider) {
    this.contextProvider = contextProvider;
    this.bankDaoProvider = bankDaoProvider;
    this.questionDaoProvider = questionDaoProvider;
  }

  @Override
  public ImportRepository get() {
    return newInstance(contextProvider.get(), bankDaoProvider.get(), questionDaoProvider.get());
  }

  public static ImportRepository_Factory create(Provider<Context> contextProvider,
      Provider<QuestionBankDao> bankDaoProvider, Provider<QuestionDao> questionDaoProvider) {
    return new ImportRepository_Factory(contextProvider, bankDaoProvider, questionDaoProvider);
  }

  public static ImportRepository newInstance(Context context, QuestionBankDao bankDao,
      QuestionDao questionDao) {
    return new ImportRepository(context, bankDao, questionDao);
  }
}
