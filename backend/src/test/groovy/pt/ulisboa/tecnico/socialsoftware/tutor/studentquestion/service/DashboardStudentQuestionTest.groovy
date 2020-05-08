package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.Evaluation
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.EvaluationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@DataJpaTest
class DashboardStudentQuestionTest extends Specification {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    EvaluationRepository evaluationRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Shared
    StudentQuestion studentQuestion

    @Shared
    Course course

    @Shared
    User user

    def setup() {
        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        CourseExecution courseExecution = new CourseExecution(course, 'TEST', 'TEST', Course.Type.TECNICO)
        courseExecution.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution)

        user = new User("name", "username", 1, User.Role.STUDENT)
        user.addCourse(courseExecution)
        user.setPublicSuggestedQuestionsDashboard(true)
        courseExecution.addUser(user)
        userRepository.save(user)

        def question = new Question()
        question.setTitle("QuestionTitle")
        question.setKey(1)
        question.setStatus(Question.Status.PENDING)
        question.setCourse(course)
        questionRepository.save(question)

        studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
    }

    def "get private dashboard"() {
        when:
        def dashboardDto = studentQuestionService.getPrivateDashboard(course.getId(), user.getId())
        then:
        dashboardDto != null
        dashboardDto.getAccepted() == 0
        dashboardDto.getTotal() == 1
    }

    def "get private dashboard with approved"() {
        given:
        def evaluation = new Evaluation(studentQuestion, true, "all good")
        evaluationRepository.save(evaluation)
        when:
        def dashboardDto = studentQuestionService.getPrivateDashboard(course.getId(), user.getId())
        then:
        dashboardDto != null
        dashboardDto.getAccepted() == 1
        dashboardDto.getTotal() == 1
    }

    def "get public dashboard"() {
        when:
        def dashboardDtos = studentQuestionService.getDashboard(course.getId())
        then:
        dashboardDtos != null
        dashboardDtos.size() == 1
        dashboardDtos.get(0).getName() == user.getName()
        dashboardDtos.get(0).getTotal() == 1
        dashboardDtos.get(0).getAccepted() == 0
    }

    def "get public dashboard with private settings"() {
        given:
        user.setPublicSuggestedQuestionsDashboard(false)
        when:
        def dashboardDtos = studentQuestionService.getDashboard(course.getId())
        then:
        dashboardDtos != null
        dashboardDtos.size() == 0
    }

    @Unroll("Visibility to #value")
    def "set user dashboard visibility"() {
        when:
        def res = studentQuestionService.setDashboardVisibility(user.getId(), (Boolean) expected)
        then:
        res != null
        res == actual
        user.isPublicSuggestedQuestionsDashboard() == actual
        where:
        expected || actual
        true     || true
        false    || false
    }

    @TestConfiguration
    static class DashboardStudentQuestionServiceImplTestContextConfiguration {

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
