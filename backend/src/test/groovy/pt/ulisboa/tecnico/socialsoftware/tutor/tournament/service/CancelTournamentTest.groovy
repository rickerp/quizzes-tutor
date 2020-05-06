package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.INVALID_TOURNAMENT_CREATOR
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_OPENED
import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.STUDENT

@DataJpaTest
class CancelTournamentTest extends Specification {
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
        "Create Opened Tournaments"
        tournament = new Tournament()
        tournament.setName(T_NAME)
        tournament.setCreator(creator)
        tournament.setTopics(topics)
        tournament.setNrQuestions(1)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartTime(NOW.plusMinutes(10))
        tournament.setEndTime(NOW.plusMinutes(20))
        tournamentRepository.save(tournament)
    }

    def "Delete a Tournament"() {

        when:
        tournamentService.removeTournament(tournament.getId(), creator.getId())
        tournamentRepository.findById(tournament.getId())
                .orElseThrow({ -> new TutorException(TOURNAMENT_NOT_FOUND, tournament.getId()) })

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "Delete a Tournament with one Enrollment"() {

        given: "Enroll User in Tournament"
        tournamentService.enroll(user.getId(), tournament.getId())

        when:
        tournamentService.removeTournament(tournament.getId(), creator.getId())
        tournamentRepository.findById(tournament.getId())
                .orElseThrow({ -> new TutorException(TOURNAMENT_NOT_FOUND, tournament.getId()) })

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "Delete a Tournament with a Quiz Generated"() {

        given: "Enroll User & Creator in Tournament"
        tournamentService.enroll(creator.getId(), tournament.getId())
        tournamentService.enroll(user.getId(), tournament.getId())

        when:
        tournamentService.removeTournament(tournament.getId(), creator.getId())
        tournamentRepository.findById(tournament.getId())
                .orElseThrow({ -> new TutorException(TOURNAMENT_NOT_FOUND, tournament.getId()) })

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_NOT_FOUND
    }

    def "Delete a Tournament with a User != Creator"() {

        when:
        tournamentService.removeTournament(tournament.getId(), user.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == INVALID_TOURNAMENT_CREATOR
    }

    def "Delete an In Progress Tournament"() {

        given: "Opened Tournament -> In Progress Tournament"
        tournament.setStartTime(NOW.plusMinutes(-10))

        when:
        tournamentService.removeTournament(tournament.getId(), creator.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_NOT_OPENED
    }

    def "Delete a Closed Tournament"() {

        given: "Opened Tournament -> Closed Tournament"
        tournament.setStartTime(NOW.plusMinutes(-20))
        tournament.setEndTime(NOW.plusMinutes(-10))

        when:
        tournamentService.removeTournament(tournament.getId(), creator.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOURNAMENT_NOT_OPENED
    }

    @TestConfiguration
    static class CancelTournamentConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
