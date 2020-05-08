<template>
  <v-dialog
    :value="dialog"
    @input="$emit('close-dialog')"
    @keydown.esc="$emit('close-dialog')"
    max-width="50%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{
          editTournament.id ? editTournament.name : 'New Tournament'
        }}</span>
      </v-card-title>
      <v-card-text>
        <v-container fluid>
          <v-row
            ><v-col
              ><v-text-field
                v-model="editTournament.name"
                label="Name *"
                data-cy="newTdPName"
              ></v-text-field></v-col
          ></v-row>
          <v-row>
            <v-col>
              <VueCtkDateTimePicker
                label="Start Date-Time *"
                id="startDateInput"
                v-model="editTournament.startTime"
                format="YYYY-MM-DDTHH:mm:ssZ"
                data-cy="newTdPStartTime"
              ></VueCtkDateTimePicker>
            </v-col>
            <v-col>
              <VueCtkDateTimePicker
                label="End Date-Time *"
                id="endDateInput"
                v-model="editTournament.endTime"
                format="YYYY-MM-DDTHH:mm:ssZ"
                data-cy="newTdPEndTime"
              ></VueCtkDateTimePicker>
            </v-col>
          </v-row>
          <v-row class="mt-8">
            <v-col cols="3"
              ><v-subheader>Number Of Questions *</v-subheader></v-col
            >
            <v-col>
              <v-slider
                v-model="editTournament.nrQuestions"
                hint="Don't keep the tournament too long"
                min="1"
                max="100"
                thumb-label
              >
                <template v-slot:append>
                  <v-text-field
                    v-model="editTournament.nrQuestions"
                    class="mt-0 pt-0 ml-1"
                    hide-details
                    single-line
                    min="1"
                    max="100"
                    type="number"
                    style="width: 40px"
                    data-cy="newTdPNrQuestions"
                  ></v-text-field></template
              ></v-slider>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="3">
              <v-subheader>Topics *</v-subheader>
            </v-col>
            <v-col>
              <v-autocomplete
                v-model="editTournament.topicsId"
                :items="topics"
                multiple
                item-value="id"
                data-cy="newTdPTopicsMenu"
              >
                <template v-slot:selection="data">
                  <v-chip
                    v-bind="data.attrs"
                    :input-value="data.selected"
                    close
                    @click="data.select"
                    @click:close="removeTopic(data.item)"
                  >
                    {{ data.item.name }}
                  </v-chip>
                </template>
                <template v-slot:item="data">
                  <v-list-item-content>
                    <v-list-item-title
                      v-html="data.item.name"
                      data-cy="newTdPTopic"
                    />
                  </v-list-item-content>
                </template>
              </v-autocomplete>
            </v-col>
          </v-row>
        </v-container>
        <span>* Indicates Required Field</span>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue darken-1"
          class="white--text"
          @click="$emit('close-dialog')"
          >Close</v-btn
        >
        <v-btn
          color="blue darken-1"
          class="white--text"
          @click="saveTournament()"
          data-cy="newTdPSave"
          >Save</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Tournament from '@/models/management/Tournament';
import Topic from '@/models/management/Topic';
import RemoteServices from '../../../services/RemoteServices';

@Component
export default class EditTournamentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Tournament, required: true }) readonly tournament!: Tournament;

  topics: Topic[] = [];
  editTournament: Tournament = new Tournament(this.tournament);

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  removeTopic(topic: Topic) {
    this.editTournament.topicsId = this.editTournament.topicsId.filter(
      element => element != topic.id
    );
  }

  async saveTournament() {
    if (
      this.editTournament &&
      !(
        this.editTournament.name &&
        this.editTournament.startTime &&
        this.editTournament.endTime &&
        this.editTournament.nrQuestions &&
        this.editTournament.topicsId &&
        this.editTournament.topicsId.length
      )
    ) {
      await this.$store.dispatch(
        'error',
        'Tournament must have a Name, Start-Time, End-Time and at least One Topic'
      );
      return;
    }

    if (this.editTournament.startTime <= new Date().toISOString()) {
      await this.$store.dispatch(
        'error',
        'Start-Time must be greater than Current-Time'
      );
      return;
    }

    if (this.editTournament.startTime >= this.editTournament.endTime) {
      await this.$store.dispatch(
        'error',
        'End-Time must be greater than Start-Time'
      );
      return;
    }

    if (this.editTournament && this.editTournament.id) {
      /* FOR FUTURE EDIT TOURNAMENT (update)
      try {
        const result = await RemoteServices.updateTournament(
          this.editTournament
        );
        this.$emit('save-Tournament', result);

      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      */
    } else if (this.editTournament) {
      try {
        const result = await RemoteServices.createTournament(
          this.editTournament
        );
        this.$emit('save-tournament', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>

<style scoped>
.v-dialog {
  overflow: visible !important;
}
</style>
