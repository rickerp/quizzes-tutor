import Question from '@/models/management/Question';

export default class StudentQuestion {
  id!: number;
  question!: Question;
  evaluation!: any;
  student!: number;
  constructor(jsonObj?: StudentQuestion) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.evaluation = jsonObj.evaluation;
      this.question = new Question(jsonObj.question);
      this.student = jsonObj.student;
    }
  }
}
