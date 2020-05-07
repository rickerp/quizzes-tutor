<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="publicStats"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
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
      <template v-slot:item.percentageOfPublicClarifications="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfPublicClarifications)"
          dark
          >{{ item.percentageOfPublicClarifications + '%' }}</v-chip
        >
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import { ClarificationStats } from '@/models/management/ClarificationStats';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class PublicClarificationsDashboardsView extends Vue {
  publicStats: ClarificationStats[] = [];

  search: string = '';
  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '40%' },
    {
      text: 'Total Clarifications',
      value: 'totalClarificationRequests',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Public Clarifications',
      value: 'publicClarificationRequests',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Percentage of Public Clarifications',
      value: 'percentageOfPublicClarifications',
      align: 'center',
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.publicStats = await RemoteServices.getPublicClarificationsStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getPercentageColor(percentage: number) {
    if (percentage < 25) return 'red';
    else if (percentage < 50) return 'orange';
    else if (percentage < 75) return 'lime';
    else return 'green';
  }
}
</script>
