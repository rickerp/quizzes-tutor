<template>
  <v-card class="table">
    <tournaments-table
      v-model="calendar"
      :tournaments="tournaments"
      v-on:new-tournament="newTournament"
      v-on:enroll-player="enrollPlayer"
      v-on:cancel-tournament="removeTournament"
    ></tournaments-table>
    <edit-tournament-dialog
      v-if="currentTournament"
      v-model="editDialog"
      :tournament="currentTournament"
      v-on:save-tournament="onSaveTournament"
      v-on:close-dialog="onCloseDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';
import RemoteServices from '@/services/RemoteServices';
import EditTournamentDialog from '@/views/student/tournaments/EditTournamentDialog.vue';
import TournamentsTable from '@/components/TournamentsTable.vue';

@Component({
  components: {
    'tournaments-table': TournamentsTable,
    'edit-tournament-dialog': EditTournamentDialog
  }
})
export default class CalendarView extends Vue {
  userId: number = this.$store.getters.getUser.id;
  tournaments: Tournament[] = [];
  calendar: boolean = true;
  editDialog: boolean = false;
  currentTournament: Tournament | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = (await RemoteServices.getOpenedTournaments()).sort(
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

  newTournament() {
    this.editDialog = true;
    this.currentTournament = new Tournament();
  }

  async onSaveTournament(tournament: Tournament) {
    /* FOR FUTURE EDIT TOURNAMENT (update)
    this.tournaments = this.tournaments.filter(q => q.id !== tournament.id);
    */
    this.tournaments.unshift(tournament);
    this.editDialog = false;
    this.currentTournament = null;
  }

  onCloseDialog() {
    this.editDialog = false;
    this.currentTournament = null;
  }

  async enrollPlayer(item: Tournament) {
    if (confirm('Are you sure you want to Enroll in "' + item.name + '"?')) {
      await this.$store.dispatch('loading');
      try {
        let idx = this.tournaments.findIndex(e => e.id === item.id);
        this.tournaments.splice(
          idx,
          1,
          await RemoteServices.enrollPlayer(item.id)
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async removeTournament(item: Tournament) {
    if (confirm('Are you sure you want to Cancel "' + item.name + '"?')) {
      await this.$store.dispatch('loading');
      try {
        await RemoteServices.removeTournament(item.id);
        let idx = this.tournaments.findIndex(e => e.id === item.id);
        this.tournaments.splice(idx, 1);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }
}
</script>
