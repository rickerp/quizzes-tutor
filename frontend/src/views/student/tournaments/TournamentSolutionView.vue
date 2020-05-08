<template>
  <div class="quiz-container" v-if="quiz.correctAnswers.length > 0">
    <div class="question-navigation">
      <div class="navigation-buttons">
        <span
          v-for="idx in +quiz.questions.length"
          v-bind:class="getQuestionClass(idx)"
          :key="idx"
          @click="gotoQuestion(idx - 1)"
        >
          {{ idx }}
        </span>
      </div>
      <span class="left-button" @click="prevQuestion" v-if="seq > 0"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        class="right-button"
        @click="nextQuestion"
        v-if="seq + 1 < quiz.questions.length"
        data-cy="NextQuestionTdP"
        ><i class="fas fa-chevron-right"
      /></span>
    </div>
    <result-component
      v-model="seq"
      :answer="quiz.answers[seq]"
      :correctAnswer="quiz.correctAnswers[seq]"
      :question="quiz.questions[seq]"
      :questionNumber="quiz.questions.length"
      @increase-order="nextQuestion"
      @decrease-order="prevQuestion"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import StatementTournamentQuiz from '@/models/statement/StatementTournamentQuiz';

@Component({
  components: {
    'result-component': ResultComponent
  }
})
export default class TournamentSolutionView extends Vue {
  quiz: StatementTournamentQuiz = StatementTournamentQuiz.getInstance;
  seq: number = 0;

  async created() {
    if (this.quiz.correctAnswers.length == 0) this.goBack();
  }

  async goBack() {
    StatementTournamentQuiz.newInstance();
    await this.$router.push({ name: 'tournaments-inprogress' });
  }

  getQuestionClass(idx: number): String[] {
    return [
      'question-button',
      idx === this.seq + 1 ? 'current-question-button' : '',
      idx === this.seq + 1 &&
      this.quiz.correctAnswers[idx - 1].correctOptionId !==
        this.quiz.answers[idx - 1].optionId
        ? 'incorrect-current'
        : '',
      this.quiz.correctAnswers[idx - 1].correctOptionId !==
      this.quiz.answers[idx - 1].optionId
        ? 'incorrect'
        : ''
    ];
  }

  nextQuestion() {
    if (this.seq + 1 < this.quiz.questions.length) this.seq++;
  }

  prevQuestion() {
    if (this.seq > 0) this.seq--;
  }

  gotoQuestion(newSeq: number) {
    this.seq = newSeq;
  }
}
</script>

<style lang="scss" scoped>
.incorrect {
  color: #cf2323 !important;
}

.incorrect-current {
  background-color: #cf2323 !important;
  color: #fff !important;
}
</style>
