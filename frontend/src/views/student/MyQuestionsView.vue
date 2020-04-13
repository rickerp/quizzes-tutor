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
    </v-data-table>
    <show-question-dialog
      v-if="currentQuestion"
      :dialog="questionDialog"
      :question="currentQuestion"
      v-on:close-show-question-dialog="onCloseShowQuestionDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/studentquestion/StudentQuestion';
import Image from '@/models/management/Image';
import Question from '@/models/management/Question';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog
  }
})
export default class StatsView extends Vue {
  currentQuestion: Question | null = null;
  questionDialog = false;
  headers = [
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
        evaluation: s.evaluation ? '?' : 'Pending'
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

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }
}
</script>

<style lang="scss" scoped></style>
