package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import spock.lang.Specification

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@DataJpaTest
class GetPublicClarificationsStatsTest extends Specification {

    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"

    @Autowired
    ClarificationRequestService clarificationRequestService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    def courseExecution

    def setup() {
        def course = new Course()
        course.setName("course")
        courseRepository.save(course)

        courseExecution = new CourseExecution()
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)

        def quiz = new Quiz()
        quiz.setTitle("titleQuiz")
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)

        def question = new Question()
        question.setTitle("titleQuestion")
        question.setKey(1)
        questionRepository.save(question)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        1.upto(3, {
            def student = new User("Student" + it , "Student" + it, it.intValue(), User.Role.STUDENT)
            student.setClarificationDashState(User.DashBoardState.PUBLIC)
            student.addCourse(courseExecution)
            courseExecution.addUser(student)
            userRepository.save(student)

            def clarificationRequest = new ClarificationRequest()
            clarificationRequest.setState(ClarificationRequest.State.RESOLVED)
            clarificationRequest.setType(ClarificationRequest.Type.PUBLIC)
            clarificationRequest.setContent(CLARIFICATION_CONTENT)
            clarificationRequest.setUser(student)
            clarificationRequest.setQuestionAnswer(questionAnswer)
            clarificationRequestRepository.save(clarificationRequest)
        })
    }

    def "Student requests all Public Clarifications Stats"() {
        when:
        def publicStatsDto = clarificationRequestService.getPublicClarificationsStats(courseExecution.getId())

        then:
        publicStatsDto.size() == 3
    }

    def "Student requests all Public Clarifications Stats of a non existing Course Execution"() {
        when:
        clarificationRequestService.getPublicClarificationsStats(0)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
    }


    @TestConfiguration
    static class GetPublicClarificationsStatsContextConfiguration {

        @Bean
        ClarificationRequestService ClarificationRequestService() {
            return new ClarificationRequestService()
        }

        @Bean
        PublicClarificationService PublicClarificationService() {
            return new PublicClarificationService()
        }
    }
}
