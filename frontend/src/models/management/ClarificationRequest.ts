import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import { ClarificationComment } from '@/models/management/ClarificationComment';
import User from '@/models/user/User';
import { ISOtoString } from '@/services/ConvertDateService';

export class ClarificationRequest {
  user!: User;
  id!: number;
  state!: string;
  content!: string;
  creationDate!: string;
  clarificationComment!: ClarificationComment;
  questionAnswer!: QuestionAnswer;

  constructor(jsonObj?: ClarificationRequest) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.state = jsonObj.state;
      this.content = jsonObj.content;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.user = new User(jsonObj.user);
      this.questionAnswer = new QuestionAnswer(jsonObj.questionAnswer);
      if (jsonObj.clarificationComment == null) {
        return;
      }
      this.clarificationComment = new ClarificationComment(
        jsonObj.clarificationComment
      );
    }
  }
}
