package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.mailer.Mailer
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.ExternalUserDto
import spock.lang.Unroll
import spock.mock.DetachedMockFactory

import java.time.LocalDateTime;

@DataJpaTest
class ConfirmRegistrationTest extends SpockTest {

    Course course
    CourseExecution courseExecution
    ExternalUserDto externalUserDto

    @Autowired
    Mailer mailerMock

    def setup(){
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL)
        courseExecutionRepository.save(courseExecution)

        def executionId = courseExecution.getId()

        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(USER_1_EMAIL)
        externalUserDto.setUsername(USER_1_EMAIL)
        externalUserDto.setConfirmationToken(USER_1_TOKEN)
        externalUserDto.setRole(User.Role.STUDENT)

        userServiceApplicational.createExternalUser(executionId, externalUserDto)

        User user = userService.findByUsername(USER_1_EMAIL)
        user.setConfirmationToken(USER_1_TOKEN)
    }

	def "user confirms registration successfully" () {
        given: "a password"
        externalUserDto.setPassword(USER_1_PASSWORD)

        when:
        def result = authServiceApplcational.confirmRegistration(externalUserDto)

        then:"the user has a new password and matches"
        passwordEncoder.matches(USER_1_PASSWORD, result.getPassword())
        and: "and is active"
        result.isActive()
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername,_,_,_)
	}

    def "user is already active" () {
        given: "an active user"
        externalUserDto.setPassword(USER_1_PASSWORD)
        authServiceApplcational.confirmRegistration(externalUserDto)

        when:
        authServiceApplcational.confirmRegistration(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.USER_ALREADY_ACTIVE
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername,_,_,_)
    }

    def "user token expired" () {
        given: "a new password"
        externalUserDto.setPassword(USER_1_PASSWORD)
        and: "and an expired token generation date"
        User user = userService.findByUsername(USER_1_EMAIL)
        user.setTokenGenerationDate(LocalDateTime.now().minusDays(1).minusMinutes(1))

        when:
        def result = authServiceApplcational.confirmRegistration(externalUserDto)

        then:
        result.active == false
        and: "a new token is created"
        result.confirmationToken != USER_1_TOKEN
        and: "a new confirmation mail is sent"
        1 * mailerMock.sendSimpleMail(mailerUsername, USER_1_EMAIL, userService.PASSWORD_CONFIRMATION_MAIL_SUBJECT,_)
    }

    @Unroll
	def "registration confirmation unsuccessful" () {
        given: "a user"
        externalUserDto = new ExternalUserDto()
        externalUserDto.setEmail(email)
        externalUserDto.setUsername(email)
        externalUserDto.setConfirmationToken(token)
        externalUserDto.setPassword(password)

        when:
        authServiceApplcational.confirmRegistration(externalUserDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == errorMessage
        and: "no mail is sent"
        0 * mailerMock.sendSimpleMail(mailerUsername,_,_,_)

        where:
        email        | password         | token           || errorMessage
        ""           | USER_1_PASSWORD  | USER_1_TOKEN    || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        null         | USER_1_PASSWORD  | USER_1_TOKEN    || ErrorMessage.EXTERNAL_USER_NOT_FOUND
        USER_1_EMAIL | null             | USER_1_TOKEN    || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | ""               | USER_1_TOKEN    || ErrorMessage.INVALID_PASSWORD
        USER_1_EMAIL | USER_1_PASSWORD  | ""              || ErrorMessage.INVALID_CONFIRMATION_TOKEN
	}


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {
        def mockFactory = new DetachedMockFactory()

        @Bean
        Mailer mailer(){
            return mockFactory.Mock(Mailer)
        }
    }
}
