package com.quizapp.ui.screens.importt;

import com.quizapp.data.repository.ImportRepository;
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
public final class ImportViewModel_Factory implements Factory<ImportViewModel> {
  private final Provider<ImportRepository> importRepositoryProvider;

  public ImportViewModel_Factory(Provider<ImportRepository> importRepositoryProvider) {
    this.importRepositoryProvider = importRepositoryProvider;
  }

  @Override
  public ImportViewModel get() {
    return newInstance(importRepositoryProvider.get());
  }

  public static ImportViewModel_Factory create(
      Provider<ImportRepository> importRepositoryProvider) {
    return new ImportViewModel_Factory(importRepositoryProvider);
  }

  public static ImportViewModel newInstance(ImportRepository importRepository) {
    return new ImportViewModel(importRepository);
  }
}
