<template>
  <div>
    <div>
      <clarification-list
        v-if="!createMode"
        @newClarification="newClarification"
        :clarifications="clarifications"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import ClarificationList from '@/views/student/clarification/ClarificationList.vue';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';

@Component({
  components: {
    ClarificationList
  }
})
export default class StudentClarificationsView extends Vue {
  clarifications: ClarificationRequest[] = [];
  createMode: boolean = false;
  async created() {
    await this.$store.dispatch('loading');
    try {
      this.clarifications = await RemoteServices.getClarifications();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  newClarification() {
    this.createMode = true;
  }
}
</script>

<style lang="scss" scoped></style>
