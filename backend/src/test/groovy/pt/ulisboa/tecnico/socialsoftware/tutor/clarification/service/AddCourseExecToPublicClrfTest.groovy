package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import static pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.PublicClarificationDto.Availability.*
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.PublicClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import spock.lang.Specification

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean


@DataJpaTest
class AddCourseExecToPublicClrfTest extends Specification {

    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"

    @Autowired
    PublicClarificationService publicClarificationService

    @Autowired
    ClarificationRequestRepository clarificationRequestRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    PublicClarificationRepository pClrfRepository


    def courseExecution
    def pClrf
    def teacher
    def question

    def setup() {
        courseExecution = new CourseExecution()
        teacher = new User("teacher", "teacherName", 2, User.Role.TEACHER)
        teacher.addCourse(courseExecution)
        def student = new User("student", "studentName", 1, User.Role.STUDENT)
        student.addCourse(courseExecution)

        question = new Question()
        question.setTitle("titleQuestion")
        question.setKey(1)

        def clrReq = new ClarificationRequest()
        clrReq.setContent("Content")
        clrReq.setUser(student)
        clrReq.setState(ClarificationRequest.State.RESOLVED)
        clrReq.setType(ClarificationRequest.Type.PUBLIC)
        clrReq.setCreationDate(DateHandler.now())
        clarificationRequestRepository.save(clrReq)

        pClrf = new PublicClarification()
        pClrf.setClarificationRequest(clrReq)
        question.addPublicClarification(pClrf)
        pClrfRepository.save(pClrf)
        userRepository.save(student)
        userRepository.save(teacher)
        courseExecutionRepository.save(courseExecution)
        questionRepository.save(question)
    }

    def "A teacher adds its courseExecution to a Public Clarification that doesn't have it"() {
        when:
        def pClrfDto = publicClarificationService.addCourseExecToPublicClrf(pClrf.getId(), courseExecution.getId())

        then:
        pClrfDto.availability == VISIBLE
        def pClrf = pClrfRepository.findById(pClrf.getId()).orElseThrow()
        pClrf.getCourseExecutions().contains(courseExecution)
    }

    def "A teacher adds its courseExecution to a Public Clarification that already has it"() {
        given: "a public Clarification with the course execution of the teacher"
        pClrf.addCourseExecution(courseExecution)
        pClrfRepository.save(pClrf)

        when:
        publicClarificationService.addCourseExecToPublicClrf(pClrf.getId(), courseExecution.getId())

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.EXECUTION_ALREADY_VISIBLE
    }

    @TestConfiguration
    static class ClarificationRequestServiceImplTestContextConfiguration {

        @Bean
        PublicClarificationService publicClarificationService() {
            return new PublicClarificationService()
        }
    }
}