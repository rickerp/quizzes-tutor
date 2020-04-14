export default class Tournament {
  id!: number;
  name!: string;
  creatorId!: number;
  creatorName!: string;
  playersId!: number[];
  startTime!: string;
  endTime!: string;
  nrQuestions!: number;
  topicsName!: string[];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.creatorId = jsonObj.creatorId;
      this.creatorName = jsonObj.creatorName;
      this.playersId = jsonObj.playersId;
      this.startTime = jsonObj.startTime;
      this.endTime = jsonObj.endTime;
      this.nrQuestions = jsonObj.nrQuestions;
      this.topicsName = jsonObj.topicsName;
    }
  }
}
