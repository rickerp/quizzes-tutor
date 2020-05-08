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
        <template v-slot:item.question.content="{ item }">
          <span v-html="convertMarkDownNoFigure(item.content)"
        /></template>

        <template v-slot:item.state="{ item }">
          <span v-if="item.type === 'PRIVATE'">
            <v-select
              v-model="item.state"
              :items="stateList"
              dense
              @change="changeState(item, item.state)"
            >
              <template v-slot:selection="{ item }">
                <v-chip :color="getStatusColor(item)" class="white--text" small>
                  <span>
                    <strong> {{ item }} </strong>
                  </span>
                </v-chip>
              </template>
            </v-select>
          </span>
          <span v-if="item.type === 'PUBLIC'">
            <v-chip
              filter
              :color="getStatusColor(item.state)"
              class="white--text"
              small
            >
              <span>
                <strong> {{ item.state }} </strong></span
              >
            </v-chip>
          </span>
        </template>

        <template v-slot:item.type="{ item }">
          <v-chip
            filter
            :color="getTypeColor(item.type)"
            class="white--text"
            small
          >
            <span>
              <strong> {{ item.type }} </strong></span
            >
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
                color="grey darken-3"
                large
                class="mr-2"
                v-on="on"
                @click="changeVue(item, 2)"
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
                @click="changeVue(item, 3)"
                >fas fa-comments</v-icon
              >
            </template>
            <span>Show Clarification</span>
          </v-tooltip>

          <v-tooltip
            bottom
            v-if="$store.getters.isTeacher && item.state === 'RESOLVED'"
          >
            <template v-slot:activator="{ on }">
              <v-icon
                large
                color="grey darken-3"
                class="mr-2"
                v-on="on"
                data-cy="changeType"
                @click="changeType(item)"
                >{{
                  item.type === 'PUBLIC' ? 'fas fa-lock' : 'fas fa-unlock'
                }}</v-icon
              >
            </template>
            <span
              >{{ item.type === 'PUBLIC' ? 'Make Private' : 'Make Public' }}
            </span>
          </v-tooltip>

          <v-tooltip bottom v-if="item.type === 'PRIVATE'">
            <template v-slot:activator="{ on }">
              <v-icon
                large
                color="primary darken-1"
                class="mr-2"
                v-on="on"
                data-cy="addCmt"
                @click="createComment(item)"
                >fas fa-plus</v-icon
              >
            </template>
            <span>Add Comment </span>
          </v-tooltip>

          <v-tooltip bottom v-if="canDeleteClr(item)">
            <template v-slot:activator="{ on }">
              <v-icon
                large
                class="mr-2"
                v-on="on"
                @click="deleteClarification(item)"
                color="red"
                data-cy="deleteClrf"
                >delete</v-icon
              >
            </template>
            <span>Delete Clarification</span>
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
          <v-switch
            v-if="clarification.type === 'PRIVATE'"
            class="pt-6 pl-4"
            flat
            color="primary"
            :dense="true"
            v-model="isResolved"
            @change="changeStateClarification()"
          >
            <template v-slot:label>
              <div>
                <strong class="grey--text text--lighten-2 ">
                  {{ isResolved ? 'Resolved' : 'Unresolved' }}
                </strong>
              </div>
            </template>
          </v-switch>
          <v-spacer />
          <v-card-actions>
            <v-btn
              v-if="viewAction === 3 && clarification.type === 'PRIVATE'"
              color="primary"
              dark
              data-cy="bttnAddComment"
              @click="createComment(clarification)"
            >
              Add Comment
            </v-btn>
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

    <new-comment-dialog
      v-if="newCommentDialog"
      v-model="addComment"
      :clarification="clarification"
      :comment="comment"
      v-on:save-comment="onSaveComment"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import ClarificationQuestionComponent from '@/views/common/clarification/ClarificationQuestionComponent.vue';
import Option from '@/models/management/Option';
import ChatComponent from '@/views/common/clarification/ChatComponent.vue';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';
import NewClarificationCommentDialog from '@/views/teacher/quizzes/NewClarificationCommentDialog.vue';
import { ClarificationComment } from '@/models/management/ClarificationComment';

