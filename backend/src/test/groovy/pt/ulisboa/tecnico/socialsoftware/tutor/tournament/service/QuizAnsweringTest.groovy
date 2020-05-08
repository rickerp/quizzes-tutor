package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementOptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.OPTION_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ANSWER_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_ALREADY_FINISHED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_ACCEPTING_RESPONSES
import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.STUDENT

@DataJpaTest
class QuizAnsweringTest extends Specification {
    public static final String T_NAME = "Demo-Tournament"
    public static final String NAME = "Name"
    public static final Integer KEY_1 = 1
    public static final Integer KEY_2 = 2
    public static final String USERNAME_1 = "Username_" + KEY_1
    public static final String USERNAME_2 = "Username_" + KEY_2
    public static final LocalDateTime NOW = DateHandler.now()

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    TournamentService tournamentService

    def creator
    def user
    def course
    def courseExecution
    def topics
    def question
    def option
    def tournament

    def setup() {
        "Create a Course"
        course = new Course(NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        "Create a Course Execution"
        courseExecution = new CourseExecution(course, NAME, NAME, Course.Type.TECNICO)
        courseExecution.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution)
        "Create a User Creator"
        creator = new User(NAME, USERNAME_1, KEY_1, STUDENT)
        creator.addCourse(courseExecution)
        userRepository.save(creator)
        "Create a User"
        user = new User(NAME, USERNAME_2, KEY_2, STUDENT)
        user.addCourse(courseExecution)
        userRepository.save(user)
        "Create an Option"
        option = new Option()
        option.setSequence(KEY_1)
        option.setCorrect(true)
        option.setContent(NAME)
        optionRepository.save(option)
        "Create a Question"
        question = new Question()
        question.setKey(KEY_1)
        question.setCourse(course)
        question.setTitle(NAME)
        questionRepository.save(question)
        option.setQuestion(question)
        "Create a Topic"
        Topic topic = new Topic()
        topic.setName(NAME)
        topic.setCourse(course)
        topicRepository.save(topic)
        question.addTopic(topic)
        topics = new HashSet<Topic>()
        topics.add(topic)
        "Create Opened Tournament"
        tournament = new Tournament()
        tournament.setName(T_NAME)
        tournament.setCreator(creator)
        tournament.setTopics(topics)
        tournament.setNrQuestions(1)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartTime(NOW.plusMinutes(10))
        tournament.setEndTime(NOW.plusMinutes(20))
        tournamentRepository.save(tournament)
        "Enroll Users in Opened Tournament"
        tournamentService.enroll(creator.getId(), tournament.getId())
        tournamentService.enroll(user.getId(), tournament.getId())
    }

    def "Start an Opened Tournament Quiz"() {

        when:
        tournamentService.beginQuiz(tournament.getId(), user.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_NOT_ACCEPTING_RESPONSES
    }

    def "Start an In Progress Tournament Quiz"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))

        when:
        TournamentQuizDto quizDto = tournamentService.beginQuiz(tournament.getId(), user.getId())

        then: "Check data in DB"
        quizDto.getTimeToEnd() <= ChronoUnit.MILLIS.between(tournament.getStartTime(), tournament.getEndTime())
        quizDto.getTimeToEnd() >= ChronoUnit.MILLIS.between(DateHandler.now(), tournament.getEndTime())
        quizDto.getQuestions().size() == 1
        StatementQuestionDto q = quizDto.getQuestions().get(0)
        q.getQuestionId() == question.getId()
        q.getOptions().size() == 1
        StatementOptionDto o = q.getOptions().get(0)
        optionRepository.findById(o.getOptionId())
                .orElseThrow({ -> new TutorException(OPTION_NOT_FOUND, o.getOptionId()) })
                .getId() == option.getId()
        quizDto.getAnswers().size() == 1
        StatementAnswerDto a = quizDto.getAnswers().get(0)
        q.getSequence() == a.getSequence()
    }

    def "Start a Closed Tournament Quiz"() {

        given: "Opened Tournament -> Closed Tournament"
        tournament.setStartTime(NOW.plusMinutes(-20))
        tournament.setEndTime(NOW.plusMinutes(-10))

        when:
        tournamentService.beginQuiz(tournament.getId(), user.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_NOT_ACCEPTING_RESPONSES
    }

    def "Select a Valid Question Option for Tournament Quiz"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))

        when: "Begin and Answer the Quiz"
        TournamentQuizDto dto = tournamentService.beginQuiz(tournament.getId(), user.getId())
        StatementAnswerDto answer = dto.getAnswers().get(0)
        answer.setOptionId(option.getId())
        tournamentService.selectQuestionOption(tournament.getId(), user.getId(), answer)

        then: "Check DB Data"
        tournament.getTournamentAnswers().size() == 2
        Set<QuestionAnswer> answers = tournament.getTournamentAnswer(user.getId()).getQuestionAnswers()
        answers.size() == 1
        Option o = answers[0].getOption()
        o != null
        o.getId() == option.getId()
    }

    def "Select an Invalid Question Option for Tournament Quiz"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))
        and: "New Option"
        def option_2 = new Option()
        option_2.setSequence(KEY_2)
        option_2.setCorrect(false)
        option_2.setContent(NAME)
        optionRepository.save(option_2)

        when: "Begin and Answer the Quiz"
        TournamentQuizDto dto = tournamentService.beginQuiz(tournament.getId(), user.getId())
        StatementAnswerDto answer = dto.getAnswers().get(0)
        answer.setOptionId(option_2.getId())
        tournamentService.selectQuestionOption(tournament.getId(), user.getId(), answer)

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == QUESTION_OPTION_MISMATCH
    }

    def "Begin and Finish a Tournament Quiz"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))

        when:
        TournamentQuizDto quizDto = tournamentService.beginQuiz(tournament.getId(), user.getId())
        List<CorrectAnswerDto> dtoList = tournamentService.finishQuiz(tournament.getId(), user.getId())

        then: "Check DB Data"
        dtoList.size() == 1
        dtoList.get(0).getCorrectOptionId() == option.getId()
        quizDto.getQuestions().size() == 1
        quizDto.getQuestions().get(0).getOptions().stream()
                .allMatch({ opt -> opt.getOptionId() == option.getId() })
    }

    def "Begin a Finished Tournament Quiz"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))

        when:
        tournamentService.finishQuiz(tournament.getId(), user.getId())
        tournamentService.beginQuiz(tournament.getId(), user.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_ALREADY_FINISHED
    }

    def "Answer a Finished Tournament Quiz"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))

        when:
        tournamentService.finishQuiz(tournament.getId(), user.getId())
        tournamentService.selectQuestionOption(tournament.getId(), user.getId(), null)

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_ALREADY_FINISHED
    }

    @TestConfiguration
    static class QuizAnsweringConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
