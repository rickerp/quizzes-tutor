import { ClarificationRequest } from '@/models/management/ClarificationRequest';

export default class StatementAnswer {
  public optionId: number | null = null;
  public questionAnswerId: number | null = null;
  public timeTaken: number = 0;
  public sequence!: number;
  public clarificationRequests: ClarificationRequest[] = [];

  constructor(jsonObj?: StatementAnswer) {
    if (jsonObj) {
      this.optionId = jsonObj.optionId;
      this.timeTaken = jsonObj.timeTaken;
      this.sequence = jsonObj.sequence;
      this.questionAnswerId = jsonObj.questionAnswerId;

      if (jsonObj.clarificationRequests) {
        this.clarificationRequests = jsonObj.clarificationRequests.map(
          request => new ClarificationRequest(request)
        );
      }
    }
  }
}
