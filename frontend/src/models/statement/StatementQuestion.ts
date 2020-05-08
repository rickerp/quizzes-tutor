import StatementOption from '@/models/statement/StatementOption';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';

export default class StatementQuestion {
  sequence!: number;
  questionId!: number;
  content!: string;
  image: Image | null = null;
  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.sequence = jsonObj.sequence;
      this.questionId = jsonObj.questionId;
      this.content = jsonObj.content;
      this.image = jsonObj.image;

      if (jsonObj.options) {
        this.options = _.shuffle(
          jsonObj.options.map(
            (option: StatementOption) => new StatementOption(option)
          )
        );
      }
    }
  }
}
