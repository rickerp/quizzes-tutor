<template>
  <v-container
    fluid
    class="pb-3 mb-3 pt-4 mt-4"
    v-if="question"
    v-bind:class="[
      'question-container',
      option.id === null ? 'unanswered' : '',
      option.id !== null && correctOption.id === option.id
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <div class="question">
      <span class="square">
        <span> 1 </span>
      </span>
      <div
        class="question-content"
        v-html="convertMarkDown(question.content, question.image)"
      ></div>
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
export default class ClarificationQuestionComponent extends Vue {
  @Prop(Question) readonly question!: Question;
  @Prop(Option) readonly option!: Option;
  @Prop(Option) readonly correctOption!: Option;
  optionLetters: string[] = ['A', 'B', 'C', 'D'];

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
