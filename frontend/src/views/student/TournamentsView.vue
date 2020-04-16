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
        <span data-cy="enrollmentsTdP">{{ item.playersId.length }}</span>
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
            <v-list-item v-for="name in item.topicsName" :key="name" link>
              <v-list-item-title v-text="name"></v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip v-if="item.playersId.includes(userId)">
          <template v-slot:activator="{ on }">
            <v-icon color="green darken-2" class="mr-2" v-on="on"
              >fas fa-user-check</v-icon
            >
          </template>
        </v-tooltip>
        <v-tooltip v-else bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              color="primary"
              class="mr-2"
              v-on="on"
              @click="playerEnroll(item.id)"
              data-cy="enrollTdP"
              >fas fa-user-plus</v-icon
            >
          </template>
          <span>Enroll</span>
        </v-tooltip>
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
      width: '10%'
    },
    {
      text: 'Enrollments',
      value: 'playersId',
      align: 'center',
      width: '5%'
    },
    {
      text: 'Action',
      value: 'action',
      align: 'center',
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.getTournaments()).sort(
        (ta, tb) => {
          let ea = ta.playersId.includes(this.userId);
          let eb = tb.playersId.includes(this.userId);
          let ca = ta.creatorId == this.userId;
          let cb = tb.creatorId == this.userId;

          if (ea) return -1;
          if (eb) return 1;
          if (ca) return -1;
          if (cb) return 1;
          return -1;
        }
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async playerEnroll(id: number) {
    await this.$store.dispatch('loading');
    try {
      let idx = this.tournaments.findIndex(e => e.id === id);
      this.tournaments.splice(idx, 1, await RemoteServices.playerEnroll(id));
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped></style>
