package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.EvaluationService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

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

    User user
    Course course

    def setup(){
        evaluationService = new EvaluationService()
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
        def result = evaluationService.createEvaluation(studentQuestionDto, ACCEPTED, null)
        then:
        result.getStudentQuestionDto() != null
        result.isAccepted()
        result.getJustification() == null
    }

    def "studentQuestion does not exist"(){
        when: "there is no studentQuestion"
        def result = evaluationService.createEvaluation(null, ACCEPTED, null)
        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENTQUESTION_IS_EMPTY
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
        def evaluationDto = evaluationService.createEvaluation(studentQuestionDto, REJECTED, JUSTIFICATION_ONE)
        when: "create another evaluation"
        def result = evaluationService.createEvaluation(studentQuestionDto, ACCEPTED, null)
        then: "overwrite the older one"
        result.getStudentQuestionDto() != null
        result.isAccepted()
        result.getJustification() == null
    }

    def "accepted is empty"(){
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
        when: "create evaluation with no accepted value"
        def result = evaluationService.createEvaluation(studentQuestionDto, null, null)
        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.ACCEPTED_IS_EMPTY
    }

    def "accept studentQuestion and add a justification"() {
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
        def result = evaluationService.createEvaluation(studentQuestionDto, ACCEPTED, JUSTIFICATION_ONE)
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
        def result = evaluationService.createEvaluation(studentQuestionDto, REJECTED, JUSTIFICATION_ONE)
        then:
        result.getStudentQuestionDto() != null
        !result.isAccepted()
        result.getJustification() == JUSTIFICATION_ONE
    }
}
