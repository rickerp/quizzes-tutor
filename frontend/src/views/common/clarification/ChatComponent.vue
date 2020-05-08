<template>
  <v-card
    :elevation="0"
    :style="showToolbar ? 'max-height: 650px;' : 'max-height: 500px;'"
    class="scrollbar mb-2 pb-2"
  >
    <div v-for="request in requests" v-bind:key="request.id">
      <v-subheader class="mt-3 mb-3">
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
            class="mt-3 mb-3"
            v-bind:class="{ 'justify-end': isTeacher(comment.user) }"
          >
            {{ comment.creationDate }}
          </v-subheader>
          <v-divider
            :inset="true"
            class="mb-4"
            v-bind:class="{ 'mr-0 ml-12': isStudent(comment.user) }"
          ></v-divider>
          <v-hover v-slot:default="{ hover }" open-delay="200">
            <v-card
              shaped
              class=""
              v-bind:class="{
                'ml-12 mr-2 mb-2': isTeacher(comment.user),
                'ml-2 mb-2 mr-12': isStudent(comment.user)
              }"
              :elevation="hover ? 12 : 3"
            >
              <v-card-title
                class="justify-end -italic"
                v-bind:class="{
                  'justify-end -italic': isTeacher(comment.user),
                  '-italic': isStudent(comment.user)
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
      <hr
        v-if="request.id !== requests[requests.length - 1].id"
        class="rounded mt-10 mb-10"
      />
    </div>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import User from '@/models/user/User';

@Component
export default class ChatComponent extends Vue {
  @Prop({ type: Array, required: true })
  readonly requests!: ClarificationRequest[];
  @Prop() readonly showToolbar!: boolean;

  isTeacher(user: User) {
    return user.role === 'TEACHER';
  }

  isStudent(user: User) {
    return user.role === 'STUDENT';
  }
}
</script>
<style scoped>
hr.rounded {
  border-top: 4px dotted #333333;
}
.v-subheader {
  color: black !important;
  font-size: 17px !important;
}
</style>
