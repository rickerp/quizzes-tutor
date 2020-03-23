package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.EvaluationService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.EvaluationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.EvaluationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification


@DataJpaTest
class CreateEvaluationServicePerformanceTest extends Specification {
    static final String JUSTIFICATION_ONE = "Não gostei da pergunta. Reformule por favor"
    static final boolean ACCEPTED = true
    static final boolean REJECTED = false
    static final USERNAME = 'username'

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    EvaluationService evaluationService

    @Autowired
    EvaluationRepository evaluationRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    QuestionRepository questionRepository

    def user
    def question

    def setup(){
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        def course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle("title")
        questionDto.setContent("content")
        questionDto.setStatus(Question.Status.PENDING.name())
        def optionDto = new OptionDto()
        optionDto.setContent("content")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto.setOptions(options)
        question = new Question(course, questionDto)
        questionRepository.save(question)
    }

    def "performance testing to create 10000 evaluations"() {
        given: "a studentQuestion"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluationDto"
        def evaluationDto = new EvaluationDto()
        evaluationDto.setStudentQuestionDto(new StudentQuestionDto(studentQuestion))
        evaluationDto.setAccepted(ACCEPTED)
        evaluationDto.setJustification(null)

        when:
        1.upto(10000, {evaluationService.createEvaluation(evaluationDto)})

        then:
        true
    }

    @TestConfiguration
    static class CreateEvaluationServicePerformanceImplTestContextConfiguration {
        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

        @Bean
        EvaluationService evaluationService() {
            return new EvaluationService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}
