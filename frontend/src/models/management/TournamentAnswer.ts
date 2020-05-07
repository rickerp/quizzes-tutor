import { ISOtoString } from '@/services/ConvertDateService';

export class TournamentAnswer {
  tournamentId!: number;
  tournamentName!: string;
  topicsName!: string[];
  nrQuestions!: number;
  nrCorrectAnswers!: number;
  finishTime!: string;

  constructor(jsonObj?: TournamentAnswer) {
    if (jsonObj) {
      this.tournamentId = jsonObj.tournamentId;
      this.tournamentName = jsonObj.tournamentName;
      this.topicsName = jsonObj.topicsName;
      this.nrQuestions = jsonObj.nrQuestions;
      this.nrCorrectAnswers = jsonObj.nrCorrectAnswers;
      if (jsonObj.finishTime) this.finishTime = ISOtoString(jsonObj.finishTime);
    }
  }
}
