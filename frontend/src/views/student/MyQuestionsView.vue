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
          data-cy="evaluationBtn"
          @click="showEvaluationDialog(item.studentQuestion)"
          :color="getEvaluationColor(item.evaluation)"
          dark
          >{{ getEvaluationLabel(item.evaluation) }}</v-chip
        >
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom v-if="item.evaluation && item.evaluation.accepted">
          <template v-slot:activator="{ on }">
            <v-icon
              v-if="teacherView"
              data-cy="publish"
              large
              class="mr-2"
              v-on="on"
              @click="publish(item.studentQuestion)"
              >publish</v-icon
            >
          </template>
          <span>Publish Question</span>
        </v-tooltip>
        <v-tooltip
          bottom
          v-if="
            item.evaluation &&
              ((!teacherView && !item.evaluation.accepted) ||
                (teacherView && item.evaluation.accepted))
          "
        >
          <template v-slot:activator="{ on }">
            <v-icon
              data-cy="edit"
              large
              class="mr-2"
              v-on="on"
              @click="editQuestion(item.studentQuestion)"
              >edit</v-icon
            >
          </template>
          <span>Edit Question</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <submit-question-dialog
      v-if="newQuestionDialog"
      v-model="newQuestionDialog"
      v-on:submit-question="onSubmitQuestion"
      v-on:close-submit-question-dialog="onCloseSubmitQuestionDialog"
    />

    <edit-question-dialog
      v-if="editQuestionDialog"
      v-model="editQuestionDialog"
      :studentQuestion="studentQuestion"
      :teacher="teacherView"
      v-on:edit-question="onEditQuestion"
      v-on:close-edit-question-dialog="onCloseEditQuestionDialog"
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
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/studentquestion/StudentQuestion';
import Question from '@/models/management/Question';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import Evaluation from '@/models/studentquestion/Evaluation';
import EvaluationDialog from '@/views/student/EvaluationDialog.vue';
import SubmitQuestionDialog from '@/views/student/SubmitQuestionDialog.vue';
import SubmitEvaluationDialog from '@/views/teacher/studentquestions/SubmitEvaluationDialog.vue';
import EditQuestionDialog from '@/views/teacher/studentquestions/EditQuestionDialog.vue';

@Component({
  components: {
    'submit-question-dialog': SubmitQuestionDialog,
    'evaluation-dialog': EvaluationDialog,
    'show-question-dialog': ShowQuestionDialog,
    'submit-evaluation-dialog': SubmitEvaluationDialog,
    'edit-question-dialog': EditQuestionDialog
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
  editQuestionDialog = false;

  headers = [
    ...(this.teacherView
      ? [
          { text: 'Actions', value: 'action' },
          { text: 'Student', value: 'student' }
        ]
      : [{ text: 'Actions', value: 'action' }]),
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
      this.questions.sort(
        (a, b) =>
          +new Date(b.question.creationDate as string) -
          +new Date(a.question.creationDate as string)
      );
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

  publish(question: StudentQuestion) {
    RemoteServices.publishStudentQuestion(question);
    this.questions = this.questions.filter(q => q !== question);
  }

  editQuestion(question: StudentQuestion) {
    this.studentQuestion = question;
    this.editQuestionDialog = true;
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

  onCloseEditQuestionDialog() {
    this.editQuestionDialog = false;
  }

  onEditQuestion(studentQuestion: StudentQuestion) {
    this.studentQuestion = studentQuestion;
    this.questions = this.questions.filter(
      studentQuestion => studentQuestion.id !== this.studentQuestion?.id
    );
    this.questions.unshift(studentQuestion);
    this.editQuestionDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
