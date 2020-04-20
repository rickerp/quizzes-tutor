<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{ 'Submit new question' }}
        </span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md8>
              <v-text-field
                class="questionTitle"
                v-model="question.title"
                label="Title"
              />
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="question.content"
                data-cy="questionContent"
                label="Question"
              ></v-textarea>
            </v-flex>
            <v-flex
              xs24
              sm12
              md12
              v-for="index in question.options.length"
              :key="index"
            >
              <v-switch
                v-model="question.options[index - 1].correct"
                :data-cy="'correct' + index"
                class="ma-4"
                label="Correct"
              />
              <v-textarea
                outline
                rows="10"
                v-model="question.options[index - 1].content"
                :data-cy="'option' + index"
                :label="`Option ${index}`"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          class="white--text"
          color="blue darken-1"
          @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn
          class="white--text"
          data-cy="saveQuestionBtn"
          color="blue darken-1"
          @click="saveQuestion"
          >Submit</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '../../models/studentquestion/StudentQuestion';

@Component({
  components: {}
})
export default class SubmitQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  question!: Question;

  async created() {
    this.question = new Question(this.question);
  }

  async saveQuestion() {
    if (this.question && (!this.question.title || !this.question.content)) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    try {
      let studentQuestion = new StudentQuestion();
      studentQuestion.question = this.question;
      const result = await RemoteServices.submitStudentQuestion(
        studentQuestion
      );
      this.$emit('submit-question', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
