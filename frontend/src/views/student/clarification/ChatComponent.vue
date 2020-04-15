<template>
  <v-container class="'mt-0 pt-0 mr-0 pr-0' : !showToolbar">
    <v-toolbar v-if="showToolbar" color="#333333" dark>
      <v-icon style="padding-right: 20px"> fas fa-comments </v-icon>
      <v-toolbar-title>Clarification</v-toolbar-title>
    </v-toolbar>
    <v-list three-line
      style="max-height: 450px"
      class="scrollbar">
      <v-list-item-group>
        <template>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div>
            <v-subheader class="font-weight-medium">
              {{ request.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
          <div v-if="hasComment">
            <v-subheader class="font-weight-medium">
              {{ request.clarificationComment.creationDate }}
            </v-subheader>
            <v-divider :inset="true"></v-divider>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title class="text-left">
                  {{ request.clarificationComment.user.username }}
                </v-list-item-title>
                <v-list-item-subtitle class="font-weight-bold">
                  {{ request.clarificationComment.content }}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
          </div>
        </template>
      </v-list-item-group>
    </v-list>
  </v-container>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';

@Component
export default class ChatComponent extends Vue {
  @Prop(ClarificationRequest) readonly request!: ClarificationRequest;
  @Prop() readonly hasComment!: boolean;
  @Prop() readonly showToolbar!: boolean;
}
</script>
