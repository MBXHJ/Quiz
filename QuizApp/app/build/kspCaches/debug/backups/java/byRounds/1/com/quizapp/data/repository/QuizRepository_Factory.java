package com.quizapp.data.repository;

import com.quizapp.data.db.dao.AnsweredQuestionDao;
import com.quizapp.data.db.dao.ExamRecordDao;
import com.quizapp.data.db.dao.PracticeProgressDao;
import com.quizapp.data.db.dao.PracticeRecordDao;
import com.quizapp.data.db.dao.QuestionBankDao;
import com.quizapp.data.db.dao.QuestionDao;
import com.quizapp.data.db.dao.WrongRecordDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class QuizRepository_Factory implements Factory<QuizRepository> {
  private final Provider<QuestionBankDao> bankDaoProvider;

  private final Provider<QuestionDao> questionDaoProvider;

  private final Provider<WrongRecordDao> wrongRecordDaoProvider;

  private final Provider<ExamRecordDao> examRecordDaoProvider;

  private final Provider<AnsweredQuestionDao> answeredQuestionDaoProvider;

  private final Provider<PracticeProgressDao> practiceProgressDaoProvider;

  private final Provider<PracticeRecordDao> practiceRecordDaoProvider;

  public QuizRepository_Factory(Provider<QuestionBankDao> bankDaoProvider,
      Provider<QuestionDao> questionDaoProvider, Provider<WrongRecordDao> wrongRecordDaoProvider,
      Provider<ExamRecordDao> examRecordDaoProvider,
      Provider<AnsweredQuestionDao> answeredQuestionDaoProvider,
      Provider<PracticeProgressDao> practiceProgressDaoProvider,
      Provider<PracticeRecordDao> practiceRecordDaoProvider) {
    this.bankDaoProvider = bankDaoProvider;
    this.questionDaoProvider = questionDaoProvider;
    this.wrongRecordDaoProvider = wrongRecordDaoProvider;
    this.examRecordDaoProvider = examRecordDaoProvider;
    this.answeredQuestionDaoProvider = answeredQuestionDaoProvider;
    this.practiceProgressDaoProvider = practiceProgressDaoProvider;
    this.practiceRecordDaoProvider = practiceRecordDaoProvider;
  }

  @Override
  public QuizRepository get() {
    return newInstance(bankDaoProvider.get(), questionDaoProvider.get(), wrongRecordDaoProvider.get(), examRecordDaoProvider.get(), answeredQuestionDaoProvider.get(), practiceProgressDaoProvider.get(), practiceRecordDaoProvider.get());
  }

  public static QuizRepository_Factory create(Provider<QuestionBankDao> bankDaoProvider,
      Provider<QuestionDao> questionDaoProvider, Provider<WrongRecordDao> wrongRecordDaoProvider,
      Provider<ExamRecordDao> examRecordDaoProvider,
      Provider<AnsweredQuestionDao> answeredQuestionDaoProvider,
      Provider<PracticeProgressDao> practiceProgressDaoProvider,
      Provider<PracticeRecordDao> practiceRecordDaoProvider) {
    return new QuizRepository_Factory(bankDaoProvider, questionDaoProvider, wrongRecordDaoProvider, examRecordDaoProvider, answeredQuestionDaoProvider, practiceProgressDaoProvider, practiceRecordDaoProvider);
  }

  public static QuizRepository newInstance(QuestionBankDao bankDao, QuestionDao questionDao,
      WrongRecordDao wrongRecordDao, ExamRecordDao examRecordDao,
      AnsweredQuestionDao answeredQuestionDao, PracticeProgressDao practiceProgressDao,
      PracticeRecordDao practiceRecordDao) {
    return new QuizRepository(bankDao, questionDao, wrongRecordDao, examRecordDao, answeredQuestionDao, practiceProgressDao, practiceRecordDao);
  }
}
