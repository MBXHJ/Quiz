package com.quizapp.ui.screens.question;

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
public final class QuestionViewModel_Factory implements Factory<QuestionViewModel> {
  private final Provider<QuizRepository> quizRepositoryProvider;

  public QuestionViewModel_Factory(Provider<QuizRepository> quizRepositoryProvider) {
    this.quizRepositoryProvider = quizRepositoryProvider;
  }

  @Override
  public QuestionViewModel get() {
    return newInstance(quizRepositoryProvider.get());
  }

  public static QuestionViewModel_Factory create(Provider<QuizRepository> quizRepositoryProvider) {
    return new QuestionViewModel_Factory(quizRepositoryProvider);
  }

  public static QuestionViewModel newInstance(QuizRepository quizRepository) {
    return new QuestionViewModel(quizRepository);
  }
}
