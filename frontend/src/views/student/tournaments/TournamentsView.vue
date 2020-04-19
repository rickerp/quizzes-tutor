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
          <v-btn color="primary" @click="newTournament">New Tournament</v-btn>
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
        <v-tooltip v-else>
          <template v-slot:activator="{ on }">
            <v-icon
              color="primary"
              class="mr-2"
              v-on="on"
              @click="playerEnroll(item)"
              data-cy="enrollTdP"
              >fas fa-user-plus</v-icon
            >
          </template>
          <span>Enroll</span>
        </v-tooltip>
      </template>
    </v-data-table>
    <edit-tournament-dialog
      v-if="currentTournament"
      v-model="editDialog"
      :tournament="currentTournament"
      v-on:save-tournament="onSaveTournament"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';
import RemoteServices from '@/services/RemoteServices';
import EditTournamentDialog from '@/views/student/tournaments/EditTournamentDialog.vue';

@Component({
  components: {
    'edit-tournament-dialog': EditTournamentDialog
  }
})
export default class TournamentsView extends Vue {
  userId: number = this.$store.getters.getUser.id;
  tournaments: Tournament[] = [];
  search: string = '';
  editDialog: boolean = false;
  currentTournament: Tournament | null = null;
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

  newTournament() {
    this.currentTournament = new Tournament();

    this.currentTournament.startTime = new Date(
      new Date().getTime() + 15 * 60000
    )
      .toLocaleString('se-SE')
      .slice(0, -3);

    this.currentTournament.endTime = new Date(new Date().getTime() + 30 * 60000)
      .toLocaleString('se-SE')
      .slice(0, -3);

    this.editDialog = true;
  }

  @Watch('editDialog')
  closeError() {
    if (!this.editDialog) {
      this.currentTournament = null;
    }
  }

  async onSaveTournament(tournament: Tournament) {
    // FOR EDIT TOURNAMENT (update): this.tournaments = this.tournaments.filter(q => q.id !== tournament.id);
    this.tournaments.unshift(tournament);
    this.editDialog = false;
    this.currentTournament = null;
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.getTournaments()).sort(
        (ta, tb) => {
          if (ta.playersId.includes(this.userId)) return -1;
          if (tb.playersId.includes(this.userId)) return 1;
          if (ta.creatorId == this.userId) return -1;
          if (tb.creatorId == this.userId) return 1;
          return -1;
        }
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async playerEnroll(item: Tournament) {
    if (confirm('Are you sure you want to enroll in "' + item.name + '"?')) {
      await this.$store.dispatch('loading');
      try {
        let idx = this.tournaments.findIndex(e => e.id === item.id);
        this.tournaments.splice(
          idx,
          1,
          await RemoteServices.playerEnroll(item.id)
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }
}
</script>

<style lang="scss" scoped></style>
