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
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.Evaluation
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.EvaluationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class ReSubmitStudentQuestionServiceTest extends Specification {
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

    Course course
    User user
    QuestionDto questionDto

    def setup() {
        user = new User("name", USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        course = new Course("course", Course.Type.TECNICO)
        courseRepository.save(course)

        questionDto = new QuestionDto()
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
    }

    def "edit and resubmit a question on a rejected student question"() {
        given: "a question"
        def question = new Question(course, questionDto)
        questionRepository.save(question)

        and: "a student question"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluation"
        def evaluation = new Evaluation()
        evaluation.setJustification("Please change the question");
        evaluation.setAccepted(false);
        evaluation.setStudentQuestion(studentQuestion);
        studentQuestion.setEvaluation(evaluation);

        and: "a new questionDto"
        def questionDto2 = new QuestionDto()
        questionDto2.setKey(1)
        questionDto2.setTitle("title2")
        questionDto2.setContent("content2")
        questionDto2.setStatus(Question.Status.PENDING.name())
        def optionDto = new OptionDto()
        optionDto.setContent("content2")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto2.setOptions(options)
        questionDto2.setCreationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))

        and: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setEvaluation(new EvaluationDto(evaluation))
        studentQuestionDto.setId(studentQuestion.getId())
        studentQuestionDto.setQuestion(questionDto2)

        when:
        def result = studentQuestionService.reSubmitStudentQuestion(studentQuestionDto)

        then:
        result.getQuestion().getTitle() == "title2"
        result.getQuestion().getContent() == "content2"

        and: "question is changed on the database"
        def updatedStudentQuestion = studentQuestionRepository.findById(result.getId()).orElse(null)
        updatedStudentQuestion != null
        updatedStudentQuestion.getQuestion().getTitle() == "title2"
        updatedStudentQuestion.getQuestion().getContent() == "content2"
    }

    def "edit question on a accepted student question"() {
        given: "a question"
        def question = new Question(course, questionDto)
        questionRepository.save(question)

        and: "a student question"
        def studentQuestion = new StudentQuestion(user, question)
        studentQuestionRepository.save(studentQuestion)
        and: "an evaluation"
        def evaluation = new Evaluation()
        evaluation.setJustification(null);
        evaluation.setAccepted(true);
        evaluation.setStudentQuestion(studentQuestion);
        studentQuestion.setEvaluation(evaluation);

        and: "a new questionDto"
        def questionDto2 = new QuestionDto()
        questionDto2.setKey(1)
        questionDto2.setTitle("title2")
        questionDto2.setContent("content2")
        questionDto2.setStatus(Question.Status.PENDING.name())
        def optionDto = new OptionDto()
        optionDto.setContent("content2")
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        questionDto2.setOptions(options)
        questionDto2.setCreationDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))

        and: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setEvaluation(new EvaluationDto(evaluation))
        studentQuestionDto.setId(studentQuestion.getId())
        studentQuestionDto.setQuestion(questionDto)

        when: "resubmit on a accepted student question"
        studentQuestionService.reSubmitStudentQuestion(studentQuestionDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENT_QUESTION_NOT_ACCEPTED
    }

    @TestConfiguration
    static class EditStudentQuestionServiceImplTestContextConfiguration {
        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService();
        }

        @Bean
        UserService userService() {
            return new UserService();
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService();
        }
    }
}
