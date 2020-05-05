<template>
  <v-card
    :elevation="0"
    :style="showToolbar ? 'max-height: 650px;' : 'max-height: 500px;'"
    class="scrollbar mb-2 pb-2 'mt-0 pt-0 mr-0 pr-0' : !showToolbar"
  >
    <div v-for="request in requests" v-bind:key="request.id">
      <v-subheader class="font-weight-medium mt-3 mb-3">
        {{ request.creationDate }}
      </v-subheader>
      <v-divider :inset="true" class="mb-4"></v-divider>
      <v-hover v-slot:default="{ hover }" open-delay="200">
        <v-card shaped class="ml-2 mb-2 mr-12" :elevation="hover ? 12 : 3">
          <v-card-title class="-italic">
            {{ request.user.username }}
          </v-card-title>
          <v-card-text align="left" class="font-weight-bold">
            {{ request.content }}
          </v-card-text>
        </v-card>
      </v-hover>
      <span v-if="request.clarificationComments != null">
        <span
          v-for="comment in request.clarificationComments"
          :key="comment.id"
        >
          <v-subheader
            class="font-weight-medium"
            v-bind:class="{ 'justify-end': comment.user.role === 'TEACHER' }"
          >
            {{ comment.creationDate }}
          </v-subheader>
          <v-divider
            :inset="true"
            class="mb-4"
            v-bind:class="{ 'mr-0 ml-12': comment.user.role === 'STUDENT' }"
          ></v-divider>
          <v-hover v-slot:default="{ hover }" open-delay="200">
            <v-card
              shaped
              class=""
              v-bind:class="{
                'ml-12 mr-2 mb-2': comment.user.role === 'TEACHER',
                'ml-2 mb-2 mr-12': comment.user.role === 'STUDENT'
              }"
              :elevation="hover ? 12 : 3"
            >
              <v-card-title
                class="justify-end -italic"
                v-bind:class="{
                  'justify-end -italic': comment.user.role === 'TEACHER',
                  '-italic': comment.user.role === 'STUDENT'
                }"
              >
                {{ comment.user.username }}
              </v-card-title>
              <v-card-text align="left" class="font-weight-bold">
                {{ comment.content }}
              </v-card-text>
            </v-card>
          </v-hover>
        </span>
      </span>
    </div>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';

@Component
export default class ChatComponent extends Vue {
  @Prop({ type: Array, required: true })
  readonly requests!: ClarificationRequest[];
  @Prop() readonly showToolbar!: boolean;
}
</script>
