import StatementOption from '@/models/statement/StatementOption';
import Image from '@/models/management/Image';
import { _ } from 'vue-underscore';

export default class StatementQuestion {
  questionAnswerId!: number;
  sequence!: number;
  content!: string;
  image: Image | null = null;
  options: StatementOption[] = [];

  constructor(jsonObj?: StatementQuestion) {
    if (jsonObj) {
      this.questionAnswerId = jsonObj.questionAnswerId;
      this.sequence = jsonObj.sequence;
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
