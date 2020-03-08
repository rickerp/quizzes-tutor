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
    public static final String CLARIFICATION_CONTENT = "Clarification Question"

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
        user = new User("Name", "Username", 1, User.Role.STUDENT)

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

        creationTime = LocalDateTime.now()

        clarificationDto = new ClarificationDTO()
        clarificationDto.setQuestionAnswerId(questionAnswer.getId())
        clarificationDto.setContent(CLARIFICATION_CONTENT)
        clarificationDto.setCreationDate(creationTime)
        clarificationDto.setState(Clarification.State.UNRESOLVED)
        clarificationDto.setUserName(user.getUsername())

    }

    def "Submit a clarification to a question that has no clarifications submited"() {
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

    def "Submit a clarification with an image"() {
        given: "An Image"
        def imageDto = new ImageDto()
        imageDto.setUrl("URL")
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
        clarificationCreated.getClarificationImage().getUrl() == "URL"
        clarificationCreated.getClarificationImage().getWidth() == 20
    }

    def "Submit two clarifications to the same question, but different question answers"() {
        given: "Another QuestionAnswer"
        def quizAnswer = new QuizAnswer(user, quiz)
        def questionAnswerCreated = quizAnswer.getQuestionAnswers()[0]
        quizAnswer.completed = true
        questionAnswerRepository.save(questionAnswerCreated)

        when:
        clarificationService.createClarification(clarificationDto)
        clarificationDto.setQuestionAnswerId(questionAnswerCreated.getId())
        clarificationService.createClarification(clarificationDto)

        then:
        def clarificationCreatedList = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreatedList.size() == 1
        def clarificationCreatedList2 = clarificationRepository.findClarificationByQuestionAnswer(questionAnswerCreated.getId())
        clarificationCreatedList2.size() == 1
        questionAnswer.getQuizQuestion().getQuestion().getClarifications().size() == 2
    }

    def "Submit two clarifications to the same question answer"() {
        when:
        clarificationService.createClarification(clarificationDto)
        clarificationService.createClarification(clarificationDto)

        then:
        questionAnswer.getClarifications().size() == 2
    }

    @Unroll("Test: #creationDate | #userName | #content | #state || #message")
    def "submit a clarification with wrong arguments"() {
        given: "Another ClarificationDto"
        def clarificationCreated = new ClarificationDTO()
        clarificationCreated.setQuestionAnswerId(questionAnswer.getId())
        clarificationCreated.setContent(content)
        clarificationCreated.setCreationDate(creationDate)
        clarificationCreated.setState(state)
        clarificationCreated.setUserName(userName)

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

    def "Submit a clarification without a creationTime"() {
        given: "Update clarificationDto"
        clarificationDto.setCreationDate(null)

        when:
        clarificationService.createClarification(clarificationDto)

        then:
        def clarificationCreated = clarificationRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationCreated[0].getCreationDate() != null
    }

    def "Submit a clarification with a question answer that doesn't exist"() {
        given: "A non existent QuestionAnswer id"
        clarificationDto.setQuestionAnswerId(questionAnswerId)

        when:
        clarificationService.createClarification(clarificationDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_INVALID_QUESTION_ANSWER

        where:
        questionAnswerId << [500, 0]
    }

    def "Submit a clarification with a question that the student didn't answer"() {
        given: "Another user"
        user = new User("Rafael", "rafafigo", 2, User.Role.STUDENT)
        userRepository.save(user)
        and: "Change username"
        clarificationDto.setUserName("rafafigo")

        when:
        clarificationService.createClarification(clarificationDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_QUESTION_ANSWER_NOT_IN_USER
    }

    def "Submit a clarification with a question answer associated to quiz answer that is not finished"() {
        given: "another QuizAnswer and QuestionAnswer"
        def quizAnswerCreated = new QuizAnswer(user, quiz)
        quizAnswerCreated.completed = false
        def questionAnswerCreated = quizAnswerCreated.getQuestionAnswers()[0]
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
