import axios from 'axios';
import Store from '@/store';
import Question from '@/models/management/Question';
import { Quiz } from '@/models/management/Quiz';
import Course from '@/models/user/Course';
import StatementCorrectAnswer from '@/models/statement/StatementCorrectAnswer';
import StudentStats from '@/models/statement/StudentStats';
import StatementQuiz from '@/models/statement/StatementQuiz';
import SolvedQuiz from '@/models/statement/SolvedQuiz';
import Topic from '@/models/management/Topic';
import { Student } from '@/models/management/Student';
import Assessment from '@/models/management/Assessment';
import AuthDto from '@/models/user/AuthDto';
import StatementAnswer from '@/models/statement/StatementAnswer';
import { QuizAnswers } from '@/models/management/QuizAnswers';
import StudentQuestion from '@/models/studentquestion/StudentQuestion';
import Evaluation from '@/models/studentquestion/Evaluation';
import Tournament from '@/models/management/Tournament';
import { ClarificationRequest } from '@/models/management/ClarificationRequest';
import { ClarificationComment } from '@/models/management/ClarificationComment';
import TournamentQuiz from '@/models/management/TournamentQuiz';
import TournamentDashboard from '@/models/management/TournamentDashboard';
import { PublicClarification } from '@/models/management/PublicClarification';
import { ClarificationStats } from '@/models/management/ClarificationStats';
import StudentQuestionDashboard from '@/models/studentquestion/StudentQuestionDashboard';

