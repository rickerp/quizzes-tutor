export default class StudentQuestionDashboard {
  name: string | undefined;
  accepted!: number;
  total!: number;

  constructor(jsonObj?: StudentQuestionDashboard) {
    if (jsonObj) {
      this.name = jsonObj.name;
      this.accepted = jsonObj.accepted;
      this.total = jsonObj.total;
    }
  }
}
