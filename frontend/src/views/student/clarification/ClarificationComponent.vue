<template>
  <v-container
    class="pb-2 mb-2 pt-5 mt-5"
    v-if="question"
    v-bind:class="[
      'question-container',
      option.id === null ? 'unanswered' : '',
      option.id !== null && correctOption.id === option.id
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <v-toolbar class="pb-3 mb-3" color="#333333" dark>
      <v-icon style="padding-right: 20px"> fas fa-file-alt </v-icon>
      <v-toolbar-title>Question</v-toolbar-title>
      <v-spacer />
      <v-btn color="primary" dark @click="$emit('closeClarification')">
        Close
      </v-btn>
    </v-toolbar>
    <div class="question">
      <span class="square">
        <span> 1 </span>
      </span>
      <div class="question-content" v-html="convertMarkDown(question.content, question.image)"></div>
    </div>
    <ul class="option-list">
      <li
        v-for="(n, index) in question.options.length"
        :key="index"
        v-bind:class="[
          option.id === question.options[index].id ? 'wrong' : '',
          correctOption.id === question.options[index].id ? 'correct' : '',
          'option'
        ]"
      >
        <i
          v-if="correctOption.id === question.options[index].id"
          class="fas fa-check option-letter"
        />
        <i
          v-else-if="option.id === question.options[index].id"
          class="fas fa-times option-letter"
        />
        <span v-else class="option-letter">{{ optionLetters[index] }}</span>
        <span
          class="option-content"
          v-html="convertMarkDown(question.options[index].content)"
        />
      </li>
    </ul>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import Question from '@/models/management/Question';
import Option from '@/models/management/Option';

@Component
export default class ClarificationComponent extends Vue {
  @Prop(Question) readonly question!: Question;
  @Prop(Option) readonly option!: Option;
  @Prop(Option) readonly correctOption!: Option;
  optionLetters: string[] = ['A', 'B', 'C', 'D'];

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>

<style lang="scss" scoped>
.unanswered {
  .question {
    background-color: #761515 !important;
    color: #fff !important;
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.correct-question {
  .question .question-content {
    background-color: #285f23 !important;
    color: white !important;
  }
  .question .square {
    background-color: #285f23 !important;
    color: white !important;
  }
  .correct {
    .option-content {
      background-color: #299455;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #299455 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}

.incorrect-question {
  .question .question-content {
    background-color: #761515 !important;
    color: white !important;
  }
  .question .square {
    background-color: #761515 !important;
    color: white !important;
  }
  .wrong {
    .option-content {
      background-color: #cf2323;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #cf2323 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
  .correct {
    .option-content {
      background-color: #333333;
      color: rgb(255, 255, 255) !important;
    }

    .option-letter {
      background-color: #333333 !important;
      color: rgb(255, 255, 255) !important;
    }
  }
}
</style>
