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
          {{ 'Edit Question' }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editQuestion">
        <v-text-field v-model="editQuestion.question.title" label="Title" />
        <v-textarea
          data-cy="questionContent"
          outline
          rows="10"
          v-model="editQuestion.question.content"
          label="Question"
        ></v-textarea>
        <div v-for="index in editQuestion.question.options.length" :key="index">
          <v-switch
            v-model="editQuestion.question.options[index - 1].correct"
            :data-cy="'correct' + index"
            class="ma-4"
            label="Correct"
          />
          <v-textarea
            outline
            rows="10"
            v-model="editQuestion.question.options[index - 1].content"
            :label="`Option ${index}`"
            :data-cy="'option' + index"
          ></v-textarea>
        </div>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn
          color="blue darken-1"
          data-cy="saveQuestionBtn"
          @click="saveQuestion"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestion from '@/models/studentquestion/StudentQuestion';

@Component
export default class EditQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;
  @Prop({ type: Boolean, required: true })
  readonly teacher!: boolean;

  editQuestion!: StudentQuestion;

  created() {
    this.updateQuestion();
  }

  @Watch('question', { immediate: true, deep: true })
  updateQuestion() {
    this.editQuestion = new StudentQuestion(this.studentQuestion);
  }

  async saveQuestion() {
    if (
      this.editQuestion &&
      (!this.editQuestion.question.title || !this.editQuestion.question.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    try {
      let result;
      if (this.teacher)
        result = await RemoteServices.editQuestion(this.editQuestion);
      else result = await RemoteServices.reSubmitQuestion(this.editQuestion);
      this.$emit('edit-question', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
