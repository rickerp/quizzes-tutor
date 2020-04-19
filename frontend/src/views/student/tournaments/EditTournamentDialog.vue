<template>
  <v-dialog v-model="dialog" max-width="600px">
    <v-card>
      <v-card-title>
        <span class="headline">{{
          editTournament.id ? editTournament.name : 'New Tournament'
        }}</span>
      </v-card-title>
      <v-card-text>
        <v-container>
          <v-row
            ><v-col
              ><v-text-field
                v-model="editTournament.name"
                label="Name *"
              ></v-text-field></v-col
          ></v-row>
          <v-row>
            <v-col cols="3">
              <v-subheader>Start Date-Time *</v-subheader>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-menu
                v-model="startDateMenu"
                :close-on-content-click="false"
                transition="scale-transition"
                offset-y
                min-width="290px"
              >
                <template v-slot:activator="{ on }">
                  <v-text-field
                    v-model="startDate"
                    label="Start date"
                    prepend-icon="event"
                    readonly
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="startDate"
                  no-title
                  scrollable
                  :allowed-dates="startDateAllowed"
                  color="primary"
                  @input="startDateMenu = false"
                >
                </v-date-picker>
              </v-menu>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-menu
                ref="startTimeMenu"
                v-model="startTimeMenu"
                :close-on-content-click="false"
                :nudge-right="40"
                :return-value.sync="startTime"
                transition="scale-transition"
                offset-y
                max-width="290px"
                min-width="290px"
              >
                <template v-slot:activator="{ on }">
                  <v-text-field
                    v-model="startTime"
                    label="Start time"
                    prepend-icon="access_time"
                    readonly
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-time-picker
                  v-if="startTimeMenu"
                  v-model="startTime"
                  full-width
                  format="24hr"
                  @click:minute="$refs.startTimeMenu.save(startTime)"
                ></v-time-picker>
              </v-menu>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="3">
              <v-subheader>End Date-Time *</v-subheader>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-menu
                v-model="endDateMenu"
                :close-on-content-click="false"
                transition="scale-transition"
                offset-y
                min-width="290px"
              >
                <template v-slot:activator="{ on }">
                  <v-text-field
                    v-model="endDate"
                    label="End date"
                    prepend-icon="event"
                    readonly
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-date-picker
                  v-model="endDate"
                  no-title
                  scrollable
                  color="primary"
                  :allowed-dates="endDateAllowed"
                  @input="endDateMenu = false"
                >
                </v-date-picker>
              </v-menu>
            </v-col>
            <v-col cols="12" sm="6" md="4">
              <v-menu
                ref="endTimeMenu"
                v-model="endTimeMenu"
                :close-on-content-click="false"
                :nudge-right="40"
                :return-value.sync="endTime"
                transition="scale-transition"
                offset-y
                max-width="290px"
                min-width="290px"
              >
                <template v-slot:activator="{ on }">
                  <v-text-field
                    v-model="endTime"
                    label="End time"
                    prepend-icon="access_time"
                    readonly
                    v-on="on"
                  ></v-text-field>
                </template>
                <v-time-picker
                  v-if="endTimeMenu"
                  v-model="endTime"
                  full-width
                  format="24hr"
                  @click:minute="$refs.endTimeMenu.save(endTime)"
                ></v-time-picker>
              </v-menu>
            </v-col>
          </v-row>
          <v-row>
            <v-col>
              <v-slider
                v-model="editTournament.nrQuestions"
                label="Number of questions *"
                hint="Don't keep the tournament too long"
                min="1"
                max="100"
                thumb-label
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
                    <v-list-item-title v-html="data.item.name" />
                  </v-list-item-content>
                </template>
              </v-autocomplete>
            </v-col>
          </v-row>
        </v-container>
        <small>* indicates required field</small>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="blue darken-1"
          class="white--text"
          @click="$emit('dialog', false)"
          >Close</v-btn
        >
        <v-btn
          color="blue darken-1"
          class="white--text"
          @click="saveTournament()"
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
  startDateMenu: boolean = false;
  startTimeMenu: boolean = false;
  endDateMenu: boolean = false;
  endTimeMenu: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  get startDate() {
    return this.editTournament.startTime.split('T')[0];
  }
  set startDate(val) {
    if (val > this.endDate) this.endDate = val;
    this.editTournament.startTime =
      val + 'T' + this.editTournament.startTime.split('T')[1];
  }
  get startTime() {
    return new Date(this.editTournament.startTime)
      .toLocaleTimeString('pt')
      .slice(0, -3);
  }
  set startTime(val) {
    this.editTournament.startTime = new Date(
      this.startDate + 'T' + val
    ).toISOString();
    if (this.startDate === this.endDate && val >= this.endTime)
      this.endTime = new Date(
        new Date(this.editTournament.startTime).getTime() + 15 * 60000
      )
        .toLocaleTimeString('pt')
        .slice(0, -3);
  }
  get endDate() {
    return this.editTournament.endTime.split('T')[0];
  }
  set endDate(val) {
    this.editTournament.endTime =
      val + 'T' + this.editTournament.endTime.split('T')[1];
  }
  get endTime() {
    return new Date(this.editTournament.endTime)
      .toLocaleTimeString('pt')
      .slice(0, -3);
  }
  set endTime(val) {
    this.editTournament.endTime = new Date(
      this.endDate + 'T' + val
    ).toISOString();
  }

  startDateAllowed(val: string) {
    return (
      new Date(val) >= new Date(new Date().setDate(new Date().getDate() - 1))
    );
  }

  endDateAllowed(val: string) {
    return val >= this.startDate;
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
        'Tournament must have a name and at least one topic'
      );
      return;
    }

    if (new Date(this.editTournament.startTime) <= new Date()) {
      await this.$store.dispatch(
        'error',
        'Start time must be greater than current time'
      );
      return;
    }

    this.editTournament.startTime = this.startDate + ' ' + this.startTime;
    this.editTournament.endTime = this.endDate + ' ' + this.endTime;

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
        console.log('Erro na criação');
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
