import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';

export default class TournamentQuiz {
  questions: StatementQuestion[] = [];
  answers: StatementAnswer[] = [];
  timeToEnd!: number;

  constructor(jsonObj?: TournamentQuiz) {
    if (jsonObj) {
      this.timeToEnd = jsonObj.timeToEnd;

      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: StatementQuestion) => new StatementQuestion(question)
        );
      }
      if (jsonObj.answers) {
        this.answers = jsonObj.answers.map(
          (answer: StatementAnswer) => new StatementAnswer(answer)
        );
      }
    }
  }
}
