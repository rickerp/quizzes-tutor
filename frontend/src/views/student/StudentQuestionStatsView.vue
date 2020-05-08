<template>
  <div class="container">
    <h2>Suggested Questions Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper">
          <animated-number :number="stats.total" />
        </div>
        <div class="project-name">
          <p>Total Suggestions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper">
          <animated-number :number="stats.accepted" />
        </div>
        <div class="project-name">
          <p>Accepted Suggestions</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper">
          <animated-number :number="(100 * stats.accepted) / stats.total"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Approval Rate</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import StudentQuestionDashboard from '@/models/studentquestion/StudentQuestionDashboard';

@Component({
  components: { AnimatedNumber }
})
export default class StudentQuestionStatsView extends Vue {
  stats: StudentQuestionDashboard | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getStudentQuestionDashBoard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
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
