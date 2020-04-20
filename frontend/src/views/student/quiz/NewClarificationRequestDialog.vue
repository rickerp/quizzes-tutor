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
          Clarification Request
        </span>
      </v-card-title>
      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="editClarification.content"
                label="Content"
                data-cy="ClrfReq"
              ></v-textarea>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="primary"
          dark
          @click="$emit('dialog', false)"
          data-cy="bttnClrfCancel"
          >Cancel</v-btn
        >
        <v-btn
          color="primary"
          dark
          @click="saveClarification"
          data-cy="bttnClrfSave"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';

@Component
export default class NewClarificationRequestDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ClarificationRequest, required: true })
  readonly request!: ClarificationRequest;
  @Prop({ required: true }) questionAnswerId!: number;

  editClarification!: ClarificationRequest;

  created() {
    this.editClarification = this.request;
  }

  async saveClarification() {
    if (!this.editClarification.content) {
      await this.$store.dispatch('error', 'Clarification must have Content');
      return;
    }
    try {
      const result = await RemoteServices.createClarification(
        this.questionAnswerId,
        this.editClarification
      );
      this.$emit('save-clarification', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
