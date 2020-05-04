package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class ListStudentQuestionsTest extends Specification {

    static final USERNAME = 'username'

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Shared
    Course course

    @Shared
    User user

    @Shared
    StudentQuestion studentQuestion

    def setup() {
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        def question = new Question()
        question.setTitle("QuestionTitle")
        question.setKey(1)
        question.setStatus(Question.Status.PENDING)
        question.setCourse(course)
        questionRepository.save(question)

        studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
    }

    def "list student questions"() {
        when:
        def list = studentQuestionService.list(course.getId(), user.getId())
        then:
        list != null
        list.size() == 1
    }

    def "list course student questions"() {
        when:
        def list = studentQuestionService.listCourseStudentQuestions(course.getId())
        then:
        list != null
        list.size() == 1
    }

    @Unroll("With courseId: #courseId and userId: #userId")
    def "list student questions empty"() {
        when:
        def list = studentQuestionService.list(courseId, userId)
        then:
        list != null
        list.size() == 0
        where:
        courseId        | userId
        course.getId()  | 123
        123             | user.getId()
    }

    @TestConfiguration
    static class ListStudentQuestionsServiceImplTestContextConfiguration {

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
