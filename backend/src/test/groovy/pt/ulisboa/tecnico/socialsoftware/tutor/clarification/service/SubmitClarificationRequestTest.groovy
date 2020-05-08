package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared

@DataJpaTest
class SubmitClarificationRequestTest extends Specification {

    public static final String QUESTION_CONTENT = "Question content"
    public static final String CLARIFICATION_CONTENT = "ClarificationRequest Question"

    @Autowired
    ClarificationRequestService clarificationRequestService

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
    ClarificationRequestRepository clarificationRequestRepository

    def quiz
    def question
    def clarificationRequestDto
    def questionAnswer

    @Shared
    def user


    def setup() {
        user = new User("Name", "Username", 1, User.Role.STUDENT)

        quiz = new Quiz()
        quiz.setTitle("QuizTitle")
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())

        question = new Question()
        question.setTitle("QuestionTitle")
        question.setKey(1)

        def quizQuestion = new QuizQuestion(quiz, question, 0)
        def quizAnswer = new QuizAnswer(user, quiz)
        quizAnswer.setCompleted(true)
        questionAnswer = quizAnswer.getQuestionAnswers()[0]

        userRepository.save(user)
        quizRepository.save(quiz)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)
        quizAnswerRepository.save(quizAnswer)
        questionAnswerRepository.save(questionAnswer)

        clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setCreationDate(DateHandler.toISOString(DateHandler.now()))
        clarificationRequestDto.setState(ClarificationRequest.State.UNRESOLVED)
        clarificationRequestDto.setUser(new UserDto(user))
    }

    def "Submit a clarification request to a question that has no clarifications submited"() {
        when:
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then:
        def clarificationRequestCreatedList = clarificationRequestRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationRequestCreatedList.size() == 1
        def clarificationRequestCreated = clarificationRequestCreatedList[0]
        clarificationRequestCreated.getState() == clarificationRequestDto.getState()
        clarificationRequestCreated.getContent() == clarificationRequestDto.getContent()
        DateHandler.toISOString(clarificationRequestCreated.getCreationDate()) == clarificationRequestDto.getCreationDate()
        clarificationRequestCreated.getUser().getUsername() == clarificationRequestDto.getUser().getUsername()
    }

    def "Submit a clarification request with an image"() {
        given: "An Image"
        def imageDto = new ImageDto()
        imageDto.setUrl("URL")
        imageDto.setWidth(20)
        and: "Add image to clarification"
        clarificationRequestDto.setImage(imageDto)

        when:
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then:
        def clarificationRequestCreatedList = clarificationRequestRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationRequestCreatedList.size() == 1
        def clarificationRequestCreated = clarificationRequestCreatedList[0]
        clarificationRequestCreated.getImage().getId() != null
        clarificationRequestCreated.getImage().getUrl() == "URL"
        clarificationRequestCreated.getImage().getWidth() == 20
    }

    def "Submit two clarifications requests to the same question, but different question answers"() {
        given: "Another QuestionAnswer"
        def quizAnswer = new QuizAnswer(user, quiz)
        def questionAnswerCreated = quizAnswer.getQuestionAnswers()[0]
        quizAnswer.setCompleted(true)
        questionAnswerRepository.save(questionAnswerCreated)

        when:
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)
        clarificationRequestService.createClarificationRequest(questionAnswerCreated.getId(), clarificationRequestDto)

        then:
        def clarificationRequestCreatedList = clarificationRequestRepository.findClarificationByQuestionAnswer(questionAnswer.getId())
        clarificationRequestCreatedList.size() == 1
        def clarificationRequestCreatedList2 = clarificationRequestRepository.findClarificationByQuestionAnswer(questionAnswerCreated.getId())
        clarificationRequestCreatedList2.size() == 1
        def Question1 = clarificationRequestCreatedList[0].getQuestionAnswer().getQuestion()
        def Question2 = clarificationRequestCreatedList2[0].getQuestionAnswer().getQuestion()
        Question1.getId() == Question2.getId()
    }

    def "Submit two clarifications requests to the same question answer"() {
        when:
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then:
        questionAnswer.getClarificationRequests().size() == 2
    }

    def "Submit an Empty clarification request"() {
        when:
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(),null)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_IS_EMPTY
    }

    def "Submit a clarification request without a creationTime"() {
        given: "Update clarificationRequestDto"
        clarificationRequestDto.setCreationDate(null)

        when:
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then:
        def clarificationRequestCreated = clarificationRequestRepository.findClarificationByQuestionAnswer(questionAnswer.getId())[0]
        clarificationRequestCreated.getCreationDate() != null
    }

    @Unroll("Test: #content | #state || #message")
    def "submit a clarification request with wrong arguments"() {
        given: "Another clarificationRequestDto"
        def clarificationRequestCreated = new ClarificationRequestDto()
        clarificationRequestCreated.setContent(content)
        clarificationRequestCreated.setCreationDate(DateHandler.toISOString(DateHandler.now()))
        clarificationRequestCreated.setState(state)
        clarificationRequestCreated.setUser(new UserDto(user))

        when:
            clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestCreated)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == message

        where:
              content         |                state                  ||               message
        CLARIFICATION_CONTENT | ClarificationRequest.State.RESOLVED   || ErrorMessage.CLARIFICATION_INVALID_STATE
        CLARIFICATION_CONTENT | null                                  || ErrorMessage.CLARIFICATION_INVALID_STATE
        null                  | ClarificationRequest.State.UNRESOLVED || ErrorMessage.CLARIFICATION_INVALID_CONTENT
    }

    @Unroll("Test: questionAnswerId: #questionAnswerId")
    def "Submit a clarification request with a question answer that doesn't exist"() {
        when:
        clarificationRequestService.createClarificationRequest(questionAnswerId, clarificationRequestDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_INVALID_QUESTION_ANSWER

        where:
        questionAnswerId << [500, 0]
    }

    def "Submit a clarification request with a question that the student didn't answer"() {
        given: "Another user"
        user = new User("User_S", "User_Name", 2, User.Role.STUDENT)
        userRepository.save(user)
        and: "Change username"
        clarificationRequestDto.setUser(new UserDto(user))

        when:
        clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_QUESTION_ANSWER_NOT_IN_USER
    }

    def "Submit a clarification request with a question answer associated to quiz answer that is not finished"() {
        given: "another QuizAnswer and QuestionAnswer"
        def quizAnswerCreated = new QuizAnswer(user, quiz)
        quizAnswerCreated.setCompleted(false)
        def questionAnswerCreated = quizAnswerCreated.getQuestionAnswers()[0]
        questionAnswerRepository.save(questionAnswerCreated)

        when:
        clarificationRequestService.createClarificationRequest(questionAnswerCreated.id, clarificationRequestDto)

        then:
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.CLARIFICATION_QUIZ_NOT_COMPLETED
    }

    def "Submit a clarification request and test the returned clarificationRequestDto"() {
        when:
        def clarificationRequestDtoCreated = clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)
        then:
        def clarificationRequestCreated = clarificationRequestRepository.findClarificationByQuestionAnswer(questionAnswer.getId())[0]
        clarificationRequestDtoCreated.getId() == clarificationRequestCreated.getId()
        clarificationRequestDtoCreated.getState() == clarificationRequestDto.getState()
        clarificationRequestDtoCreated.getContent() == clarificationRequestDto.getContent()
        clarificationRequestDtoCreated.getUser().getUsername() == clarificationRequestDto.getUser().getUsername()
        clarificationRequestDtoCreated.getCreationDate() == clarificationRequestDto.getCreationDate()
    }

    @TestConfiguration
    static class ClarificationRequestServiceImplTestContextConfiguration {

        @Bean
        ClarificationRequestService ClarificationRequestService() {
            return new ClarificationRequestService()
        }

        @Bean
        PublicClarificationService PublicClarificationService() {
            return new PublicClarificationService()
        }
    }
}
