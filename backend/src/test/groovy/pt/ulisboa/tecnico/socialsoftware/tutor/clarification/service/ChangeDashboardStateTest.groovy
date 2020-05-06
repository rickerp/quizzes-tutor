package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import spock.lang.Shared
import spock.lang.Specification

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@DataJpaTest
class ChangeDashboardStateTest extends Specification  {

    @Autowired
    ClarificationRequestService clarificationRequestService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Shared
    def teacher
    @Shared
    def student

    def courseExecution

    def setup() {
        def course = new Course()
        course.setName("course")
        courseRepository.save(course)

        courseExecution = new CourseExecution()
        courseExecution.setCourse(course)
        courseExecutionRepository.save(courseExecution)

        student = new User("Student", "Student", 2, User.Role.STUDENT)
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        teacher = new User("Name", "Username", 1, User.Role.TEACHER)
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)
    }


    def "A student changes Clarification Dashboard State to Public"() {
        when:
        clarificationRequestService.changeDashboardState(student, courseExecution.getId(), "Public")

        then:
        student.getClarificationDashState() == User.DashBoardState.PUBLIC
    }

    def "A student changes Clarification Dashboard State to Private"() {
        when:
        clarificationRequestService.changeDashboardState(student, courseExecution.getId(),"Private")

        then:
        student.getClarificationDashState() == User.DashBoardState.PRIVATE

    }

    def "A student tries to change Clarification Dashboard State to an invalid state"() {
        when:
        clarificationRequestService.changeDashboardState(student, courseExecution.getId(), "State")

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_DASHBOARD_INVALID_STATE
    }

    def "A teacher tries to change Clarification Dashboard State"() {
        when:
        clarificationRequestService.changeDashboardState(teacher, courseExecution.getId(),"Public")

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_NOT_STUDENT
    }

    @TestConfiguration
    static class ChangeDashboardStateContextConfiguration {

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