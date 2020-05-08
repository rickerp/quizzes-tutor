<template>
  <div class="mt-0 pr-0">
    <v-card v-if="viewAction === 1" class="table">
      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :items="pClarifications"
        :search="search"
        multi-sort
        :mobile-breakpoint="0"
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      >
        <template v-slot:item.question.content="{ item }">
          <span v-html="convertMarkDownNoFigure(item.content)"
        /></template>

        <template v-slot:item.availability="{ item }">
          <v-chip
            filter
            :color="getAvailabilityColor(item.availability)"
            class="white--text"
            small
          >
            <span>
              <strong> {{ item.availability }} </strong></span
            >
          </v-chip> </template
        >Availability

        <template v-slot:top>
          <v-card-title>
            <v-text-field
              v-model="search"
              append-icon="search"
              label="Search"
              class="mx-2"
            />
            <v-spacer />
            <v-btn color="primary" dark @click="$emit('closeShowPclr')"
              >Close</v-btn
            >
          </v-card-title>
        </template>
        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                color="grey darken-3"
                large
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
                large
                color="primary darken-1"
                class="mr-2"
                v-on="on"
                data-cy="showClrf"
                @click="showFullClarification(item, 3)"
                >fas fa-comments</v-icon
              >
            </template>
            <span>Show Clarification</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.availability === 'INVISIBLE'">
            <template v-slot:activator="{ on }">
              <v-icon
                large
                color="green darken-4"
                class="mr-2"
                v-on="on"
                data-cy="changeVisibility"
                @click="addCourseExecutionToPClarification(item)"
                >fas fa-plus</v-icon
              >
            </template>
            <span>Show to Students</span>
          </v-tooltip>
          <v-tooltip bottom v-if="item.availability === 'VISIBLE'">
            <template v-slot:activator="{ on }">
              <v-icon
                large
                color="red darken-4"
                class="mr-2"
                v-on="on"
                data-cy="changeVisibility"
                @click="rmvCourseExecutionToPClarification(item)"
                >fas fa-minus</v-icon
              >
            </template>
            <span>Hide from Students</span>
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
          <v-card-actions>
            <v-btn
              color="primary"
              dark
              @click="closeAction"
              data-cy="bttnClose"
            >
              Close
            </v-btn>
          </v-card-actions>
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
import { Component, Vue, Prop } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import { PublicClarification } from '@/models/management/PublicClarification';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import Option from '@/models/management/Option';
import ClarificationQuestionComponent from '@/views/common/clarification/ClarificationQuestionComponent.vue';
import ChatComponent from '@/views/common/clarification/ChatComponent.vue';

@Component({
  components: {
    'req-component': ClarificationQuestionComponent,
    'chat-component': ChatComponent
  }
})
export default class ListPublicClarifications extends Vue {
  @Prop({ type: Question, required: true }) readonly question!: Question;
  pClarifications: PublicClarification[] = [];
  search: string = '';
  viewAction: number = 1;
  clarification: ClarificationRequest | undefined;
  questionAnswer: QuestionAnswer | undefined;
  correctOption: Option | undefined;

  async created() {
    await this.$store.dispatch('loading');
    if (this.question.id) {
      try {
        this.pClarifications = await RemoteServices.getPublicClarifications(
          this.question.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  showFullClarification(pClarification: PublicClarification, action: number) {
    this.viewAction = action;
    this.clarification = pClarification.clarificationRequest;
    this.questionAnswer = this.clarification.questionAnswer;
    this.correctOption = this.questionAnswer.question.options.filter(
      option => (option.correct = true)
    )[0];
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

  convertMarkDownNoFigure(text: string): string {
    return convertMarkDownNoFigure(text, null);
  }

  getAvailabilityColor(availability: string) {
    if (availability === 'INVISIBLE') return 'red darken-2';
    else return 'green darken-2';
  }

  async addCourseExecutionToPClarification(
    publicClarification: PublicClarification
  ) {
    await this.$store.dispatch('loading');
    if (this.question.id) {
      const index = this.pClarifications.indexOf(publicClarification);
      try {
        const result = await RemoteServices.addCourseExecutionToPublicClarification(
          publicClarification.id,
          this.question.id
        );
        this.pClarifications.splice(index, 1, result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async rmvCourseExecutionToPClarification(
    publicClarification: PublicClarification
  ) {
    console.log(publicClarification);
    await this.$store.dispatch('loading');
    if (this.question.id) {
      const index = this.pClarifications.indexOf(publicClarification);
      try {
        const result = await RemoteServices.removeCourseExecutionToPublicClarification(
          publicClarification.id,
          this.question.id
        );
        this.pClarifications.splice(index, 1, result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  headers: object = [
    {
      text: 'Action',
      value: 'action',
      align: 'left',
      width: '20%',
      sortable: false
    },
    {
      text: 'Clarification Request',
      value: 'clarificationRequest.content',
      align: 'left',
      width: '30%'
    },
    {
      text: 'Question Title',
      value: 'clarificationRequest.questionAnswer.question.title',
      align: 'left',
      width: '15%'
    },
    {
      text: 'Question',
      value: 'clarificationRequest.questionAnswer.question.content',
      align: 'left',
      width: '25%'
    },
    {
      text: 'Availability',
      value: 'availability',
      align: 'left',
      width: '10'
    }
  ];
}
</script>
