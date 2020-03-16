package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared
import spock.lang.Unroll
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

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationCommentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationCommentService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationCommentRepository

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import java.time.LocalDateTime

@DataJpaTest
class SubmitCommentTest extends Specification {

    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"
    public static final String COMMENT_CONTENT = "Teacher Answer"

    @Autowired
    ClarificationCommentService commentService

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
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    ClarificationCommentRepository clarificationCommentRepository

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
        quiz.setKey(1)
        quiz.setCourseExecution(courseExecution)
        quizRepository.save(quiz)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setState(ClarificationRequest.State.UNRESOLVED)
        clarificationRequest.setContent(CLARIFICATION_CONTENT)
        clarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequestRepository.save(clarificationRequest)

        def creationDate = LocalDateTime.now()

        commentDto = new ClarificationCommentDto()
        commentDto.setContent(COMMENT_CONTENT)
        commentDto.setUserName(user.getUsername())
        commentDto.setClarificationId(clarificationRequest.getId())
        commentDto.setCreationDate(creationDate)
    }

    @Shared
    def user
    def clarificationRequest

    def commentDto

    def "submit a comment to a clarification request"() {
        when:
        commentService.createComment(commentDto)

        then: "the comment data is correct"
        def comment = clarificationCommentRepository.findComment(clarificationRequest.getId())
        comment.getId() != null
        comment.getContent() == commentDto.getContent()
        comment.getUser().getUsername() == commentDto.getUserName()
        comment.getClarificationRequest().getId() == commentDto.getClarificationId()
        comment.getCreationDate() == commentDto.getCreationDate();
    }

    def "submit a comment without a creationTime"() {
        given: "Update commentDto"
        commentDto.setCreationDate(null)

        when:
        commentService.createComment(commentDto)

        then:
        def comment = clarificationCommentRepository.findComment(clarificationRequest.getId())
        comment.getCreationDate() != null
    }

    @Unroll("Test: #clarificationId")
    def "submit comment to an non existing clarification request"() {
        given: "Update commentDto"
        commentDto.setClarificationId(clarificationId)

        when:
        commentService.createComment(commentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COMMENT_INVALID_CLARIFICATION

        where:
        clarificationId << [500, 0]
    }

    @Unroll("Test: #username | #content | clarificationRequestState || #message")
    def "submit comment with wrong arguments"() {
        given: "Update commentDto"
        commentDto.setUserName(username)
        commentDto.setContent(content)
        and: "ClarificationRequest update"
        clarificationRequest.setState(clarificationRequestState)

        when:
        commentService.createComment(commentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == message

        where:
            username       |    content      |       clarificationRequestState       ||         message
        "Username2"        | COMMENT_CONTENT | ClarificationRequest.State.UNRESOLVED || ErrorMessage.COMMENT_INVALID_USER
        null               | COMMENT_CONTENT | ClarificationRequest.State.UNRESOLVED || ErrorMessage.COMMENT_INVALID_USER
        user.getUsername() | null            | ClarificationRequest.State.UNRESOLVED || ErrorMessage.COMMENT_INVALID_CONTENT
        user.getUsername() | COMMENT_CONTENT | ClarificationRequest.State.RESOLVED   || ErrorMessage.COMMENT_INVALID_CLARIFICATION_STATE
    }

    def "submit an empty comment"() {
        when:
        commentService.createComment(null)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COMMENT_IS_EMPTY
    }

    def "student tries to submit comment to clarification request"() {
        given: "another user"
        def newUser = new User("Name2", "Username2", 2, User.Role.STUDENT)
        userRepository.save(newUser)
        and: "update clarification request"
        clarificationRequest.setUser(newUser)
        and: "update commentDto"
        commentDto.setUserName(newUser.getUsername())

        when:
        commentService.createComment(commentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COMMENT_INVALID_USER
    }

    def "teacher tries to submits comment to clarification request from different course"() {
        given: "A course"
        def courseUser = new Course()
        courseUser.setName("firstCourse")
        courseRepository.save(courseUser)
        and: "courseExecution"
        def courseExecUser = new CourseExecution()
        courseExecUser.setCourse(courseUser)
        courseExecutionRepository.save(courseExecUser)
        and: "Update user with course"
        Set<CourseExecution> courseExecutions = new HashSet<>()
        courseExecutions.add(courseExecUser)
        user.setCourseExecutions(courseExecutions)

        when:
        commentService.createComment(commentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COMMENT_INVALID_USER_COURSE
    }

    @TestConfiguration
    static class SubmitCommentTestContextConfiguration {

        @Bean
        ClarificationCommentService commentService() {
            return new ClarificationCommentService()
        }
    }
}