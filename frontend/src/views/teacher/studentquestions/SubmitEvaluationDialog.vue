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
          {{ 'Submit new evaluation' }}
        </span>
      </v-card-title>

      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-switch
              data-cy="accepted"
              v-model="evaluation.accepted"
              class="ma-4"
              label="Accepted"
            />
            <v-flex xs24 sm12 md8>
              <v-text-field
                data-cy="justification"
                v-model="evaluation.justification"
                label="Justification"
              />
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
        <v-btn class="white--text" color="blue darken-1" @click="saveEvaluation"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import StudentQuestion from '@/models/studentquestion/StudentQuestion';
import Evaluation from '@/models/studentquestion/Evaluation';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {}
})
export default class SubmitQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: StudentQuestion, required: true })
  readonly studentQuestion!: StudentQuestion;

  evaluation!: Evaluation;

  async created() {
    this.evaluation = new Evaluation();
  }

  async saveEvaluation() {
    if (!this.evaluation.accepted && !this.evaluation.justification) {
      await this.$store.dispatch(
        'error',
        'Rejected student questions must have a justification'
      );
      return;
    }

    try {
      let result = await RemoteServices.submitStudentQuestionEvaluation(
        this.evaluation,
        this.studentQuestion.id
      );
      this.$emit('submit-evaluation', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>

<style scoped></style>
