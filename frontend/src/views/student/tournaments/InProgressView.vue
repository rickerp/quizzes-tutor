<template>
  <v-card class="table">
    <tournaments-table
      v-model="calendar"
      :tournaments="tournaments"
      v-on:start-quiz="startQuiz"
    ></tournaments-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';
import RemoteServices from '@/services/RemoteServices';
import TournamentsTable from '@/components/TournamentsTable.vue';
import StatementTournamentQuiz from '@/models/statement/StatementTournamentQuiz';

@Component({
  components: {
    'tournaments-table': TournamentsTable
  }
})
export default class InProgressView extends Vue {
  tournaments: Tournament[] = [];
  calendar: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getInProgressTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async gotoQuiz() {
    await this.$router.push({ name: 'tournament-quiz' });
  }

  async startQuiz(item: Tournament) {
    StatementTournamentQuiz.newInstance();
    StatementTournamentQuiz.getInstance.tournamentId = item.id;
    this.gotoQuiz();
  }
}
</script>
