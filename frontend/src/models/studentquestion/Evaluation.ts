export default class Evaluation {
  id!: number;
  accepted!: boolean;
  justification!: string;

  constructor(jsonObj?: Evaluation) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.accepted = jsonObj.accepted;
      this.justification = jsonObj.justification;
    }
  }
}
