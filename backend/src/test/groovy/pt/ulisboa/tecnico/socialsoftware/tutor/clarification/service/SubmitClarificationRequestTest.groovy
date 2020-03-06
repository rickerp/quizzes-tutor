package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDTO
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

import java.time.LocalDateTime
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared

@DataJpaTest
class SubmitClarificationRequestTest extends Specification {

    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "ES"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final String QUESTION_CONTENT = "Question content"
    public static final String USERNAME = "Username"
    public static final String NAME = "Name"
    public static final String CLARIFICATION_CONTENT = "Clarification Question"
    public static final String URL = 'URL'

    @Autowired
    ClarificationService clarificationService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    def courseExecution
    def quiz
    def question
    def clarification

    @Shared
    def user
    @Shared
    def creationTime
    @Shared
    def questionAnswer


    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(NAME, USERNAME, 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setCourseExecution(courseExecution)
        courseExecution.addQuiz(quiz)

        question = new Question()
        question.setKey(1)
        question.setCourse(course)


        course.addQuestion(question)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.completed = true
        questionAnswer = quizAnswer.getQuestionAnswers()[0]


        userRepository.save(user)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)


        clarification = new ClarificationDTO()
        clarification.setQuestionAnswerId(questionAnswer.getId())
        clarification.setContent(CLARIFICATION_CONTENT)
        creationTime = LocalDateTime.now()
        clarification.setCreationDate(creationTime)
        clarification.setState(Clarification.State.UNRESOLVED)
        clarification.setUserName(user.getUsername())
    }

    def "submit a clarification to a question that has no clarifications submited"() {
        when:
        clarificationService.createClarification(clarification)

        then:
        def clarificationCreatedList = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreatedList.size() == 1
        def clarificationCreated = clarificationCreatedList[0]
        clarificationCreated.getId() != null
        clarificationCreated.getState() == clarification.getState()
        clarificationCreated.getContent() == clarification.getContent()
        clarificationCreated.getCreationDate() == clarification.getCreationDate()
        clarificationCreated.getUser().getUsername() == clarification.getUserName()
    }

    def "submit a clarification to a question that already has clarifications"() {
        given: "Create a clarification"
        def clarificationSaved = new Clarification(clarification, user, questionAnswer)
        and: "Save clarification in Repository"
        clarificationRepository.save(clarificationSaved)

        when:
        clarificationService.createClarification(clarification)

        then:
        def clarificationCreatedList = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreatedList.size() == 2
    }

    def "submit a clarification with an image"() {
        given: "Create an Image"
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)

        and: "Add image to clarification"
        clarification.setImage(image)

        when:
        clarificationService.createClarification(clarification)

        then:
        def clarificationCreatedList = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreatedList.size() == 1
        def clarificationCreated = clarificationCreatedList[0]
        clarificationCreated.getId() != null
        clarificationCreated.getState() == clarification.getState()
        clarificationCreated.getContent() == clarification.getContent()
        clarificationCreated.getCreationDate() == clarification.getCreationDate()
        clarificationCreated.getUser().getUsername() == clarification.getUserName()
        clarificationCreated.getClarificationImage().getId() != null
        clarificationCreated.getClarificationImage().getUrl() == URL
        clarificationCreated.getClarificationImage().getWidth() == 20

    }

    @Unroll("Test: #creationDate | #userName | #content | #questionAnswerId | #state || #message")
    def "submit a clarification with wrong arguments"() {
        given: "Create a clarificationDto"
        clarification = new ClarificationDTO()
        clarification.setQuestionAnswerId(questionAnswerId)
        clarification.setContent(content)
        clarification.setCreationDate(creationDate)
        clarification.setState(state)
        clarification.setUserName(userName)
        and: "Creation Time after current time"

        when:
        clarificationService.createClarification(clarification)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == message

        where:
        creationDate |      userName      |       content         |    questionAnswerId    |            state               ||                  message
        creationTime | user.getUsername() | CLARIFICATION_CONTENT | questionAnswer.getId() | Clarification.State.RESOLVED   || ErrorMessage.CLARIFICATION_INVALID_STATE
        creationTime | user.getUsername() | CLARIFICATION_CONTENT | questionAnswer.getId() | null                           || ErrorMessage.CLARIFICATION_INVALID_STATE
        creationTime | user.getUsername() | CLARIFICATION_CONTENT | 500                    | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_QUESTION_ANSWER_NOT_FOUND
        creationTime | null               | CLARIFICATION_CONTENT | questionAnswer.getId() | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_INVALID_USER
        creationTime | "rafael"           | CLARIFICATION_CONTENT | questionAnswer.getId() | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_USER_NOT_FOUND
        creationTime | user.getUsername() | null                  | questionAnswer.getId() | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_INVALID_CONTENT
        LocalDateTime.now().plusDays(2)   | user.getUsername() | CLARIFICATION_CONTENT | questionAnswer.getId() | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_INVALID_CREATION_DATE

    }

    def "submit a clarification without a creationTime"() {
        given: "Update clarificationDto"
        clarification.setCreationDate(null)
        when:
        clarificationService.createClarification(clarification)
        then:
        def clarificationCreated = clarificationRepository.findClarificationByQuestionAnswer(question.getId())
        clarificationCreated[0].getCreationDate() != null
    }

    def "submit a clarification with a question answer associated to quiz answer that is not finished"() {
        given: "Create Quiz Answer"
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.completed = false
        questionAnswer = quizAnswer.getQuestionAnswers()[0]
        and: "save questionAnswer"
        questionAnswerRepository.save(questionAnswer)
        and: "Update Clarification"
        clarification.setQuestionAnswerId(questionAnswer.id)

        when:
        clarificationService.createClarification(clarification)
        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_QUIZ_NOT_COMPLETED
    }

    def "submit a clarification with a question that the student didn't answer"() {
        given: "Create new user"
        user = new User("NAME", "USERNAME", 2, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)
        and: "Change Clarification username"
        clarification.setUserName("USERNAME")

        when:
        clarificationService.createClarification(clarification)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_INVALID_QUESTION
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
