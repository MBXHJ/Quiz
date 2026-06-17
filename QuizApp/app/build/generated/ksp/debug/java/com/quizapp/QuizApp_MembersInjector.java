package com.quizapp;

import com.quizapp.data.repository.ImportRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class QuizApp_MembersInjector implements MembersInjector<QuizApp> {
  private final Provider<ImportRepository> importRepositoryProvider;

  public QuizApp_MembersInjector(Provider<ImportRepository> importRepositoryProvider) {
    this.importRepositoryProvider = importRepositoryProvider;
  }

  public static MembersInjector<QuizApp> create(
      Provider<ImportRepository> importRepositoryProvider) {
    return new QuizApp_MembersInjector(importRepositoryProvider);
  }

  @Override
  public void injectMembers(QuizApp instance) {
    injectImportRepository(instance, importRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.quizapp.QuizApp.importRepository")
  public static void injectImportRepository(QuizApp instance, ImportRepository importRepository) {
    instance.importRepository = importRepository;
  }
}
