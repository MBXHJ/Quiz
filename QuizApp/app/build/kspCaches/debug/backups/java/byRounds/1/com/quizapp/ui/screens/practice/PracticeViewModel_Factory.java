package com.quizapp.ui.screens.practice;

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
public final class PracticeViewModel_Factory implements Factory<PracticeViewModel> {
  private final Provider<QuizRepository> quizRepositoryProvider;

  public PracticeViewModel_Factory(Provider<QuizRepository> quizRepositoryProvider) {
    this.quizRepositoryProvider = quizRepositoryProvider;
  }

  @Override
  public PracticeViewModel get() {
    return newInstance(quizRepositoryProvider.get());
  }

  public static PracticeViewModel_Factory create(Provider<QuizRepository> quizRepositoryProvider) {
    return new PracticeViewModel_Factory(quizRepositoryProvider);
  }

  public static PracticeViewModel newInstance(QuizRepository quizRepository) {
    return new PracticeViewModel(quizRepository);
  }
}
