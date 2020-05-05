import { ClarificationRequest } from '@/models/management/ClarificationRequest';

export class PublicClarification {
  clarificationRequest!: ClarificationRequest;
  availability!: string;
  constructor(jsonObj?: PublicClarification) {
    if (jsonObj) {
      this.clarificationRequest = new ClarificationRequest(
        jsonObj.clarificationRequest
      );
      this.availability = jsonObj.availability;
    }
  }
}