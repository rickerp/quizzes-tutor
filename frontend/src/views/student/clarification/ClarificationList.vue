<template>
  <v-card class="table">
    <v-data-table
      v-if="!viewFullClarification"
      :headers="headers"
      :items="clarifications"
      :search="search"
      :sort-by="['content']"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
          <v-btn color="primary" dark @click="$emit('newClarification')">
            New Clarification
          </v-btn>
        </v-card-title>
      </template>
      <template v-slot:item.action="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon small class="mr-2" v-on="on" @click="showFullClarification(item)"
              >visibility</v-icon
            >
          </template>
          <span>Show Question</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <req-component
      v-if="viewFullClarification"
      @closeClarification="closeClarification"
      :question="questionAnswer.question"
      :option="questionAnswer.option"
      :correctOption="correctOption"
    />
    <chat-component
      v-if="viewFullClarification"
      :request="clarification"
      :hasComment="hasComment"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import ClarificationComponent from '@/views/student/clarification/ClarificationComponent.vue';
import Option from '@/models/management/Option';
import ChatComponent from '@/views/student/clarification/ChatComponent.vue';
@Component({
  components: {
    'req-component': ClarificationComponent,
    'chat-component': ChatComponent
  }
})
export default class ClarificationList extends Vue {
  @Prop({ type: Array, required: true })
  readonly clarifications!: ClarificationRequest[];
  clarification: ClarificationRequest | undefined;
  hasComment: boolean = false;
  questionAnswer: QuestionAnswer | undefined;
  correctOption: Option | undefined;
  viewFullClarification: boolean = false;
  search: string = '';
  headers: object = [
    {
      text: 'Clarification Request',
      value: 'content',
      align: 'left',
      width: '50%'
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
      width: '25%'
    },
    {
      text: 'State',
      value: 'state',
      align: 'center',
      width: '20%'
    },
    {
      text: 'Action',
      value: 'action',
      align: 'center',
      width: '5%'
    }
  ];
  async showFullClarification(request: ClarificationRequest) {
    this.viewFullClarification = true;
    this.clarification = request;
    this.hasComment = typeof request.clarificationComment !== 'undefined';
    this.questionAnswer = request.questionAnswer;
    this.correctOption = this.questionAnswer.question.options.filter(
      option => (option.correct = true)
    )[0];
  }
  async closeClarification() {
    this.viewFullClarification = false;
  }
}
</script>

<style lang="scss"></style>
