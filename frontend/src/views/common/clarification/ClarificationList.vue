<template>
  <div class="mt-0 pr-0">
    <v-card v-if="viewAction === 1" class="table">
      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :items="clarifications"
        :search="search"
        multi-sort
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      >
        <template v-slot:item.state="{ item }">
          <v-chip filter :color="getStatusColor(item.state)" small>
            <span>{{ item.state }}</span>
          </v-chip>
        </template>
        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
            />
          </v-card-title>
        </template>
        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="showFullClarification(item, 2)"
                data-cy="showQuestion"
                >visibility</v-icon
              >
            </template>
            <span>Show Question</span>
          </v-tooltip>
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                data-cy="showClrf"
                @click="showFullClarification(item, 3)"
                >fas fa-comments</v-icon
              >
            </template>
            <span>Show Clarification</span>
          </v-tooltip>
        </template>
      </v-data-table>
    </v-card>
    <v-container
      v-if="viewAction > 1"
      class="white mt-3 pt-0"
      style="width: 70%; height: 100%"
    >
      <v-row class="mt-0 pt-0">
        <v-toolbar
          v-if="viewAction > 1"
          class="pb-3 mt-0 pt-0 overflow-hidden "
          color="#333333"
          dark
        >
          <v-icon style="padding-right: 20px">
            {{ viewAction === 3 ? 'fas fa-comments' : 'fas fa-file-alt' }}
          </v-icon>
          <v-toolbar-title>
            {{ viewAction === 3 ? 'Clarifications' : 'Question' }}
          </v-toolbar-title>
          <v-spacer />
          <v-btn color="primary" dark @click="closeAction" data-cy="bttnClose">
            Close
          </v-btn>
        </v-toolbar>
      </v-row>
      <v-row>
        <req-component
          v-if="viewAction === 2"
          :question="questionAnswer.question"
          :option="questionAnswer.option"
          :correctOption="correctOption"
        />
      </v-row>
      <v-row>
        <chat-component
          v-if="viewAction === 3"
          @closeAction="closeAction"
          :requests="[clarification]"
          :show-toolbar="true"
        />
      </v-row>
    </v-container>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import ClarificationQuestionComponent from '@/views/common/clarification/ClarificationQuestionComponent.vue';
import Option from '@/models/management/Option';
import ChatComponent from '@/views/common/clarification/ChatComponent.vue';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
@Component({
  components: {
    'req-component': ClarificationQuestionComponent,
    'chat-component': ChatComponent
  }
})
export default class ClarificationList extends Vue {
  clarifications: ClarificationRequest[] = [];
  clarification: ClarificationRequest | undefined;
  questionAnswer: QuestionAnswer | undefined;
  correctOption: Option | undefined;
  viewAction: number = 1;
  search: string = '';

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarifications = await RemoteServices.getClarifications();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  headers: object = [
    {
      text: 'Clarification Request',
      value: 'content',
      align: 'left',
      width: '25%'
    },
    {
      text: 'Question Title',
      value: 'questionAnswer.question.title',
      align: 'left',
      width: '15%'
    },
    {
      text: 'Question',
      value: 'questionAnswer.question.content',
      align: 'left',
      width: '25%'
    },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center',
      width: '15%'
    },
    {
      text: 'State',
      value: 'state',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Action',
      value: 'action',
      align: 'center',
      width: '10%',
      sortable: false
    }
  ];

  async showFullClarification(
    request: ClarificationRequest,
    showAction: number
  ) {
    this.viewAction = showAction;
    this.clarification = request;
    this.questionAnswer = request.questionAnswer;
    this.correctOption = this.questionAnswer.question.options.filter(
      option => (option.correct = true)
    )[0];
  }

  getStatusColor(status: string) {
    if (status === 'UNRESOLVED') return 'red';
    else return 'green';
  }
  async closeAction() {
    this.viewAction = 1;
  }

  customFilter(
    value: string,
    search: string,
    clarification: ClarificationRequest
  ) {
    return (
      search != null &&
      JSON.stringify(clarification)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }
}
</script>
