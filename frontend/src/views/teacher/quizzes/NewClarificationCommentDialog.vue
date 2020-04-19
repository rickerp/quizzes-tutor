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
          Comment
        </span>
      </v-card-title>
      <v-card-text class="text-left">
        <span class="font-weight-bold">Clarification: </span>
        <span>{{ clarification.content }}</span>
      </v-card-text>
      <v-card-text class="text-left">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                rows="10"
                v-model="comment.content"
                label="Content"
                data-cy="cmtContent"
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
          data-cy="bttnClrfCancel"
          @click="$emit('dialog', false)"
        >
          Cancel</v-btn
        >
        <v-btn color="primary" dark data-cy="bttnCmtSave" @click="saveComment">
          Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import { ClarificationComment } from '@/models/management/ClarificationComment';

@Component
export default class NewClarificationCommentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: true }) clarification!: ClarificationRequest;
  @Prop({ required: true }) comment!: ClarificationComment;

  async saveComment() {
    if (!this.comment.content) {
      await this.$store.dispatch('error', 'Comment must have Content');
      return;
    }
    try {
      const result = await RemoteServices.createClarificationComment(
        this.clarification.id,
        this.comment
      );
      this.$emit('save-comment', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
