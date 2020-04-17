<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="displayQuestions"
      :items-per-page="15"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <!-- <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          /> -->
          <v-spacer />
          <v-btn
            data-cy="newQuestionBtn"
            v-if="!teacherView"
            color="primary"
            dark
            @click="newQuestion"
            >Submit new Question</v-btn
          >
        </v-card-title>
      </template>
      <template v-slot:item.content="{ item }">
        <p
          v-html="convertMarkDownNoFigure(item.content)"
          @click="showQuestionDialog(item)"
      /></template>

      <template v-slot:item.evaluation="{ item }">
        <v-chip
          @click="showEvaluationDialog(item.studentQuestion)"
          :color="getEvaluationColor(item.evaluation)"
          dark
          >{{ getEvaluationLabel(item.evaluation) }}</v-chip
        >
      </template>
    </v-data-table>

    <submit-question-dialog
      v-if="newQuestionDialog"
      v-model="newQuestionDialog"
      v-on:submit-question="onSubmitQuestion"
      v-on:close-submit-question-dialog="onCloseSubmitQuestionDialog"
    />

    <evaluation-dialog
      v-if="studentQuestion && studentQuestion.evaluation"
      v-model="evaluationDialog"
      :evaluation="studentQuestion.evaluation"
      v-on:close-evaluation-dialog="onCloseEvaluationDialog"
    />

    <submit-evaluation-dialog
      v-if="submitEvaluationDialog && teacherView"
      v-model="submitEvaluationDialog"
      :studentQuestion="studentQuestion"
      v-on:submit-evaluation="onSubmitEvaluation"
      v-on:close-submit-evaluation-dialog="onCloseSubmitEvaluationDialog"
    />

    <show-question-dialog
      v-if="studentQuestion"
      v-model="questionDialog"
      :question="studentQuestion.question"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/studentquestion/StudentQuestion';
import Question from '@/models/management/Question';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import Evaluation from '@/models/studentquestion/Evaluation';
import EvaluationDialog from '@/views/student/EvaluationDialog.vue';
import SubmitQuestionDialog from '@/views/student/SubmitQuestionDialog.vue';
import SubmitEvaluationDialog from '@/views/teacher/studentquestions/SubmitEvaluationDialog.vue';

@Component({
  components: {
    'submit-question-dialog': SubmitQuestionDialog,
    'evaluation-dialog': EvaluationDialog,
    'show-question-dialog': ShowQuestionDialog,
    'submit-evaluation-dialog': SubmitEvaluationDialog
  }
})
export default class MyQuestionsView extends Vue {
  @Prop({ type: Boolean, required: false, default: false })
  readonly teacherView!: boolean;

  studentQuestion: StudentQuestion | null = null;
  evaluationDialog = false;
  questionDialog = false;
  submitEvaluationDialog = false;
  newQuestionDialog = false;

  headers = [
    ...(this.teacherView ? [{ text: 'Student', value: 'student' }] : []),
    { text: 'Title', value: 'title' },
    { text: 'Question', value: 'content' },
    { text: 'Creation Date', value: 'creationDate' },
    { text: 'Evaluation', value: 'evaluation' }
  ];
  questions: StudentQuestion[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      if (this.teacherView)
        this.questions = await RemoteServices.listCourseStudentQuestions();
      else {
        this.questions = await RemoteServices.listStudentQuestions();
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  get displayQuestions() {
    return this.questions.map(s => {
      return {
        studentQuestion: s,
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
    this.studentQuestion = question;
    this.questionDialog = true;
  }

  showEvaluationDialog(studentQuestion: StudentQuestion) {
    this.studentQuestion = studentQuestion;

    if (studentQuestion.evaluation) {
      this.evaluationDialog = true;
    } else {
      this.submitEvaluationDialog = true;
    }
  }

  onCloseSubmitEvaluationDialog() {
    this.submitEvaluationDialog = false;
  }

  onSubmitEvaluation(evaluation: Evaluation) {
    if (this.studentQuestion) this.studentQuestion.evaluation = evaluation;
    this.submitEvaluationDialog = false;
    this.questions = [...this.questions]; // refresh questions to show the evaluation instantly
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

  newQuestion() {
    this.newQuestionDialog = true;
  }

  onCloseSubmitQuestionDialog() {
    this.newQuestionDialog = false;
  }

  onSubmitQuestion(studentQuestion: StudentQuestion) {
    this.questions.unshift(studentQuestion);
    this.newQuestionDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
