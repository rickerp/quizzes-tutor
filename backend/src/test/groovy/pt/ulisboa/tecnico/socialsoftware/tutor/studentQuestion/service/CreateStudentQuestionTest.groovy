package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

import javax.management.relation.Role

@DataJpaTest
class CreateStudentQuestionTest extends Specification {

    static final USERNAME = 'username'

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    CourseRepository courseRepository

    User user
    Course course

    def setup() {
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)
    }

    def "user not found"() {
        when:
        studentQuestionService.createStudentQuestion(null, course.getId(), new QuestionDto())
        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_FOUND
    }

    def "create studentQuestion with no question"() {
        when: "no question exists"
        studentQuestionService.createStudentQuestion(USERNAME, course.getId(), null)
        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.QUESTION_IS_EMPTY
    }

    def "createStudentQuestion" () {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle("title")
        questionDto.setContent("content")
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent("content")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)

        when: "a student question is created"
        def studentQuestionDto = studentQuestionService.createStudentQuestion(USERNAME, course.getId(), questionDto);

        then: "the question contains correct information"
        studentQuestionDto != null
        studentQuestionDto.question != null
        studentQuestionDto.question.title == questionDto.title
        studentQuestionDto.question.image == questionDto.image
    }

    @TestConfiguration
    static class CreateStudentQuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService();
        }

        @Bean
        UserService userService() {
            return new UserService();
        }

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
