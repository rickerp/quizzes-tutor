import { ClarificationRequest } from '@/models/management/ClarificationRequest';

export class PublicClarification {
  id!: number;
  clarificationRequest!: ClarificationRequest;
  availability!: string;
  constructor(jsonObj?: PublicClarification) {
    if (jsonObj) {
      this.clarificationRequest = new ClarificationRequest(
        jsonObj.clarificationRequest
      );
      this.id = jsonObj.id;
      this.availability = jsonObj.availability;
    }
  }
}
