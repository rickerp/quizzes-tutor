package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest

import static pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest.State.*
import static pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest.Type.*
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationCommentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification

@DataJpaTest
class RemoveClarificationRequestTest extends Specification {

    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"

    @Autowired
    ClarificationRequestService clarificationRequestService

    @Autowired
    PublicClarificationService publicClarificationService

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

    def student
    def clarificationRequest

    def setup() {
        def course = new Course()
        course.setName("course")
        courseRepository.save(course)

        def courseExecution = new CourseExecution()
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)

        student = new User("Student", "Student", 2, User.Role.STUDENT)
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        def questionAnswer = new QuestionAnswer();
        questionAnswerRepository.save(questionAnswer)

        clarificationRequest = new ClarificationRequest()
        clarificationRequest.setContent(CLARIFICATION_CONTENT)
        clarificationRequest.setUser(student)
        clarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequest.setType(PRIVATE)
        clarificationRequestRepository.save(clarificationRequest)
    }

    def "Delete a clarification request"() {
        when:
        clarificationRequestService.removeClarification(clarificationRequest.getId())

        then:
        def student = userRepository.findById(student.getId()).orElseThrow()
        !student.getClarificationRequests().contains(clarificationRequest)
    }

    def "Delete a clarification that already has Comments"() {
        given: "a clarification request with a comment"
        def comment = new ClarificationComment()
        comment.setClarificationRequest(clarificationRequest)
        clarificationCommentRepository.save(comment)
        clarificationRequest.addClarificationComment(comment)
        clarificationRequestRepository.save(clarificationRequest)
        when:
        clarificationRequestService.removeClarification(clarificationRequest.getId())

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_HAS_COMMENTS
    }

    def "Delete a clarification that is already Public"() {
        given: "a Clarification public"
        clarificationRequest.setState(RESOLVED)
        clarificationRequest.setType(PUBLIC)

        when:
        clarificationRequestService.removeClarification(clarificationRequest.getId())

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_IS_PUBLIC;
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
