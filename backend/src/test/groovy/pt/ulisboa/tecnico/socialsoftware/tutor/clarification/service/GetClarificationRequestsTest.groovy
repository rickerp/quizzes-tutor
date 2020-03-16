package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationCommentService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationCommentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository

import spock.lang.Specification

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationCommentRepository

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import spock.lang.Unroll


@DataJpaTest
class GetClarificationRequestsTest extends Specification {

    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"

    @Autowired
    ClarificationRequestService clarificationRequestService

    @Autowired
    ClarificationCommentService clarificationCommentService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    ClarificationCommentRepository clarificationCommentRepository

    def courseExecution
    def student
    def teacher
    def quiz
    def questionAnswer1
    def questionAnswer2

    def clarificationRequestDto

    def setup() {
        courseExecution = new CourseExecution()
        student = new User("student", "studentname", 1, User.Role.STUDENT)
        student.addCourse(courseExecution)

        teacher = new User("teacher", "teacherName", 2, User.Role.TEACHER)
        teacher.addCourse(courseExecution)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setCourseExecution(courseExecution)
        quiz.setType(Quiz.QuizType.GENERATED)

        def question = new Question()
        question.setKey(1)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        def quizQuestion2 = new QuizQuestion(quiz, question, 1)
        def quizAnswer = new QuizAnswer(student, quiz)
        quizAnswer.completed = true
        def questionAnswers = quizAnswer.getQuestionAnswers()
        questionAnswer1 = questionAnswers[0]
        questionAnswer2 = questionAnswers[1]

        courseExecutionRepository.save(courseExecution)
        userRepository.save(student)
        userRepository.save(teacher)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizQuestionRepository.save(quizQuestion2)
        quizAnswerRepository.save(quizAnswer)

        clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setState(ClarificationRequest.State.UNRESOLVED)
        clarificationRequestDto.setUserName(student.getUsername())

    }

    def "A student that has two clarifications requests of the same course execution"() {
        given: "Create two Clarifications"
        clarificationRequestService.createClarificationRequest(questionAnswer1.getId(), clarificationRequestDto)
        clarificationRequestService.createClarificationRequest(questionAnswer2.getId(), clarificationRequestDto)

        when:
            def clarificationRequestList = clarificationRequestService.getClarificationRequests(student.getUsername(), courseExecution.getId())

        then:
            clarificationRequestList.size() == 2
    }

    def "A teacher that has a student that made two clarifications requests of the same course execution"() {
        given: "Create two Clarifications"
        clarificationRequestService.createClarificationRequest(questionAnswer1.getId(), clarificationRequestDto)
        clarificationRequestService.createClarificationRequest(questionAnswer2.getId(), clarificationRequestDto)

        when:
        def clarificationRequestList = clarificationRequestService.getClarificationRequests(teacher.getUsername(), courseExecution.getId())

        then:
        clarificationRequestList.size() == 2
    }

    def "A teacher that has two students each with one clarification request from the same course execution"() {
        given: "A new Student"
        def newStudent = new User("newStudent", "newStudentName", 3, User.Role.STUDENT)
        student.addCourse(courseExecution)
        userRepository.save(newStudent)
        and: "A new Quiz Answer"
        def quizAnswer = new QuizAnswer(newStudent, quiz)
        quizAnswer.completed = true
        def questionAnswer2 = quizAnswer.getQuestionAnswers()[1]
        quizAnswerRepository.save(quizAnswer)
        and: "Two Clarifications"
        clarificationRequestService.createClarificationRequest(questionAnswer1.getId(), clarificationRequestDto)
        clarificationRequestDto.setUserName(newStudent.getUsername())
        clarificationRequestService.createClarificationRequest(questionAnswer2.getId(), clarificationRequestDto)

        when:
        def clarificationRequestList = clarificationRequestService.getClarificationRequests(teacher.getUsername(), courseExecution.getId())

        then:
        clarificationRequestList.size() == 2
    }

    def "A teacher that has no student clarifications requests"(){
        when:
        def clarificationRequestList = clarificationRequestService.getClarificationRequests(teacher.getUsername(), courseExecution.getId())

        then:
        clarificationRequestList.size() == 0
    }

    def "A Student that has no clarifications requests"() {
        when:
        def clarificationRequestList = clarificationRequestService.getClarificationRequests(student.getUsername(), courseExecution.getId())
        then:
        clarificationRequestList.size() == 0
    }

    def "Get a clarification request with a clarification comment using a clarificationId"() {
        given: "Create a Clarification request"
        def clarificationRequestDto = clarificationRequestService.createClarificationRequest(questionAnswer1.getId(), clarificationRequestDto)
        and: "Create a Clarification Comment"
        def clarificationCommentDto = new ClarificationCommentDto()
        clarificationCommentDto.setContent("Content")
        clarificationCommentDto.setUserName(teacher.getUsername())
        clarificationCommentDto.setClarificationId(clarificationRequestDto.getId())
        clarificationCommentDto = clarificationCommentService.createComment(clarificationCommentDto)

        when:
        def clarificationRequestDtoReceived = clarificationRequestService.getClarificationRequest(clarificationRequestDto.getId())

        then:
        clarificationRequestDto.getId() == clarificationRequestDtoReceived.getId()
        clarificationCommentDto.getId() == clarificationRequestDtoReceived.getClarificationComment().getId()
    }

    @TestConfiguration
    static class ClarificationRequestServiceImplTestContextConfiguration {

        @Bean
        ClarificationRequestService ClarificationRequestService() {
            return new ClarificationRequestService()
        }

        @Bean
        ClarificationCommentService clarificationCommentService() {
            return new ClarificationCommentService()
        }
    }
}