package com.quizapp.ui.screens.exam;

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
public final class ExamViewModel_Factory implements Factory<ExamViewModel> {
  private final Provider<QuizRepository> quizRepositoryProvider;

  public ExamViewModel_Factory(Provider<QuizRepository> quizRepositoryProvider) {
    this.quizRepositoryProvider = quizRepositoryProvider;
  }

  @Override
  public ExamViewModel get() {
    return newInstance(quizRepositoryProvider.get());
  }

  public static ExamViewModel_Factory create(Provider<QuizRepository> quizRepositoryProvider) {
    return new ExamViewModel_Factory(quizRepositoryProvider);
  }

  public static ExamViewModel newInstance(QuizRepository quizRepository) {
    return new ExamViewModel(quizRepository);
  }
}
