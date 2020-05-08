package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
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

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler

@DataJpaTest
class SubmitClarificationCommentTest extends Specification {

    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"
    public static final String COMMENT_CONTENT = "Teacher Answer"

    @Autowired
    ClarificationCommentService clarificationCommentService

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
    def teacher
    @Shared
    def student

    def clarificationRequest
    def clarificationCommentDto

    def setup() {
        def course = new Course()
        course.setName("course")
        courseRepository.save(course)

        def courseExecution = new CourseExecution()
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

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        student = new User("Student", "Student", 2, User.Role.STUDENT)
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setState(ClarificationRequest.State.UNRESOLVED)
        clarificationRequest.setType(ClarificationRequest.Type.PRIVATE)
        clarificationRequest.setContent(CLARIFICATION_CONTENT)
        clarificationRequest.setUser(student)
        clarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequestRepository.save(clarificationRequest)

        def creationDate = DateHandler.now()

        clarificationCommentDto = new ClarificationCommentDto()
        clarificationCommentDto.setContent(COMMENT_CONTENT)
        clarificationCommentDto.setUser(new UserDto(teacher))
        clarificationCommentDto.setCreationDate(DateHandler.toISOString(creationDate))
    }

    def "teacher submits a comment to a clarification request"() {
        when:
        clarificationCommentService.createClarificationComment(clarificationRequest.getId(), clarificationCommentDto)

        then: "the comment data is correct"
        def comment = clarificationCommentRepository.findComments(clarificationRequest.getId()).get(0)
        comment.getId() != null
        comment.getContent() == clarificationCommentDto.getContent()
        comment.getUser().getUsername() == teacher.getUsername()
        DateHandler.toISOString(comment.getCreationDate()) == clarificationCommentDto.getCreationDate()
    }

    def "student submits comment to clarification request"() {
        given: "Update clarification request"
        clarificationRequest.setUser(student)
        and: "update commentDto"
        clarificationCommentDto.setUser(new UserDto(student))

        when:
        clarificationCommentService.createClarificationComment(clarificationRequest.getId(), clarificationCommentDto)

        then:
        def comment = clarificationCommentRepository.findComments(clarificationRequest.getId()).get(0)
        comment.getId() != null
        comment.getContent() == clarificationCommentDto.getContent()
        comment.getUser().getUsername() == student.getUsername()
        DateHandler.toISOString(comment.getCreationDate()) == clarificationCommentDto.getCreationDate()
    }

    def "submit a comment without a creationTime"() {
        given: "Update commentDto"
        clarificationCommentDto.setCreationDate(null)

        when:
        clarificationCommentService.createClarificationComment(clarificationRequest.getId(), clarificationCommentDto)

        then:
        def comment = clarificationCommentRepository.findComments(clarificationRequest.getId()).get(0)
        comment.getCreationDate() != null
    }

    @Unroll("Test: #clarificationId")
    def "submit comment to an non existing clarification request"() {
        given: "update clarification request"
        clarificationRequest.setId(clarificationId)
        when:
        clarificationCommentService.createClarificationComment(clarificationRequest.getId(), clarificationCommentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COMMENT_INVALID_CLARIFICATION

        where:
        clarificationId << [500, 0]
    }

    @Unroll("Test: #content | clarificationRequestState || #message")
    def "submit comment with wrong arguments"() {
        given: "Update commentDto"
        clarificationCommentDto.setContent(content)
        and: "ClarificationRequest update"
        if (clarificationRequest.getState() != ClarificationRequest.State.RESOLVED)
            clarificationRequest.setState(ClarificationRequest.State.RESOLVED)
        if (clarificationRequest.getType() != clarificationRequestType)
            clarificationRequest.setType(clarificationRequestType)

        when:
        clarificationCommentService.createClarificationComment(clarificationRequest.getId(), clarificationCommentDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == message

        where:
            content     |       clarificationRequestType       ||         message
        null            | ClarificationRequest.Type.PRIVATE     || ErrorMessage.COMMENT_INVALID_CONTENT
        COMMENT_CONTENT | null                                  || ErrorMessage.COMMENT_INVALID_CLARIFICATION_TYPE
        COMMENT_CONTENT | ClarificationRequest.Type.PUBLIC      || ErrorMessage.COMMENT_INVALID_CLARIFICATION_TYPE
    }

    def "submit an empty comment"() {
        when:
        clarificationCommentService.createClarificationComment(clarificationRequest.getId(), null)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.COMMENT_IS_EMPTY
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
        teacher.setCourseExecutions(courseExecutions)

        when:
        clarificationCommentService.createClarificationComment(clarificationRequest.getId(), clarificationCommentDto)

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

        @Bean
        PublicClarificationService PublicClarificationService() {
            return new PublicClarificationService()
        }
    }
}