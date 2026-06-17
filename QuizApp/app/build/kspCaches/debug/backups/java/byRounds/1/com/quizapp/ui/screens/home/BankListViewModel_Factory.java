package com.quizapp.ui.screens.home;

import com.quizapp.data.repository.QuizRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class BankListViewModel_Factory implements Factory<BankListViewModel> {
  private final Provider<QuizRepository> quizRepositoryProvider;

  public BankListViewModel_Factory(Provider<QuizRepository> quizRepositoryProvider) {
    this.quizRepositoryProvider = quizRepositoryProvider;
  }

  @Override
  public BankListViewModel get() {
    return newInstance(quizRepositoryProvider.get());
  }

  public static BankListViewModel_Factory create(Provider<QuizRepository> quizRepositoryProvider) {
    return new BankListViewModel_Factory(quizRepositoryProvider);
  }

  public static BankListViewModel newInstance(QuizRepository quizRepository) {
    return new BankListViewModel(quizRepository);
  }
}
