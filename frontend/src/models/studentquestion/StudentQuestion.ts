import Question from '@/models/management/Question';
import Evaluation from '@/models/studentquestion/Evaluation';

export default class StudentQuestion {
  id!: number;
  question!: Question;
  evaluation!: Evaluation;
  student!: number;
  constructor(jsonObj?: StudentQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;

      if (jsonObj.evaluation)
        this.evaluation = new Evaluation(jsonObj.evaluation);
      this.question = new Question(jsonObj.question);
      this.student = jsonObj.student;
    }
  }
}
