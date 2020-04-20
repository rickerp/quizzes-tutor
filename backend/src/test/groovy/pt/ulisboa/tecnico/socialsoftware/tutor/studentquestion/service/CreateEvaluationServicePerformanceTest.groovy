package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.EvaluationService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.EvaluationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.EvaluationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@DataJpaTest
class CreateEvaluationServicePerformanceTest extends Specification {
    static final boolean ACCEPTED = true
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

    def "performance testing to create 1000 evaluations"() {
        given: "a studentQuestion"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluationDto"
        def evaluationDto = new EvaluationDto()
        evaluationDto.setAccepted(ACCEPTED)
        evaluationDto.setJustification(null)

        when:
        1.upto(1, {evaluationService.createEvaluation(evaluationDto, studentQuestion.getId())})

        then:
        true
    }

    @TestConfiguration
    static class CreateEvaluationServicePerformanceImplTestContextConfiguration {

        @Bean
        EvaluationService evaluationService() {
            return new EvaluationService()
        }

    }
}
