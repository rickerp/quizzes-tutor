import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import User from '@/models/user/User';
import { ISOtoString } from '@/services/ConvertDateService';

export class ClarificationComment {
  id!: number;
  state!: string;
  content!: string;
  creationDate!: string;
  user!: User;
  clarificationRequest!: ClarificationRequest;

  constructor(jsonObj?: ClarificationComment) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.state = jsonObj.state;
      this.content = jsonObj.content;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.user = new User(jsonObj.user);
      this.clarificationRequest = new ClarificationRequest(
        jsonObj.clarificationRequest
      );
    }
  }
}