const httpClient = axios.create();
httpClient.defaults.timeout = 10000;
httpClient.defaults.baseURL = process.env.VUE_APP_ROOT_API;
httpClient.defaults.headers.post['Content-Type'] = 'application/json';
httpClient.interceptors.request.use(
  config => {
    if (!config.headers.Authorization) {
      const token = Store.getters.getToken;

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  error => Promise.reject(error)
);

export default class RemoteServices {
  static async fenixLogin(code: string): Promise<AuthDto> {
    return httpClient
      .get(`/auth/fenix?code=${code}`)
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoStudentLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/student')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoTeacherLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/teacher')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async demoAdminLogin(): Promise<AuthDto> {
    return httpClient
      .get('/auth/demo/admin')
      .then(response => {
        return new AuthDto(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createTournament(tournament: Tournament): Promise<Tournament> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments`,
        tournament
      )
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getOpenedTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments`
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getInProgressTournaments(): Promise<Tournament[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/ongoing`
      )
      .then(response => {
        return response.data.map((tournament: any) => {
          return new Tournament(tournament);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async enrollPlayer(id: number): Promise<Tournament> {
    return httpClient
      .put(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/${id}`
      )
      .then(response => {
        return new Tournament(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async removeTournament(id: number) {
    return httpClient
      .delete(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/${id}/cancel`
      )
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async startTournamentQuiz(id: number): Promise<TournamentQuiz> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/${id}/quiz/begin`
      )
      .then(response => {
        return new TournamentQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async selectQuestionOption(id: number, answer: StatementAnswer) {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/${id}/quiz/answer`,
        answer
      )
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async finishTournamentQuiz(
    id: number
  ): Promise<StatementCorrectAnswer[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/${id}/quiz/finish`
      )
      .then(response => {
        return response.data.map((answer: any) => {
          return new StatementCorrectAnswer(answer);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getTournamentDashboard(): Promise<TournamentDashboard> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/tournaments/dashboard`
      )
      .then(response => {
        return new TournamentDashboard(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setTournamentDashboardPrivacy(
    isPublic: boolean
  ): Promise<boolean> {
    return httpClient
      .post('/tournaments/privacy', isPublic)
      .then(response => {
        return response.data;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getUserStats(): Promise<StudentStats> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/stats`
      )
      .then(response => {
        return new StudentStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuestions(): Promise<Question[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/questions`)
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportCourseQuestions(): Promise<Blob> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/export`,
        {
          responseType: 'blob'
        }
      )
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuestions(): Promise<Question[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/available`
      )
      .then(response => {
        return response.data.map((question: any) => {
          return new Question(question);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createQuestion(question: Question): Promise<Question> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/questions/`,
        question
      )
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestion(question: Question): Promise<Question> {
    return httpClient
      .put(`/questions/${question.id}`, question)
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuestion(questionId: number) {
    return httpClient.delete(`/questions/${questionId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async setQuestionStatus(
    questionId: number,
    status: String
  ): Promise<Question> {
    return httpClient
      .post(`/questions/${questionId}/set-status`, status, {})
      .then(response => {
        return new Question(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async uploadImage(file: File, questionId: number): Promise<string> {
    let formData = new FormData();
    formData.append('file', file);
    return httpClient
      .put(`/questions/${questionId}/image`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      .then(response => {
        return response.data as string;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateQuestionTopics(questionId: number, topics: Topic[]) {
    return httpClient.put(`/questions/${questionId}/topics`, topics);
  }

  static async getTopics(): Promise<Topic[]> {
    return httpClient
      .get(`/courses/${Store.getters.getCurrentCourse.courseId}/topics`)
      .then(response => {
        return response.data.map((topic: any) => {
          return new Topic(topic);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableQuizzes(): Promise<StatementQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/available`
      )
      .then(response => {
        return response.data.map((statementQuiz: any) => {
          return new StatementQuiz(statementQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async generateStatementQuiz(params: object): Promise<StatementQuiz> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/generate`,
        params
      )
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getSolvedQuizzes(): Promise<SolvedQuiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/solved`
      )
      .then(response => {
        return response.data.map((solvedQuiz: any) => {
          return new SolvedQuiz(solvedQuiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizByQRCode(quizId: number): Promise<StatementQuiz> {
    return httpClient
      .get(`/quizzes/${quizId}/byqrcode`)
      .then(response => {
        return new StatementQuiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportQuiz(quizId: number): Promise<Blob> {
    return httpClient
      .get(`/quizzes/${quizId}/export`, {
        responseType: 'blob'
      })
      .then(response => {
        return new Blob([response.data], {
          type: 'application/zip, application/octet-stream'
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async startQuiz(quizId: number) {
    return httpClient.get(`/quizzes/${quizId}/start`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async submitAnswer(quizId: number, answer: StatementAnswer) {
    return httpClient
      .post(`/quizzes/${quizId}/submit`, answer)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async concludeQuiz(
    quizId: number
  ): Promise<StatementCorrectAnswer[] | void> {
    return httpClient
      .get(`/quizzes/${quizId}/conclude`)
      .then(response => {
        if (response.data) {
          return response.data.map((answer: any) => {
            return new StatementCorrectAnswer(answer);
          });
        }
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/topics/`,
        topic
      )
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async updateTopic(topic: Topic): Promise<Topic> {
    return httpClient
      .put(`/topics/${topic.id}`, topic)
      .then(response => {
        return new Topic(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteTopic(topic: Topic) {
    return httpClient.delete(`/topics/${topic.id}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async getNonGeneratedQuizzes(): Promise<Quiz[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes/non-generated`
      )
      .then(response => {
        return response.data.map((quiz: any) => {
          return new Quiz(quiz);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteQuiz(quizId: number) {
    return httpClient.delete(`/quizzes/${quizId}`).catch(async error => {
      throw Error(await this.errorMessage(error));
    });
  }

  static async deleteClarification(clarificationId: number) {
    return httpClient
      .delete(`/clarifications/${clarificationId}/remove`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuiz(quizId: number): Promise<Quiz> {
    return httpClient
      .get(`/quizzes/${quizId}`)
      .then(response => {
        return new Quiz(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getQuizAnswers(quizId: number): Promise<QuizAnswers> {
    return httpClient
      .get(`/quizzes/${quizId}/answers`)
      .then(response => {
        return new QuizAnswers(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveQuiz(quiz: Quiz): Promise<Quiz> {
    if (quiz.id) {
      return httpClient
        .put(`/quizzes/${quiz.id}`, quiz)
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/quizzes`,
          quiz
        )
        .then(response => {
          return new Quiz(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async getCourseStudents(course: Course) {
    return httpClient
      .get(`/executions/${course.courseExecutionId}/students`)
      .then(response => {
        return response.data.map((student: any) => {
          return new Student(student);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAssessments(): Promise<Assessment[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAvailableAssessments() {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments/available`
      )
      .then(response => {
        return response.data.map((assessment: any) => {
          return new Assessment(assessment);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async saveAssessment(assessment: Assessment) {
    if (assessment.id) {
      return httpClient
        .put(`/assessments/${assessment.id}`, assessment)
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    } else {
      return httpClient
        .post(
          `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/assessments`,
          assessment
        )
        .then(response => {
          return new Assessment(response.data);
        })
        .catch(async error => {
          throw Error(await this.errorMessage(error));
        });
    }
  }

  static async deleteAssessment(assessmentId: number) {
    return httpClient
      .delete(`/assessments/${assessmentId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setAssessmentStatus(
    assessmentId: number,
    status: string
  ): Promise<Assessment> {
    return httpClient
      .post(`/assessments/${assessmentId}/set-status`, status, {
        headers: {
          'Content-Type': 'text/html'
        }
      })
      .then(response => {
        return new Assessment(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static getCourses(): Promise<Course[]> {
    return httpClient
      .get('/courses/executions')
      .then(response => {
        return response.data.map((course: any) => {
          return new Course(course);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async activateCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/courses/activate', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getClarifications(): Promise<ClarificationRequest[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/clarifications`
      )
      .then(response => {
        return response.data.map((request: any) => {
          return new ClarificationRequest(request);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getPublicClarifications(
    questionId: number
  ): Promise<PublicClarification[]> {
    const execId = Store.getters.getCurrentCourse.courseExecutionId;
    return httpClient
      .get(`/questions/${questionId}/publicClarifications/executions/${execId}`)
      .then(response => {
        return response.data.map((request: any) => {
          return new PublicClarification(request);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async addCourseExecutionToPublicClarification(
    publicClarificationId: number,
    questionId: number
  ): Promise<PublicClarification> {
    const execId = Store.getters.getCurrentCourse.courseExecutionId;
    return httpClient
      .post(
        `/questions/${questionId}/publicClarifications/` +
          `${publicClarificationId}/add`,
        execId
      )
      .then(response => {
        return new PublicClarification(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async removeCourseExecutionToPublicClarification(
    publicClarificationId: number,
    questionId: number
  ): Promise<PublicClarification> {
    const execId = Store.getters.getCurrentCourse.courseExecutionId;
    return httpClient
      .post(
        `/questions/${questionId}/publicClarifications/` +
          `${publicClarificationId}/remove`,
        execId
      )
      .then(response => {
        return new PublicClarification(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createClarification(
    questionAnswerId: number,
    request: ClarificationRequest
  ): Promise<ClarificationRequest> {
    return httpClient
      .post(`/questionAnswers/${questionAnswerId}/clarifications`, request)
      .then(response => {
        return new ClarificationRequest(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeClarificationState(
    clrfReqId: number,
    state: string
  ): Promise<ClarificationRequest> {
    return httpClient
      .post(`/clarifications/${clrfReqId}/set-state`, state)
      .then(response => {
        return new ClarificationRequest(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeClarificationType(
    clrfReqId: number,
    type: string
  ): Promise<ClarificationRequest> {
    return httpClient
      .post(`/clarifications/${clrfReqId}/set-type`, type)
      .then(response => {
        return new ClarificationRequest(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createClarificationComment(
    clarificationRequestId: number,
    clarificationComment: ClarificationComment
  ): Promise<ClarificationComment> {
    return httpClient
      .post(
        `/clarifications/${clarificationRequestId}/comment`,
        clarificationComment
      )
      .then(response => {
        return new ClarificationComment(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getClarificationsStats(): Promise<ClarificationStats> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/clarifications/clarificationsStats`
      )
      .then(response => {
        return new ClarificationStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async changeDashboardState(
    state: string
  ): Promise<ClarificationStats> {
    return httpClient
      .post(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/clarifications/clarificationsStats/${state}`
      )
      .then(response => {
        return new ClarificationStats(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getPublicClarificationsStats(): Promise<ClarificationStats[]> {
    return httpClient
      .get(
        `/executions/${Store.getters.getCurrentCourse.courseExecutionId}/clarifications/publicClarificationsStats`
      )
      .then(response => {
        return response.data.map((request: any) => {
          return new ClarificationStats(request);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async createExternalCourse(course: Course): Promise<Course> {
    return httpClient
      .post('/courses/external', course)
      .then(response => {
        return new Course(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async deleteCourse(courseExecutionId: number | undefined) {
    return httpClient
      .delete(`/executions/${courseExecutionId}`)
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async exportAll() {
    return httpClient
      .get('/admin/export', {
        responseType: 'blob'
      })
      .then(response => {
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        let dateTime = new Date();
        link.setAttribute(
          'download',
          `export-${dateTime.toLocaleString()}.zip`
        );
        document.body.appendChild(link);
        link.click();
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async errorMessage(error: any): Promise<string> {
    if (error.message === 'Network Error') {
      return 'Unable to connect to server';
    } else if (error.message.split(' ')[0] === 'timeout') {
      return 'Request timeout - Server took too long to respond';
    } else if (error.response) {
      return error.response.data.message;
    } else if (error.message === 'Request failed with status code 403') {
      await Store.dispatch('logout');
      return 'Unauthorized access or Expired token';
    } else {
      console.log(error);
      return 'Unknown Error - Contact admin';
    }
  }

  static async publishStudentQuestion(
    studentQuestion: StudentQuestion
  ): Promise<StudentQuestion> {
    return httpClient
      .post(`/studentquestions/${studentQuestion.id}/publish`)
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async submitStudentQuestion(
    studentQuestion: StudentQuestion
  ): Promise<StudentQuestion> {
    return httpClient
      .post(
        `/courses/${Store.getters.getCurrentCourse.courseId}/studentquestion`,
        studentQuestion
      )
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async editQuestion(
    studentQuestion: StudentQuestion
  ): Promise<StudentQuestion> {
    return httpClient
      .put(
        `/courses/${Store.getters.getCurrentCourse.courseId}/editstudentquestion`,
        studentQuestion
      )
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async reSubmitQuestion(
    studentQuestion: StudentQuestion
  ): Promise<StudentQuestion> {
    return httpClient
      .put(`/studentquestions/${studentQuestion.id}/resubmit`, studentQuestion)
      .then(response => {
        return new StudentQuestion(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async listStudentQuestions(): Promise<StudentQuestion[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/studentquestions`
      )
      .then(response => {
        return response.data.map((studentQuestion: any) => {
          return new StudentQuestion(studentQuestion);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async listCourseStudentQuestions(): Promise<StudentQuestion[]> {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/coursestudentquestions`
      )
      .then(response => {
        return response.data.map((studentQuestion: any) => {
          return new StudentQuestion(studentQuestion);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async submitStudentQuestionEvaluation(
    evaluation: Evaluation,
    id: number
  ): Promise<Evaluation> {
    return httpClient
      .post(`/studentquestions/${id}/evaluation`, evaluation)
      .then(response => {
        return new Evaluation(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getAllStudentQuestionsDashBoard(): Promise<
    StudentQuestionDashboard[]
  > {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/studentquestionsdashboard/all`
      )
      .then(response => {
        return response.data.map((course: any) => {
          return new StudentQuestionDashboard(course);
        });
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getDashboardVisibilitry(): Promise<boolean> {
    return httpClient
      .get('/studentquestionsdashboard/visibility')
      .then(response => {
        return !!response.data;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async setDashboardVisibilitry(isPublic: boolean): Promise<boolean> {
    return httpClient
      .post('/studentquestionsdashboard/visibility', isPublic)
      .then(response => {
        return !!response.data;
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }

  static async getStudentQuestionDashBoard(): Promise<
    StudentQuestionDashboard
  > {
    return httpClient
      .get(
        `/courses/${Store.getters.getCurrentCourse.courseId}/studentquestionsdashboard`
      )
      .then(response => {
        return new StudentQuestionDashboard(response.data);
      })
      .catch(async error => {
        throw Error(await this.errorMessage(error));
      });
  }
}
