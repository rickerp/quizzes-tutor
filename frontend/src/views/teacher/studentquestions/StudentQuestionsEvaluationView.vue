<template>
  <questions-view :teacher-view="true"> </questions-view>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import MyQuestionsView from '@/views/student/MyQuestionsView.vue';

@Component({
  components: {
    'questions-view': MyQuestionsView
  }
})
export default class StudentQuestionEvaluationView extends Vue {}
</script>

<style lang="scss" scoped></style>
