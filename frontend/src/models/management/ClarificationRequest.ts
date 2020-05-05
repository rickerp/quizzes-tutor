import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import { ClarificationComment } from '@/models/management/ClarificationComment';
import User from '@/models/user/User';
import { ISOtoString } from '@/services/ConvertDateService';

export class ClarificationRequest {
  user!: User;
  id!: number;
  state!: string;
  type!: string;
  content!: string;
  creationDate!: string;
  clarificationComments: ClarificationComment[] = [];
  questionAnswer!: QuestionAnswer;

  constructor(jsonObj?: ClarificationRequest) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.state = jsonObj.state;
      this.type = jsonObj.type;
      this.content = jsonObj.content;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.user = new User(jsonObj.user);
      this.questionAnswer = new QuestionAnswer(jsonObj.questionAnswer);
      if (jsonObj.clarificationComments == null) {
        return;
      }
      this.clarificationComments = jsonObj.clarificationComments.map(
        (clarificationCommentDto: ClarificationComment) => {
          return new ClarificationComment(clarificationCommentDto);
        }
      );
    }
  }
}
