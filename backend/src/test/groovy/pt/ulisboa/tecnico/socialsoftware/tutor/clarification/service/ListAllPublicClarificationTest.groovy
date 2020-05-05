package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler

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
class ListAllPublicClarificationTest extends Specification {

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
    PublicClarificationRepository publicClarificationRepository


    def courseExecution
    def publicClarifications
    def student
    def teacher
    def question

    def setup() {
        courseExecution = new CourseExecution()
        student = new User("student", "studentName", 1, User.Role.STUDENT)
        student.addCourse(courseExecution)
        courseExecution.addUser(student)

        teacher = new User("teacher", "teacherName", 2, User.Role.TEACHER)
        teacher.addCourse(courseExecution)

        question = new Question()
        question.setTitle("titleQuestion")
        question.setKey(1)

        publicClarifications = new ArrayList<PublicClarification>()

        for(def i = 0; i < 3; i++) {
            def clrReq = new ClarificationRequest()
            clrReq.setContent("Content")
            clrReq.setUser(student)
            clrReq.setState(ClarificationRequest.State.RESOLVED)
            clrReq.setType(ClarificationRequest.Type.PUBLIC)
            clrReq.setCreationDate(DateHandler.now())
            clarificationRequestRepository.save(clrReq)

            publicClarifications.add(new PublicClarification())
            publicClarifications[i].setClarificationRequest(clrReq)
            question.addPublicClarification(publicClarifications.get(i))
            publicClarificationRepository.save(publicClarifications.get(i))
        }

        userRepository.save(student)
        userRepository.save(teacher)
        courseExecutionRepository.save(courseExecution)
        questionRepository.save(question)
    }

    def "A student Lists publicClarifications of a question that has three publicClarifications, all associated with the course execution of the student"() {
        given: "three publicClarifications associated with the course execution of the student"
        for(def i = 0; i < 3; i++) {
            publicClarifications.get(i).addCourseExecution(courseExecution)
            publicClarificationRepository.save(publicClarifications.get(i))
        }

        when:
        def publicClrsList = publicClarificationService.listAllClarifications(student, courseExecution.getId(), question.getId())

        then:
        publicClrsList.size() == 3
        for(def i = 0; i < 3; i++) {
            publicClrsList[i].getAvailability() == VISIBLE
        }
    }

    def "A student Lists publicClarifications of a question that has three publicClarifications, one associated with the course execution of the student"() {
        given: "one publicClarifications associated with the course execution of the student"
        publicClarifications.get(0).addCourseExecution(courseExecution)
        publicClarificationRepository.save(publicClarifications.get(0))

        when:
        def publicClrsList = publicClarificationService.listAllClarifications(student, courseExecution.getId(), question.getId())
        then:
        publicClrsList.size() == 1
        publicClrsList[0].getAvailability() == VISIBLE

    }

    def "A student Lists publicClarifications of a question that has three publicClarifications, none associated with the course execution of the student"() {
        when:
        def publicClrsList = publicClarificationService.listAllClarifications(student, courseExecution.getId(), question.getId())

        then:
        publicClrsList.size() == 0
    }

    def "A teacher Lists publicClarifications of a question that has three publicClarifications, all associated with the course execution of the teacher"(){
        given: "three publicClarifications associated with the course execution of the teacher"
        for(def i = 0; i < 3; i++) {
            publicClarifications.get(i).addCourseExecution(courseExecution)
            publicClarificationRepository.save(publicClarifications.get(i))
        }

        when:
        def publicClrsList = publicClarificationService.listAllClarifications(teacher, courseExecution.getId(), question.getId())

        then:
        publicClrsList.size() == 3
        for(def i = 0; i < 3; i++) {
            publicClrsList[i].getAvailability() == VISIBLE
        }
    }

    def "A teacher Lists publicClarifications of a question that has three publicClarifications, one associated with the course execution of the teacher"() {
        given: "one publicClarifications associated with the course execution of the teacher"
        publicClarifications.get(0).addCourseExecution(courseExecution)
        publicClarificationRepository.save(publicClarifications.get(0))

        when:
        def publicClrsList = publicClarificationService.listAllClarifications(teacher, courseExecution.getId(), question.getId())

        then:
        publicClrsList.size() == 3
        for(def i = 0; i < 2; i++) {
            publicClrsList[i].getAvailability() == INVISIBLE
        }
        publicClrsList[2].getAvailability() == VISIBLE

    }

    def "A teacher Lists publicClarifications of a question that has three publicClarifications, none associated with the course execution of the teacher"() {
        when:
        def publicClrsList = publicClarificationService.listAllClarifications(teacher, courseExecution.getId(), question.getId())

        then:
        publicClrsList.size() == 3
        for(def i = 0; i < 3; i++) {
            publicClrsList[i].getAvailability() == INVISIBLE
        }
    }

    @TestConfiguration
    static class ClarificationRequestServiceImplTestContextConfiguration {

        @Bean
        PublicClarificationService PublicClarificationService() {
            return new PublicClarificationService()
        }
    }
}