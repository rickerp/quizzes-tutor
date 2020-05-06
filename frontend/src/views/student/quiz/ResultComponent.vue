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
        {{ resultStage > 0 ? 'fas fa-comments' : 'fas fa-file-alt' }}
      </v-icon>
      <v-toolbar-title>
        {{ showTitle() }}
      </v-toolbar-title>
      <v-spacer />
      <v-btn
        color="primary"
        class="mr-3"
        v-if="resultStage === 1"
        dark
        @click="showClarificationDialog"
      >
        <strong>Create</strong>
      </v-btn>
      <v-btn
        v-if="resultStage !== 2"
        class="mr-3"
        color="primary"
        dark
        @click="changeMode(2)"
      >
        <strong>Public Clarifications</strong>
      </v-btn>
      <v-btn
        v-if="resultStage !== 1"
        :class="{ 'mr-3': resultStage !== 0 }"
        color="primary"
        dark
        @click="changeMode(1)"
      >
        <strong>My Clarifications</strong>
      </v-btn>
      <v-btn
        v-if="resultStage !== 0"
        color="primary"
        dark
        @click="changeMode(0)"
      >
        <strong>Question</strong>
      </v-btn>
    </v-toolbar>
    <div v-if="resultStage === 0">
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
      v-if="resultStage === 1"
      :requests="answer.clarificationRequests"
      :show-toolbar="false"
    >
    </chat-component>
    <chat-component
      v-if="resultStage === 2"
      :requests="Pclarifications"
      :show-toolbar="false"
    ></chat-component>
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
import {
  Component,
  Vue,
  Prop,
  Model,
  Emit,
  Watch
} from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import StatementQuestion from '@/models/statement/StatementQuestion';
import StatementAnswer from '@/models/statement/StatementAnswer';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import Image from '@/models/management/Image';
import ChatComponent from '@/views/common/clarification/ChatComponent.vue';
import NewClarificationRequestDialog from '@/views/student/quiz/NewClarificationRequestDialog.vue';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import RemoteServices from '@/services/RemoteServices';

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
  resultStage: number = 0;
  changeInAnswer: boolean = true;
  Pclarifications: ClarificationRequest[] = [];
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

  async changeMode(resultStage: number) {
    if (resultStage == 2 && this.changeInAnswer) {
      this.changeInAnswer = false;
      await this.getPublicClarifications();
    }
    this.resultStage = resultStage;
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

  showTitle() {
    if (this.resultStage == 0) return 'Question';
    if (this.resultStage == 1) return 'My Clarifications';
    if (this.resultStage == 2) return 'Public Clarifications';
  }

  @Watch('answer')
  setAChangeInAnswer() {
    if (this.resultStage === 2) {
      this.getPublicClarifications();
    } else {
      this.changeInAnswer = true;
    }
  }

  async getPublicClarifications() {
    await this.$store.dispatch('loading');
    const questionId = this.getQuestionId();
    if (questionId) {
      try {
        const result = await RemoteServices.getPublicClarifications(questionId);
        this.Pclarifications = result.map(pClr => pClr.clarificationRequest);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  getQuestionId() {
    return this.question.questionId;
  }
}
</script>
