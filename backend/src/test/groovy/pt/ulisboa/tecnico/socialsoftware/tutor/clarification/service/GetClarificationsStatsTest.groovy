package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import spock.lang.Shared
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
class GetClarificationsStatsTest extends Specification {

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

    @Shared
    def teacher
    @Shared
    def student

    def courseExecution
    def questionAnswer
    def clarificationRequest

    def setup() {
        def course = new Course()
        course.setName("course")
        courseRepository.save(course)

        courseExecution = new CourseExecution()
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)

        teacher = new User("Name", "Username", 1, User.Role.TEACHER)
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

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

        questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        student = new User("Student", "Student", 2, User.Role.STUDENT)
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setState(ClarificationRequest.State.RESOLVED)
        clarificationRequest.setType(ClarificationRequest.Type.PUBLIC)
        clarificationRequest.setContent(CLARIFICATION_CONTENT)
        clarificationRequest.setUser(student)
        clarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequestRepository.save(clarificationRequest)

        student.addClarification(clarificationRequest)
        teacher.addClarification(clarificationRequest)

    }

    def "A student requests Clarifications Statistics without making any Clarification Request"() {
        given: "A student with no Clarifications"
        def student2 = new User("Student2", "Student2", 3, User.Role.STUDENT)
        userRepository.save(student2)

        when:
        def clarificationsStats = clarificationRequestService.getClarificationsStats(student2.getId(), courseExecution.getId())

        then:
        clarificationsStats.getName() == student2.getName()
        clarificationsStats.getUsername() == student2.getUsername()
        clarificationsStats.getTotalClarificationRequests() == 0
        clarificationsStats.getPublicClarificationRequests() == 0
        clarificationsStats.getPercentageOfPublicClarifications() == 0
    }

    def "A student requests Clarifications Statistics"() {
        when:
        def clarificationsStats = clarificationRequestService.getClarificationsStats(student.getId(), courseExecution.getId())

        then:
        clarificationsStats.getName() == student.getName()
        clarificationsStats.getUsername() == student.getUsername()
        clarificationsStats.getTotalClarificationRequests() == 1
        clarificationsStats.getPublicClarificationRequests() == 1
        clarificationsStats.getPercentageOfPublicClarifications() == 100
    }

    def "A student requests Clarifications Statistics with one Public and a non Public Clarification"() {
        given: "A non Public Clarification"
        def newClarificationRequest = new ClarificationRequest()
        newClarificationRequest.setState(ClarificationRequest.State.UNRESOLVED)
        newClarificationRequest.setType(ClarificationRequest.Type.PRIVATE)
        newClarificationRequest.setContent(CLARIFICATION_CONTENT)
        newClarificationRequest.setUser(student)
        newClarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequestRepository.save(newClarificationRequest)
        student.addClarification(newClarificationRequest)

        when:
        def clarificationsStats = clarificationRequestService.getClarificationsStats(student.getId(), courseExecution.getId())

        then:
        clarificationsStats.getName() == student.getName()
        clarificationsStats.getUsername() == student.getUsername()
        clarificationsStats.getTotalClarificationRequests() == 2
        clarificationsStats.getPublicClarificationRequests() == 1
        clarificationsStats.getPercentageOfPublicClarifications() == 50
    }

    def "A teacher requests Clarifications Statistics"() {
        when:
        clarificationRequestService.getClarificationsStats(teacher.getId(), courseExecution.getId())

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT
    }

    def "A student requests Clarifications Statistics of a non existing Execution Course "() {
        when:
        clarificationRequestService.getClarificationsStats(student.getId(), 0)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND
    }


    @TestConfiguration
    static class GetClarificationsStatsContextConfiguration {

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
