<template>
  <div>
    <div>
      <p></p>
      <v-spacer></v-spacer>
      <v-card justify-center color="#333333" class="ml-12 mr-12">
        <v-layout row wrap justify-center>
          <v-spacer></v-spacer>
          <h1 style="color: ghostwhite; padding-left:170px; padding-top: 6px;">
            Clarifications Statistics
          </h1>
          <v-spacer></v-spacer>
          <v-switch
            class="ml-12 pr-12"
            flat
            color="info"
            :dense="false"
            v-model="isPublic"
            @change="changeDashboardState()"
          >
            <template v-slot:label>
              <div>
                <strong class="grey--text text--lighten-2 ">
                  {{ isPublic ? 'Public' : 'Private' }}
                </strong>
              </div>
            </template>
          </v-switch>
        </v-layout>
      </v-card>
      <v-spacer></v-spacer>
    </div>
    <div v-if="this.stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper">
          <animated-number :number="this.stats.totalClarificationRequests" />
        </div>
        <div class="project-name">
          <p>Total Clarifications</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper">
          <animated-number :number="this.stats.publicClarificationRequests" />
        </div>
        <div class="project-name">
          <p>Public Clarifications</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper">
          <animated-number :number="stats.percentageOfPublicClarifications"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Percentage of Public Clarifications</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import { ClarificationStats } from '@/models/management/ClarificationStats';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: { AnimatedNumber }
})
export default class ClarificationDashboardView extends Vue {
  stats: ClarificationStats | null = null;
  isPublic: boolean = true;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getClarificationsStats();
      this.isPublic = this.stats.dashboardState == 'PUBLIC';
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeDashboardState() {
    let state = 'PRIVATE';
    if (this.isPublic) state = 'PUBLIC';

    if (this.stats) {
      try {
        this.stats = await RemoteServices.changeDashboardState(state);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style lang="scss" scoped>
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
    flex-basis: 25%;
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
  align-self: end;
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
