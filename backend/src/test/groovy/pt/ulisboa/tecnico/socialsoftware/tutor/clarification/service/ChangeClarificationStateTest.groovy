package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest
import static pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest.State.*

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationCommentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared

@DataJpaTest
class ChangeClarificationStateTest extends Specification {

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

    @Autowired
    ClarificationCommentRepository clarificationCommentRepository

    @Shared
    def user

    def clarificationRequest

    def setup() {
        def course = new Course()
        course.setName("course")
        courseRepository.save(course)

        def courseExecution = new CourseExecution()
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)

        user = new User("Name", "Username", 1, User.Role.TEACHER)
        user.addCourse(courseExecution)
        courseExecution.addUser(user)
        userRepository.save(user)

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

        def student = new User("Student", "Student", 2, User.Role.STUDENT)
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setContent(CLARIFICATION_CONTENT)
        clarificationRequest.setUser(student)
        clarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequestRepository.save(clarificationRequest)
    }

    def "Change Clarification Request state to RESOLVED when it had UNRESOLVED"() {
        given: "a clarification request with state UNRESOLVED"
        clarificationRequest.setState(UNRESOLVED)
        clarificationRequestRepository.save(clarificationRequest)

        when:
        def clarificationReqDto = clarificationRequestService.changeClarificationState(clarificationRequest.getId(), RESOLVED.toString())

        then:
        def clarificationRequestCreated = clarificationRequestRepository.findById(clarificationRequest.getId()).orElseThrow()
        clarificationRequestCreated.getState() == RESOLVED;
        clarificationReqDto.getState() == RESOLVED
    }

    def "Change Clarification Request state to UNRESOLVED, when it had RESOLVED"() {
        given: "a clarification request with state RESOLVED"
            clarificationRequest.setState(RESOLVED)
            clarificationRequestRepository.save(clarificationRequest)

        when:
        def clarificationReqDto = clarificationRequestService.changeClarificationState(clarificationRequest.getId(), UNRESOLVED.toString())

        then:
        def clarificationRequestCreated = clarificationRequestRepository.findById(clarificationRequest.getId()).orElseThrow()
        clarificationRequestCreated.getState() == UNRESOLVED;
        clarificationReqDto.getState() == UNRESOLVED
    }

    def "Change a Clarification Request state to an invalid state"() {
        when:
        clarificationRequestService.changeClarificationState(clarificationRequest.getId(), "INVALID STATE")

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_INVALID_STATE
    }

    @Unroll("Test: #originalState #changeState")
    def "Change Clarification Request state to the same state that already is"() {
        given:
        clarificationRequest.setState(originalState)
        clarificationRequestRepository.save(clarificationRequest)

        when:
        clarificationRequestService.changeClarificationState(clarificationRequest.getId(), changeState)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_ALREADY_IN_THIS_STATE

        where:
        originalState << [RESOLVED, UNRESOLVED]
        changeState << ["RESOLVED", "UNRESOLVED"]
    }

    @TestConfiguration
    static class ClarificationRequestServiceImplTestContextConfiguration {

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
