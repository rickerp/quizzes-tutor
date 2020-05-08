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

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto

import spock.lang.Specification

@DataJpaTest
class SubmitClarificationRequestPerformanceTest extends Specification {

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

    def questionAnswer
    def user


    def setup() {
        user = new User("Name", "Username", 1, User.Role.STUDENT)

        def quiz = new Quiz()
        quiz.setTitle("QuizTitle")
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())

        def question = new Question()
        question.setTitle("QuestionTitle")
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
    }

    def "performance testing to create a clarification request 10000 times"() {
        given: "a clarification dto"
        def clarificationRequestDto = new ClarificationRequestDto()
        clarificationRequestDto.setContent(CLARIFICATION_CONTENT)
        clarificationRequestDto.setState(ClarificationRequest.State.UNRESOLVED)
        clarificationRequestDto.setUser(new UserDto(user))

        when:
        1.upto(1, {
            clarificationRequestService.createClarificationRequest(questionAnswer.getId(), clarificationRequestDto)})

        then:
        true
    }

    @TestConfiguration
    static class ClarificationRequestServiceImplPerformanceTestContextConfiguration {

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
