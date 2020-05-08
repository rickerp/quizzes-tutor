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

@DataJpaTest
class PublishStudentQuestionsTest extends Specification {

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

    @Autowired
    EvaluationRepository evaluationRepository

    @Shared
    Course course

    @Shared
    User user

    def setup() {
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)
    }

    def "publish already published student question"() {
        given:
        def question = new Question()
        question.setTitle("QuestionTitle")
        question.setKey(1)
        question.setStatus(Question.Status.AVAILABLE)
        question.setCourse(course)
        questionRepository.save(question)
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        def evaluation = new Evaluation(studentQuestion, true, "all good")
        evaluationRepository.save(evaluation)
        when:
        studentQuestionService.publishStudentQuestion(studentQuestion.getId())
        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENT_QUESTION_ALREADY_ADDED;
        where:
        accepted  | status                    || errorMessage
        true      | Question.Status.AVAILABLE || ErrorMessage.STUDENT_QUESTION_ALREADY_ADDED
        true      | Question.Status.DISABLED  || ErrorMessage.STUDENT_QUESTION_ALREADY_ADDED
        true      | Question.Status.REMOVED   || ErrorMessage.STUDENT_QUESTION_ALREADY_ADDED
        false     | Question.Status.PENDING   || ErrorMessage.EVALUATION_NOT_ACCEPTED
    }

    def "publish not evaluated student question"() {
        given:
        def question = new Question()
        question.setTitle("QuestionTitle")
        question.setKey(1)
        question.setStatus(Question.Status.PENDING)
        question.setCourse(course)
        questionRepository.save(question)
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        when:
        studentQuestionService.publishStudentQuestion(studentQuestion.getId())
        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.EVALUATION_NOT_ACCEPTED
    }

    def "publish approved student question"() {
        given:
        def question = new Question()
        question.setTitle("QuestionTitle")
        question.setKey(1)
        question.setStatus(Question.Status.PENDING)
        question.setCourse(course)
        questionRepository.save(question)
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        def evaluation = new Evaluation(studentQuestion, true, "all good")
        evaluationRepository.save(evaluation)
        when:
        def studentQuestionDto = studentQuestionService.publishStudentQuestion(studentQuestion.getId())
        then:
        studentQuestionDto.getQuestion().getStatus() == Question.Status.AVAILABLE.name()
        question.getStatus() == Question.Status.AVAILABLE
    }

    @TestConfiguration
    static class PublishStudentQuestionsServiceImplTestContextConfiguration {

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
