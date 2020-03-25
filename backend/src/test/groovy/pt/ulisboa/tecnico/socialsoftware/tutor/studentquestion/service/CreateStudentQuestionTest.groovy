package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import spock.lang.Specification

@DataJpaTest
class CreateStudentQuestionTest extends Specification {

    static final USERNAME = 'username'

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    Course course
    User user

    def setup() {
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)
    }

    def "user not found"() {
        given:
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setStudent(123)
        studentQuestionDto.setQuestion(new QuestionDto())
        when:
        studentQuestionService.createStudentQuestion(course.getId(), studentQuestionDto)
        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.USER_NOT_FOUND
    }

    def "empty studentQuestion"() {
        when:
        studentQuestionService.createStudentQuestion(course.getId(), null)
        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENT_QUESTION_IS_EMPTY
    }

    def "create studentQuestion with no question"() {
        given:
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setStudent(user.getId())
        studentQuestionDto.setQuestion(null)
        when: "no question exists"
        studentQuestionService.createStudentQuestion(course.getId(), studentQuestionDto)
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
        questionDto.setStatus(Question.Status.PENDING.name())
        and: 'a optionId'
        def optionDto = new OptionDto()
        optionDto.setContent("content")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setQuestion(questionDto)
        studentQuestionDto.setStudent(user.getId())

        when: "a student question is created"
        studentQuestionService.createStudentQuestion(course.getId(), studentQuestionDto);

        then: "the question contains correct information"
        def studentQuestion = studentQuestionRepository.findAll().get(0)
        studentQuestion != null
        studentQuestion.question != null
        studentQuestion.question.title == questionDto.title
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
