import { ISOtoString } from '@/services/ConvertDateService';

export default class Tournament {
  id!: number;
  name!: string;
  creatorId!: number;
  creatorName!: string;
  playersId!: number[];
  startTime!: string;
  endTime!: string;
  nrQuestions!: number;
  topicsId!: number[];
  topicsName!: string[];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.creatorId = jsonObj.creatorId;
      this.creatorName = jsonObj.creatorName;
      this.playersId = jsonObj.playersId;
      this.nrQuestions = jsonObj.nrQuestions;
      this.topicsId = jsonObj.topicsId;
      this.topicsName = jsonObj.topicsName;

      if (jsonObj.startTime) this.startTime = ISOtoString(jsonObj.startTime);
      if (jsonObj.endTime) this.endTime = ISOtoString(jsonObj.endTime);
    }
  }
}
