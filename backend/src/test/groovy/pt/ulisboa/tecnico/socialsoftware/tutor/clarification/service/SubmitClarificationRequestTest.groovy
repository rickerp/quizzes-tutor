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

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

import java.time.LocalDateTime
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared

@DataJpaTest
class SubmitClarificationRequestTest extends Specification {

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

    def quiz
    def question
    def clarificationDto

    @Shared
    def user
    @Shared
    def creationTime
    @Shared
    def questionAnswer


    def setup() {
        user = new User(NAME, USERNAME, 1, User.Role.STUDENT)

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED)

        question = new Question()
        question.setKey(1)

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


        clarificationDto = new ClarificationDTO()
        clarificationDto.setQuestionAnswerId(questionAnswer.getId())
        clarificationDto.setContent(CLARIFICATION_CONTENT)
        creationTime = LocalDateTime.now()
        clarificationDto.setCreationDate(creationTime)
        clarificationDto.setState(Clarification.State.UNRESOLVED)
        clarificationDto.setUserName(user.getUsername())

        System.out.println(questionAnswer.getId())
    }

    def "submit a clarification to a question that has no clarifications submited"() {
        when:
        clarificationService.createClarification(clarificationDto)

        then:
        def clarificationCreatedList = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreatedList.size() == 1
        def clarificationCreated = clarificationCreatedList[0]
        clarificationCreated.getId() != null
        clarificationCreated.getState() == clarificationDto.getState()
        clarificationCreated.getContent() == clarificationDto.getContent()
        clarificationCreated.getCreationDate() == clarificationDto.getCreationDate()
        clarificationCreated.getUser().getUsername() == clarificationDto.getUserName()
    }

    def "submit a clarification to a question that already has clarifications"() {
        given: "Create a clarification"
        def clarificationSaved = new Clarification(clarificationDto, user, questionAnswer)
        and: "Save clarification in Repository"
        clarificationRepository.save(clarificationSaved)

        when:
        clarificationService.createClarification(clarificationDto)

        then:
        def clarificationCreatedList = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreatedList.size() == 2
    }

    def "submit a clarification with an image"() {
        given: "an Image"
        def imageDto = new ImageDto()
        imageDto.setUrl(URL)
        imageDto.setWidth(20)

        and: "Add image to clarification"
        clarificationDto.setImage(imageDto)

        when:
        clarificationService.createClarification(clarificationDto)

        then:
        def clarificationCreatedList = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreatedList.size() == 1
        def clarificationCreated = clarificationCreatedList[0]
        clarificationCreated.getClarificationImage().getId() != null
        clarificationCreated.getClarificationImage().getUrl() == URL
        clarificationCreated.getClarificationImage().getWidth() == 20

    }

    @Unroll("Test: #creationDate | #userName | #content | #state || #message")
    def "submit a clarification with wrong arguments"() {
        given: "Create a clarificationDto"
        def clarificationCreated = new ClarificationDTO()
        clarificationCreated.setQuestionAnswerId(questionAnswer.getId())
        clarificationCreated.setContent(content)
        clarificationCreated.setCreationDate(creationDate)
        clarificationCreated.setState(state)
        clarificationCreated.setUserName(userName)
        and: "Creation Time after current time"

        when:
            clarificationService.createClarification(clarificationCreated)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == message

        where:
        creationDate |      userName      |       content         |            state               ||                  message
        creationTime | user.getUsername() | CLARIFICATION_CONTENT | Clarification.State.RESOLVED   || ErrorMessage.CLARIFICATION_INVALID_STATE
        creationTime | user.getUsername() | CLARIFICATION_CONTENT | null                           || ErrorMessage.CLARIFICATION_INVALID_STATE
        creationTime | null               | CLARIFICATION_CONTENT | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_INVALID_USER
        creationTime | "rafael"           | CLARIFICATION_CONTENT | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_INVALID_USER
        creationTime | user.getUsername() | null                  | Clarification.State.UNRESOLVED || ErrorMessage.CLARIFICATION_INVALID_CONTENT
    }

    def "submit a clarification with a question that doesn't exist"() {
        given: "change question answer id on clarification"
            clarificationDto.setQuestionAnswerId(500)
        when:
            clarificationService.createClarification(clarificationDto)
        then:
            def error = thrown(TutorException)
            error.getErrorMessage() == ErrorMessage.CLARIFICATION_INVALID_QUESTION_ANSWER
    }

    def "submit a clarification with a question that the student didn't answer"() {
        given: "Create new user"
            user = new User("Rafael", "rafafigo", 2, User.Role.STUDENT)
            userRepository.save(user)
            and: "Change Clarification username"
            clarificationDto.setUserName("rafafigo")

        when:
            clarificationService.createClarification(clarificationDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_QUESTION_ANSWER_NOT_IN_USER
    }

    def "submit a clarification without a creationTime"() {
        given: "Update clarificationDto"
        clarificationDto.setCreationDate(null)
        when:
        clarificationService.createClarification(clarificationDto)
        then:
        def clarificationCreated = clarificationRepository.findClarificationByQuestionAnswer(question.getId())
        clarificationCreated[0].getCreationDate() != null
    }

    def "submit a clarification with a question answer associated to quiz answer that is not finished"() {
        given: "Create Quiz Answer"
        def quizAnswerCreated = new QuizAnswer(user, quiz)
        quizAnswerCreated.completed = false
        def questionAnswerCreated = quizAnswerCreated.getQuestionAnswers()[0]
        and: "save questionAnswer"
        questionAnswerRepository.save(questionAnswerCreated)
        and: "Update Clarification"
        clarificationDto.setQuestionAnswerId(questionAnswerCreated.id)

        when:
        clarificationService.createClarification(clarificationDto)
        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_QUIZ_NOT_COMPLETED
    }

    @TestConfiguration
    static class ClarificationServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }
    }
}