@Component({
  components: {
    'req-component': ClarificationQuestionComponent,
    'chat-component': ChatComponent,
    'new-comment-dialog': NewClarificationCommentDialog
  }
})
export default class ClarificationList extends Vue {
  clarifications: ClarificationRequest[] = [];
  clarification: ClarificationRequest | undefined;
  questionAnswer: QuestionAnswer | undefined;
  comment: ClarificationComment | undefined;
  correctOption: Option | undefined;
  newCommentDialog: boolean = false;
  addComment: boolean = false;
  viewAction: number = 1;
  isResolved: boolean = true;
  search: string = '';
  stateList = ['RESOLVED', 'UNRESOLVED'];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarifications = await RemoteServices.getClarifications();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  changeVue(request: ClarificationRequest, showAction: number) {
    this.viewAction = showAction;
    this.clarification = request;
    this.isResolved = this.clarification.state == 'RESOLVED';
    this.questionAnswer = request.questionAnswer;
    this.correctOption = this.questionAnswer.question.options.filter(
      option => (option.correct = true)
    )[0];
  }

  closeAction() {
    this.viewAction = 1;
  }

  convertMarkDownNoFigure(text: string): string {
    return convertMarkDownNoFigure(text, null);
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

  createComment(clarification: ClarificationRequest) {
    this.addComment = true;
    this.newCommentDialog = true;
    this.clarification = clarification;
    this.comment = new ClarificationComment();
  }

  onSaveComment(newComment: ClarificationComment) {
    this.addComment = false;
    this.newCommentDialog = false;

    if (this.clarification && this.comment) {
      const index = this.clarifications.indexOf(this.clarification);

      let newClarification = newComment.clarificationRequest;
      this.clarification.clarificationComments.forEach(entry =>
        newClarification.clarificationComments.push(entry)
      );
      newClarification.clarificationComments.push(newComment);
      this.clarification = newClarification;

      this.comment.clarificationRequest = this.clarification;
      this.clarifications.splice(index, 1, this.clarification);
    }
  }

  async changeStateClarification() {
    let state = 'UNRESOLVED';
    if (this.isResolved) state = 'RESOLVED';

    if (this.clarification) {
      const index = this.clarifications.indexOf(this.clarification);
      try {
        this.clarification = await RemoteServices.changeClarificationState(
          this.clarification.id,
          state
        );
        this.clarifications.splice(index, 1, this.clarification);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  async changeState(clarificationReq: ClarificationRequest, state: string) {
    try {
      const index = this.clarifications.indexOf(clarificationReq);
      await RemoteServices.changeClarificationState(clarificationReq.id, state);
      this.clarifications.splice(index, 1, clarificationReq);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async changeType(clrfReq: ClarificationRequest) {
    let type = 'PUBLIC';
    if (clrfReq.type == 'PUBLIC') type = 'PRIVATE';
    const index = this.clarifications.indexOf(clrfReq);
    try {
      const result = await RemoteServices.changeClarificationType(
        clrfReq.id,
        type
      );
      this.clarifications.splice(index, 1, result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  getStatusColor(status: string) {
    if (status === 'UNRESOLVED') return 'red darken-2';
    else return 'green darken-2';
  }

  getTypeColor(type: string) {
    if (type === 'PRIVATE') return 'grey darken-3';
    return 'primary darken-2';
  }

  canDeleteClr(clarification: ClarificationRequest) {
    return (
      clarification.type === 'PRIVATE' &&
      clarification.clarificationComments.length == 0
    );
  }

  async deleteClarification(clarification: ClarificationRequest) {
    if (confirm('Are you sure you want to delete this quiz?')) {
      const index = this.clarifications.indexOf(clarification);
      try {
        await RemoteServices.deleteClarification(clarification.id);
        this.clarifications.splice(index, 1);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
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
      text: 'State',
      value: 'state',
      align: 'left',
      width: '5%'
    },
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
      width: '10%'
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
      align: 'left',
      width: '5%'
    },
    {
      text: 'Type',
      value: 'type',
      align: 'left',
      width: '10%'
    }
  ];
}
</script>

<style scoped></style>
