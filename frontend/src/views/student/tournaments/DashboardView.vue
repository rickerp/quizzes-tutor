<template>
  <div class="container">
    <br />
    <v-card
      justify-center
      color="rgba(255, 255, 255, 0.85)"
      class="ml-12 mr-12 pa-2"
    >
      <v-layout class="dash-header">
        <h1 style="color: #2c3e50; margin-left: 40%;">
          Tournaments Statistics
        </h1>
        <v-spacer></v-spacer>
        <v-switch
          v-if="dash != null"
          v-model="dash.isPublic"
          class="mr-9"
          color="#2c3e50"
          :dense="true"
          :hide-details="true"
          :v-model="dash.isPublic"
          @change="setTournamentDashboardPrivacy()"
          data-cy="dashPrivacyTdP"
        >
          <template v-slot:label>
            <div>
              <h2 style="color: #2c3e50;">
                Public
              </h2>
            </div>
          </template>
        </v-switch>
      </v-layout>
    </v-card>
    <br />
    <div v-if="dash != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalTournaments">
          <animated-number :number="dash.tournamentScores.length" />
        </div>
        <div class="project-name">
          <p>Total Tournaments Participated</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalQuestions">
          <animated-number :number="dash.nrQuestions" />
        </div>
        <div class="project-name">
          <p>Total Questions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="(dash.nrAnswers * 100) / dash.nrQuestions"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Percentage of Questions Answered</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="correctAnswers">
          <animated-number
            :number="(dash.nrCorrectAnswers * 100) / dash.nrQuestions"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Percentage of Correct Answers</p>
        </div>
      </div>
    </div>
    <br />
    <v-card
      justify-center
      color="rgba(255, 255, 255, 0.85)"
      class="ml-12 mr-12 pa-2"
    >
      <h1 style="color: #2c3e50;">
        Participated Tournaments
      </h1>
    </v-card>
    <br />
    <v-data-table
      v-if="dash != null"
      :headers="headers"
      :items="dash.tournamentScores"
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
        </v-card-title>
      </template>

      <template v-slot:item.name="{ item }" data-cy="nameTdP"
        >{{ item.tournamentName }}
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
              <v-list-item-content>
                <v-list-item-title v-text="name"></v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>

      <template v-slot:item.score="{ item }">
        <span
          v-text="item.nrCorrectAnswers + ' / ' + item.nrQuestions"
          data-cy="score"
        ></span>
      </template>
    </v-data-table>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import TournamentDashboard from '@/models/management/TournamentDashboard';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';

@Component({
  components: { AnimatedNumber }
})
export default class StatsView extends Vue {
  dash: TournamentDashboard | null = null;
  search: string = '';
  headers: object = [
    {
      text: 'Name',
      value: 'name',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Finish Date',
      value: 'finishTime',
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
      text: 'Score',
      value: 'score',
      align: 'center',
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.dash = await RemoteServices.getTournamentDashboard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async setTournamentDashboardPrivacy() {
    if (this.dash == null) return;
    await this.$store.dispatch('loading');
    try {
      this.dash.isPublic = await RemoteServices.setTournamentDashboardPrivacy(
        this.dash.isPublic
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.dash-header * {
  margin-top: auto;
  margin-bottom: auto;
}

.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 20%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
