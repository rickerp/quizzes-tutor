<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="displayQuestions"
      :items-per-page="15"
      multi-sort
    >
      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content)"
          @click="showQuestionDialog(item)"
      /></template>

      <template v-slot:item.evaluation="{ item }">
        <v-chip
          @click="showEvaluationDialog(item.evaluation)"
          :color="getEvaluationColor(item.evaluation)"
          dark
          >{{ getEvaluationLabel(item.evaluation) }}</v-chip
        >
      </template>
    </v-data-table>

    <evaluation-dialog
      v-if="currentEvaluation"
      v-model="evaluationDialog"
      :evaluation="currentEvaluation"
      v-on:close-evaluation-dialog="onCloseEvaluationDialog"
    />

    <show-question-dialog
      v-if="currentQuestion"
      v-model="questionDialog"
      :question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/studentquestion/StudentQuestion';
import Image from '@/models/management/Image';
import Question from '@/models/management/Question';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import Evaluation from '@/models/studentquestion/Evaluation';
import EvaluationDialog from '@/views/student/EvaluationDialog.vue';

@Component({
  components: {
    'evaluation-dialog': EvaluationDialog,
    'show-question-dialog': ShowQuestionDialog
  }
})
export default class StatsView extends Vue {
  @Prop({ type: Boolean, required: false, default: true })
  readonly teacherView!: boolean;

  currentQuestion: Question | null = null;
  currentEvaluation: Evaluation | null = null;
  evaluationDialog = false;
  questionDialog = false;
  headers = [
    ...(this.teacherView ? [{ text: 'Student', value: 'student' }] : []),
    { text: 'Title', value: 'title' },
    { text: 'Question', value: 'content' },
    { text: 'Topics', value: 'topics' },
    { text: 'Creation Date', value: 'creationDate' },
    { text: 'Evaluation', value: 'evaluation' }
  ];
  questions: StudentQuestion[] = [];

  async created() {
    this.questions = await RemoteServices.listStudentQuestions();
  }

  get displayQuestions() {
    return this.questions.map(s => {
      return {
        question: s.question,
        ...s.question,
        topics: s.question.topics.map(t => t.name).join(', '),
        ...s
      };
    });
  }

  convertMarkDownNoFigure(text: string): string {
    return convertMarkDownNoFigure(text, null);
  }

  showQuestionDialog(question: StudentQuestion) {
    this.currentQuestion = question.question;
    this.questionDialog = true;
  }

  showEvaluationDialog(evaluation: Evaluation) {
    this.currentEvaluation = evaluation;
    this.evaluationDialog = true;
  }

  onCloseEvaluationDialog() {
    this.evaluationDialog = false;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }

  getEvaluationColor(evaluation: Evaluation | null) {
    if (!evaluation) return 'orange';
    if (evaluation.accepted) return 'green';
    return 'red';
  }

  getEvaluationLabel(evaluation: Evaluation | null) {
    return evaluation
      ? evaluation.accepted
        ? 'Accepted'
        : 'Rejected'
      : 'Pending';
  }
}
</script>

<style lang="scss" scoped></style>
