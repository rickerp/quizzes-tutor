<template>
  <v-card class="table">
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
          <v-btn color="primary" dark data-cy="createButton"
            >New Tournament</v-btn
          >
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
        <span
          v-if="item.playersId.length === 0"
          class="font-italic font-weight-medium red--text"
          >None</span
        >
        <span
          v-else
          class="font-weight-medium green--text"
          v-text="item.playersId.length"
        >
        </span>
      </template>

      <template v-slot:item.topicsName="{ item }">
        <v-hover v-slot:default="{ hover }">
          <v-list class="pa-0" :color="hover ? 'white' : 'transparent'">
            <v-list-group>
              <v-icon slot="prependIcon" color="primary">category</v-icon>
              <v-list-item v-for="name in item.topicsName" :key="name" link>
                <v-list-item-title v-text="name"></v-list-item-title>
              </v-list-item>
            </v-list-group>
          </v-list>
        </v-hover>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class TournamentsView extends Vue {
  userId: number = this.$store.getters.getUser.id;
  tournaments: Tournament[] = [];
  search: string = '';
  headers: object = [
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
      text: 'Enrollments',
      value: 'playersId',
      align: 'center',
      width: '5%'
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
      width: '5%'
    },
    {
      text: 'Topics',
      value: 'topicsName',
      align: 'center',
      width: '5%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      console.log(this.userId);
      this.tournaments = (await RemoteServices.getTournaments()).sort(
        (ta, tb) => {
          if (ta.creatorId == this.userId) return -1;
          if (tb.creatorId == this.userId) return 1;
          if (ta.playersId.includes(this.userId)) return -1;
          if (tb.playersId.includes(this.userId)) return 1;
          return -1;
        }
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped></style>
