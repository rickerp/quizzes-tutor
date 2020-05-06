<template style="height: 100%">
  <div
    v-if="!finished"
    tabindex="0"
    class="quiz-container"
    @keydown.right="nextQuestion"
    @keydown.left="prevQuestion"
  >
    <header>
      <span class="timer" @click="showTime = !showTime">
        <i class="fas fa-clock"></i>
        <span v-if="showTime">{{ quizTimer }}</span>
      </span>
      <span
        class="end-quiz"
        @click="confirmDialog = true"
        data-cy="FinishQuizTdP"
        ><i class="fas fa-flag-checkered" />Finish</span
      >
    </header>
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
      <span v-if="seq > 0" class="left-button" @click="prevQuestion"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        v-if="seq + 1 < quiz.questions.length"
        class="right-button"
        @click="nextQuestion"
        data-cy="NextQuestionTdP"
        ><i class="fas fa-chevron-right"
      /></span>
    </div>
    <question-component
      v-if="quiz.answers[seq]"
      v-model="seq"
      :optionId="quiz.answers[seq].optionId"
      :question="quiz.questions[seq]"
      :questionNumber="quiz.questions.length"
      :backsies="true"
      @increase-order="nextQuestion"
      @decrease-order="prevQuestion"
      @select-option="selectOption"
    />
    <v-dialog v-model="confirmDialog" width="50%">
      <v-card>
        <v-card-title primary-title class="secondary white--text headline">
          Confirmation
        </v-card-title>
        <v-card-text class="text--black title">
          <br />
          Are you sure you want to Finish?
          <br />
          <span v-if="getUnansweredQuestions() > 0">
            You still have
            {{ getUnansweredQuestions() }}
            unanswered questions!
          </span>
        </v-card-text>
        <v-divider />
        <v-card-actions>
          <v-spacer />
          <v-btn color="secondary" text @click="confirmDialog = false">
            Cancel
          </v-btn>
          <v-btn color="primary" text @click="finishQuiz" data-cy="Im-SureTdP">
            I'm sure
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import QuestionComponent from '@/views/student/quiz/QuestionComponent.vue';
import RemoteServices from '@/services/RemoteServices';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';
import StatementTournamentQuiz from '@/models/statement/StatementTournamentQuiz';

@Component({
  components: {
    'question-component': QuestionComponent
  }
})
export default class QuizTournamentView extends Vue {
  finished: boolean = false;
  confirmDialog: boolean = false;
  quiz: StatementTournamentQuiz = StatementTournamentQuiz.getInstance;
  seq: number = 0;
  showTime: boolean = true;
  swapTime: Date = new Date();
  tickTime: number = Date.now();
  quizTimer: string = '';

  async created() {
    await this.$store.dispatch('loading');
    try {
      if (this.quiz.tournamentId < 0) this.goBack();
      else {
        StatementTournamentQuiz.quizSetup = await RemoteServices.startTournamentQuiz(
          this.quiz.tournamentId
        );
      }
      this.setQuizTimer();
    } catch (error) {
      await this.$store.dispatch('error', error);
      this.goBack();
    }
    await this.$store.dispatch('clearLoading');
  }

  setQuizTimer() {
    let interval = setInterval(() => {
      this.quiz.timeToEnd -= Math.floor(Date.now() - this.tickTime);

      if (this.quiz.timeToEnd > 0) {
        this.quizTimer = milisecondsToHHMMSS(this.quiz.timeToEnd);
        this.tickTime = Date.now();
      } else {
        clearInterval(interval);
        this.finishQuiz();
      }
    }, 1000);
  }

  getQuestionClass(idx: number): String[] {
    return [
      'question-button',
      idx === this.seq + 1 ? 'current-question-button' : ''
    ];
  }

  async goBack() {
    StatementTournamentQuiz.newInstance();
    await this.$router.push({ name: 'tournaments-inprogress' });
  }

  async gotoResults() {
    await this.$router.push({ name: 'tournament-solution' });
  }

  nextQuestion() {
    if (this.seq + 1 < this.quiz.questions.length) {
      this.addTime();
      this.seq++;
    }
  }

  prevQuestion() {
    if (this.seq > 0) {
      this.addTime();
      this.seq--;
    }
  }

  addTime() {
    this.quiz.answers[this.seq].timeTaken +=
      new Date().getTime() - this.swapTime.getTime();
    this.swapTime = new Date();
  }

  gotoQuestion(newSeq: number) {
    this.addTime();
    this.seq = newSeq;
  }

  async selectOption(optionId: number) {
    try {
      this.addTime();
      if (this.quiz.answers[this.seq].optionId == optionId) {
        this.quiz.answers[this.seq].optionId = null;
      } else this.quiz.answers[this.seq].optionId = optionId;

      await RemoteServices.selectQuestionOption(
        this.quiz.tournamentId,
        this.quiz.answers[this.seq]
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
      this.goBack();
    }
  }

  getUnansweredQuestions(): number {
    return this.quiz.answers.filter(answer => answer.optionId == null).length;
  }

  async finishQuiz() {
    await this.$store.dispatch('loading');
    try {
      this.addTime();
      this.finished = true;
      StatementTournamentQuiz.quizSolution = await RemoteServices.finishTournamentQuiz(
        this.quiz.tournamentId
      );
    } catch (error) {
      await this.$store.dispatch('error', error);
      this.goBack();
    }
    await this.$store.dispatch('clearLoading');
    this.gotoResults();
  }
}
</script>
