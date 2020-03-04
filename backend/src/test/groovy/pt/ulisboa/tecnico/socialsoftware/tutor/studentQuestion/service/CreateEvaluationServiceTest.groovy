package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.EvaluationService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

import java.util.concurrent.RejectedExecutionException

@DataJpaTest
class CreateEvaluationServiceTest extends Specification {
    static final String JUSTIFICATION_ONE = "Não gostei da pergunta. Reformule por favor"
    static final boolean ACCEPTED = true
    static final boolean REJECTED = false
    static final USERNAME = 'username'

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    EvaluationService evaluationService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserService userService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    User user
    Course course

    def setup(){
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)
    }

    def "studentQuestion exists and create evaluation"(){
        given: "a Question"
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
        and: "a studentQuestion"
        def studentQuestionDto = studentQuestionService.createStudentQuestion(USERNAME, course.getId(), questionDto)

        when:
        def result = evaluationService.createEvaluation(studentQuestionDto.getId(), ACCEPTED, null)
        then:
        result.getStudentQuestionDto() != null
        result.isAccepted()
        result.getJustification() == null
    }

    def "studentQuestion does not exist"(){
        when: "there is no studentQuestion"
        def result = evaluationService.createEvaluation(-1, ACCEPTED, null)
        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENT_QUESTION_NOT_FOUND
    }

    def "studentQuestion and evaluation exist"(){
        given: "a Question"
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
        and: "a studentQuestion"
        def studentQuestionDto = studentQuestionService.createStudentQuestion(USERNAME, course.getId(), questionDto)
        and: "a evaluation"
        def evaluationDto = evaluationService.createEvaluation(studentQuestionDto.getId(), REJECTED, JUSTIFICATION_ONE)
        when: "create another evaluation"
        def result = evaluationService.createEvaluation(studentQuestionDto.getId(), ACCEPTED, null)
        then: "overwrite the older one"
        result.getStudentQuestionDto() != null
        result.isAccepted()
        result.getJustification() == null
    }

    def "reject studentQuestion and do not add a justification"() {
        given: "a Question"
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
        and: "a studentQuestion"
        def studentQuestionDto = studentQuestionService.createStudentQuestion(USERNAME, course.getId(), questionDto)
        when: "create evaluation"
        def result = evaluationService.createEvaluation(studentQuestionDto.getId(), REJECTED, null)
        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.JUSTIFICATION_ERROR
    }

    def "reject studentQuestion and add a justification"() {
        given: "a Question"
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
        and: "a studentQuestion"
        def studentQuestionDto = studentQuestionService.createStudentQuestion(USERNAME, course.getId(), questionDto)
        when: "create evaluation"
        def result = evaluationService.createEvaluation(studentQuestionDto.getId(), REJECTED, JUSTIFICATION_ONE)
        then:
        result.getStudentQuestionDto() != null
        !result.isAccepted()
        result.getJustification() == JUSTIFICATION_ONE
    }

    @TestConfiguration
    static class CreateEvaluationServiceImplTestContextConfiguration {
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
