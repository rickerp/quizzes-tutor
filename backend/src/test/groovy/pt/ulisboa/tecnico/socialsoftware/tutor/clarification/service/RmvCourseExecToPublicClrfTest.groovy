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
class RmvCourseExecToPublicClrfTest extends Specification {

    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"

    @Autowired
    PublicClarificationService pClrfService

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
        pClrf.addCourseExecution(courseExecution)
        pClrf.setClarificationRequest(clrReq)
        question.addPublicClarification(pClrf)

        pClrfRepository.save(pClrf)
        userRepository.save(teacher)
        userRepository.save(student)
        courseExecutionRepository.save(courseExecution)
        questionRepository.save(question)
    }

    def "A teacher removes its courseExecution to a Public Clarification that has this course execution"() {
        when:
        def pClrfDto = pClrfService.rmvCourseExecToPublicClrf(pClrf.getId(), courseExecution.getId())

        then:
        pClrfDto.availability == INVISIBLE
        def pClrf = pClrfRepository.findById(pClrf.getId()).orElseThrow()
        !pClrf.getCourseExecutions().contains(courseExecution)
    }

    def "A teacher removes its courseExecution to a Public Clarification that doesn't have it"() {
        given: "a public clarification without the course execution of the teacher"
        pClrf.removeCourseExecution(courseExecution)
        pClrfRepository.save(pClrf)

        when:
        pClrfService.rmvCourseExecToPublicClrf(pClrf.getId(), courseExecution.getId())

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.EXECUTION_ALREADY_INVISIBLE
    }

    @TestConfiguration
    static class ClarificationRequestServiceImplTestContextConfiguration {

        @Bean
        PublicClarificationService pClrfService() {
            return new PublicClarificationService()
        }
    }
}