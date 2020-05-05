<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="dashboards"
      :items-per-page="15"
      multi-sort
    >
      <template v-slot:top>
        <v-card-title>
          <v-switch class="ma-4" label="Participate in publish dashboard" />
        </v-card-title>
      </template>

      <template v-slot:item.rate="{ item }">
        <v-chip :color="percentageColor(item)" dark
          >{{ percentage(item) }}
        </v-chip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import RemoteServices from '@/services/RemoteServices';
import StudentQuestionDashboard from '../../models/studentquestion/StudentQuestionDashboard';

@Component({
  components: {}
})
export default class StudentQuestionsDashboardView extends Vue {
  headers = [
    { text: 'Name', value: 'name' },
    { text: 'Approved', value: 'accepted' },
    { text: 'Total', value: 'total' },
    { text: 'Acceptance', value: 'rate' }
  ];

  dashboards: StudentQuestionDashboard[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.dashboards = await RemoteServices.getAllStudentQuestionsDashBoard();
      this.dashboards.forEach((x: any) => {
        x.rate = x.accepted / x.total;
      });
      this.dashboards.sort((a, b) => b.accepted - a.accepted);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  percentage(item: StudentQuestionDashboard): string {
    if (item.total == 0) return '0%';
    return `${((item.accepted * 100) / item.total).toFixed(2)}%`;
  }

  percentageColor(item: StudentQuestionDashboard): string {
    if (item.total == 0) return 'gray';
    let rate = item.accepted / item.total;
    if (rate > 0.75) return 'green';
    if (rate > 0.5) return 'orange';
    return 'red';
  }
}
</script>

<style lang="scss" scoped></style>
