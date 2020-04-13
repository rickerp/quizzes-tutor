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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateEvaluationServiceTest extends Specification {
    static final String JUSTIFICATION_ONE = "Não gostei da pergunta. Reformule por favor"
    static final boolean ACCEPTED = true
    static final boolean REJECTED = false
    static final USERNAME = 'username'

    @Autowired
    EvaluationService evaluationService

    @Autowired
    EvaluationRepository evaluationRepository

    @Autowired
    UserRepository userRepository

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
        questionDto.setCreationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
        question = new Question(course, questionDto)
        questionRepository.save(question)
    }

    def "studentQuestion exists and create evaluation"(){
        given: "a studentQuestion"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluationDto"
        def evaluationDto = new EvaluationDto()
        evaluationDto.setAccepted(ACCEPTED)
        evaluationDto.setJustification(null)

        when:
        def result = evaluationService.createEvaluation(evaluationDto, studentQuestion.getId())

        then: "the returned data is correct"
        result.isAccepted()
        result.getJustification() == null
        and: "evaluation is created"
        def evaluation = evaluationRepository.findById(result.getId()).orElse(null)
        evaluation != null
        and: "has the correct values"
        evaluation.getStudentQuestion().getId() == studentQuestion.getId()
        evaluation.isAccepted()
        evaluation.getJustification() == null
    }

    def "studentQuestion does not exist"(){
        given: "an evaluationDto"
        def evaluationDto = new EvaluationDto()
        evaluationDto.setAccepted(ACCEPTED)
        evaluationDto.setJustification(null)

        when: "there is no studentQuestion"
        evaluationService.createEvaluation(evaluationDto, -1)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENT_QUESTION_NOT_FOUND
    }

    def "studentQuestion and evaluation exist"(){
        given: "a studentQuestion"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluation"
        def evaluationDto = new EvaluationDto()
        evaluationDto.setAccepted(REJECTED)
        evaluationDto.setJustification(JUSTIFICATION_ONE)
        evaluationService.createEvaluation(evaluationDto, studentQuestion.getId())

        when: "create another evaluation"
        evaluationDto.setAccepted(ACCEPTED)
        evaluationDto.setJustification(null)
        def result = evaluationService.createEvaluation(evaluationDto, studentQuestion.getId())

        then: "overwrite the older one"
        result.isAccepted()
        result.getJustification() == null
        and: "evaluation is created"
        def evaluation = evaluationRepository.findById(result.getId()).orElse(null)
        evaluation != null
        and: "has the correct values"
        evaluation.getStudentQuestion().getId() == studentQuestion.getId()
        evaluation.isAccepted()
        evaluation.getJustification() == null
    }

    def "reject studentQuestion and do not add a justification"() {
        given: "a studentQuestion"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluationDto"
        def evaluationDto = new EvaluationDto()
        evaluationDto.setAccepted(REJECTED)
        evaluationDto.setJustification(null)

        when: "create evaluation"
        evaluationService.createEvaluation(evaluationDto, studentQuestion.getId())

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.JUSTIFICATION_NOT_FOUND
    }

    def "reject studentQuestion and add a justification"() {
        given: "a studentQuestion"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluationDto"
        def evaluationDto = new EvaluationDto()
        evaluationDto.setAccepted(REJECTED)
        evaluationDto.setJustification(JUSTIFICATION_ONE)

        when: "create evaluation"
        def result = evaluationService.createEvaluation(evaluationDto, studentQuestion.getId())

        then:
        !result.isAccepted()
        result.getJustification() == JUSTIFICATION_ONE
        and: "evaluation is created"
        def evaluation = evaluationRepository.findById(result.getId()).orElse(null)
        evaluation != null
        and: "has the correct values"
        evaluation.getStudentQuestion().getId() == studentQuestion.getId()
        !evaluation.isAccepted()
        evaluation.getJustification() == JUSTIFICATION_ONE
    }

    @TestConfiguration
    static class CreateEvaluationServiceImplTestContextConfiguration {

        @Bean
        EvaluationService evaluationService() {
            return new EvaluationService()
        }
    }
}
