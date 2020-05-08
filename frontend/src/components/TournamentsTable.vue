<template>
  <v-data-table
    :headers="headers"
    :items="tournaments"
    :search="search"
    :mobile-breakpoint="0"
    :items-per-page="15"
    :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    multi-sort
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
        <div v-if="calendar">
          <v-btn
            color="primary"
            @click="$emit('new-tournament')"
            data-cy="newTdPButton"
            >New Tournament</v-btn
          >
        </div>
      </v-card-title>
    </template>

    <template v-slot:item.creatorName="{ item }">
      <span
        v-if="item.creatorId === userId"
        class="font-italic font-weight-medium"
        v-text="item.creatorName"
      >
      </span>
      <span v-else v-text="item.creatorName"> </span>
    </template>

    <template v-slot:item.playersId="{ item }">
      <span v-text="item.playersId.length" data-cy="enrollmentsTdP"></span>
    </template>

    <template v-slot:item.topicsName="{ item }">
      <v-menu transition="slide-y-transition">
        <template v-slot:activator="{ on }">
          <v-btn color="primary" class="mr-2" v-on="on" data-cy="topicsTdP">
            {{ item.topicsName.length }}
            <v-icon color="white" class="mr-2" v-on="on">category</v-icon>
            <v-icon color="white" class="mr-2" v-on="on" small
              >fas fa-chevron-down</v-icon
            >
          </v-btn>
        </template>
        <v-list>
          <v-list-item v-for="name in item.topicsName" :key="name">
            <v-list-item-title v-text="name"></v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </template>

    <template v-slot:item.action="{ item }">
      <div v-if="calendar">
        <v-tooltip v-if="item.playersId.includes(userId)">
          <template v-slot:activator="{ on }">
            <v-icon large color="green darken-2" class="mr-2" v-on="on"
              >fas fa-user-check</v-icon
            >
          </template>
        </v-tooltip>
        <v-tooltip v-else bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              color="primary"
              class="mr-2"
              v-on="on"
              @click="$emit('enroll-player', item)"
              data-cy="enrollTdP"
              >fas fa-user-plus</v-icon
            >
          </template>
          <span>Enroll</span>
        </v-tooltip>
        <v-tooltip v-if="item.creatorId === userId" bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              color="red"
              class="mr-2"
              v-on="on"
              @click="$emit('cancel-tournament', item)"
              data-cy="cancelTdP"
              >fas fa-trash</v-icon
            >
          </template>
          <span>Cancel</span>
        </v-tooltip>
      </div>
      <div v-else>
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              x-large
              color="primary"
              class="mr-2"
              v-on="on"
              @click="$emit('start-quiz', item)"
              data-cy="playTdP"
              >fas fa-gamepad</v-icon
            >
          </template>
          <span>Play</span>
        </v-tooltip>
      </div>
    </template>
  </v-data-table>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';

@Component
export default class TournamentsTable extends Vue {
  @Model('calendar', Boolean) calendar!: boolean;
  @Prop({ type: Array, required: true })
  readonly tournaments!: Tournament[];

  userId: number = this.$store.getters.getUser.id;
  search: string = '';
  headers: object = [
    {
      text: 'Action',
      value: 'action',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Name',
      value: 'name',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Creator',
      value: 'creatorName',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Start Date',
      value: 'startTime',
      align: 'center',
      width: '10%'
    },
    {
      text: 'End Date',
      value: 'endTime',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Questions',
      value: 'nrQuestions',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Topics',
      value: 'topicsName',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Enrollments',
      value: 'playersId',
      align: 'center',
      width: '10%'
    }
  ];
}
</script>

<style lang="scss" scoped></style>
