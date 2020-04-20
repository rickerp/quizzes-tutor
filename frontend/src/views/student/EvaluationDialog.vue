<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">Teacher's justification</span>
      </v-card-title>

      <v-card-text class="text-left">
        {{ evaluation.justification || '(empty)' }}
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Evaluation from '@/models/studentquestion/Evaluation';

@Component({
  components: {}
})
export default class EvaluationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Evaluation, required: true }) readonly evaluation!: Evaluation;
}
</script>
