package com.quizapp.ui.screens.profile;

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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<QuizRepository> quizRepositoryProvider;

  public ProfileViewModel_Factory(Provider<QuizRepository> quizRepositoryProvider) {
    this.quizRepositoryProvider = quizRepositoryProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(quizRepositoryProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<QuizRepository> quizRepositoryProvider) {
    return new ProfileViewModel_Factory(quizRepositoryProvider);
  }

  public static ProfileViewModel newInstance(QuizRepository quizRepository) {
    return new ProfileViewModel(quizRepository);
  }
}
