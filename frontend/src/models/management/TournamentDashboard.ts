import { TournamentAnswer } from '@/models/management/TournamentAnswer';

export default class TournamentDashboard {
  isPublic!: boolean;
  nrQuestions!: number;
  nrAnswers!: number;
  nrCorrectAnswers!: number;
  tournamentScores!: TournamentAnswer[];

  constructor(jsonObj?: TournamentDashboard) {
    if (jsonObj) {
      this.isPublic = jsonObj.isPublic;
      this.nrQuestions = jsonObj.nrQuestions;
      this.nrAnswers = jsonObj.nrAnswers;
      this.nrCorrectAnswers = jsonObj.nrCorrectAnswers;
      this.tournamentScores = jsonObj.tournamentScores.map(
        tA => new TournamentAnswer(tA)
      );
    }
  }
}
