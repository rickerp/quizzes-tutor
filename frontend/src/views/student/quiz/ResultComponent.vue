<template>
  <div
    v-if="question"
    v-bind:class="[
      'question-container',
      answer.optionId === null ? 'unanswered' : '',
      answer.optionId !== null &&
      correctAnswer.correctOptionId === answer.optionId
        ? 'correct-question'
        : 'incorrect-question'
    ]"
  >
    <v-toolbar class="pb-3 mb-3" color="#333333" dark>
      <v-icon style="padding-right: 20px">
        {{ showClarifications ? 'fas fa-comments' : 'fas fa-file-alt' }}
      </v-icon>
      <v-toolbar-title>
        {{ showClarifications ? 'Clarifications' : 'Question' }}
      </v-toolbar-title>
      <v-spacer />
      <v-btn
        color="primary"
        class="mr-3"
        v-if="showClarifications"
        dark
        @click="showClarificationDialog"
      >
        Create
      </v-btn>
      <v-btn color="primary" dark @click="changeMode">
        {{ showClarifications ? 'Show Questions' : 'Show Clarifications' }}
      </v-btn>
    </v-toolbar>
    <div v-if="!showClarifications">
      <div class="question">
        <span
          @click="decreaseOrder"
          @mouseover="hover = true"
          @mouseleave="hover = false"
          class="square"
        >
          <i v-if="hover && questionOrder !== 0" class="fas fa-chevron-left" />
          <span v-else>{{ questionOrder + 1 }}</span>
        </span>
        <div
          class="question-content"
          v-html="convertMarkDown(question.content, question.image)"
        ></div>
        <div @click="increaseOrder" class="square">
          <i
            v-if="questionOrder !== questionNumber - 1"
            class="fas fa-chevron-right"
          />
        </div>
      </div>
      <ul class="option-list">
        <li
          v-for="(n, index) in question.options.length"
          :key="index"
          v-bind:class="[
            answer.optionId === question.options[index].optionId ? 'wrong' : '',
            correctAnswer.correctOptionId === question.options[index].optionId
              ? 'correct'
              : '',
            'option'
          ]"
        >
          <i
            v-if="
              correctAnswer.correctOptionId === question.options[index].optionId
            "
            class="fas fa-check option-letter"
          />
          <i
            v-else-if="answer.optionId === question.options[index].optionId"
            class="fas fa-times option-letter"
          />
          <span v-else class="option-letter">{{ optionLetters[index] }}</span>
          <span
            class="option-content"
            v-html="convertMarkDown(question.options[index].content)"
          />
        </li>
      </ul>
    </div>
    <chat-component
      v-if="showClarifications"
      :requests="answer.clarificationRequests"
      :show-toolbar="false"
    >
    </chat-component>
    <new-clarification-dialog
      v-if="create"
      v-model="newClarificationDialog"
      :request="newClarification"
      :question-answer-id="answer.questionAnswerId"
      v-on:save-clarification="onSaveClarification"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model, Emit } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Image from '@/models/management/Image';
import ChatComponent from '@/views/common/clarification/ChatComponent.vue';
import NewClarificationRequestDialog from '@/views/student/quiz/NewClarificationRequestDialog.vue';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';

@Component({
  components: {
    'chat-component': ChatComponent,
    'new-clarification-dialog': NewClarificationRequestDialog
  }
})
export default class ResultComponent extends Vue {
  @Model('questionOrder', Number) questionOrder: number | undefined;
  @Prop(StatementQuestion) readonly question!: StatementQuestion;
  @Prop(StatementCorrectAnswer) readonly correctAnswer!: StatementCorrectAnswer;
  @Prop(StatementAnswer) readonly answer!: StatementAnswer;
  @Prop() readonly questionNumber!: number;
  hover: boolean = false;
  showClarifications: boolean = false;
  create: boolean = false;
  newClarificationDialog: boolean = false;
  newClarification: ClarificationRequest | undefined;
  optionLetters: string[] = ['A', 'B', 'C', 'D'];

  @Emit()
  increaseOrder() {
    return 1;
  }

  @Emit()
  decreaseOrder() {
    return 1;
  }

  async changeMode() {
    this.showClarifications = !this.showClarifications;
  }
  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }

  showClarificationDialog() {
    this.newClarification = new ClarificationRequest();
    this.newClarification.state = 'UNRESOLVED';
    this.create = true;
    this.newClarificationDialog = true;
  }

  onSaveClarification(request: ClarificationRequest) {
    this.newClarificationDialog = false;
    this.create = false;
    this.answer.clarificationRequests.unshift(request);
  }
}
</script>
