import TournamentQuiz from '@/models/management/TournamentQuiz';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';

export default class StatementTournamentQuiz {
  tournamentId: number = -1;
  questions: StatementQuestion[] = [];
  answers: StatementAnswer[] = [];
  timeToEnd: number = 0;
  correctAnswers: StatementCorrectAnswer[] = [];

  private static _quiz: StatementTournamentQuiz = new StatementTournamentQuiz();

  static newInstance() {
    this._quiz = new StatementTournamentQuiz();
  }

  static get getInstance(): StatementTournamentQuiz {
    return this._quiz;
  }

  static set quizSetup(quiz: TournamentQuiz) {
    this._quiz.questions = quiz.questions;
    this._quiz.answers = quiz.answers;
    this._quiz.timeToEnd = quiz.timeToEnd;
  }

  static set quizSolution(answers: StatementCorrectAnswer[]) {
    this._quiz.correctAnswers = answers;
  }
}
