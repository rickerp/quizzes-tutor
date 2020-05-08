package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
import spock.lang.Unroll

import java.time.LocalDateTime
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOT_OPENED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_STUDENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DUPLICATE_TOURNAMENT_ENROLL
import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.*

@DataJpaTest
class EnrollTournamentServiceTest extends Specification {
    public static final String T_NAME = "Demo-Tournament"
    public static final String NAME = "Name"
    public static final String USERNAME_1 = "Username_1"
    public static final String USERNAME_2 = "Username_2"
    public static final Integer KEY_1 = 1
    public static final Integer KEY_2 = 2
    public static final LocalDateTime NOW = DateHandler.now()

    @Autowired
    UserRepository userRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    def courseExecution
    def player
    def topics
    def tournament

    def setup() {
        "Create a Course"
        Course course = new Course(NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        "Create a Course Execution"
        courseExecution = new CourseExecution()
        courseExecution.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution)
        "Create a User Creator"
        player = new User(NAME, USERNAME_1, KEY_1, STUDENT)
        player.addCourse(courseExecution)
        userRepository.save(player)
        "Create 5 Topics, each with 2 questions"
        topics = new HashSet<Topic>()
        1.upto(5, {
            Topic topic = new Topic()
            topic.setName(NAME + it)
            topicRepository.save(topic)

            1.upto(2, {
                Option option = new Option()
                option.setSequence(topic.getId() * 5 + it)
                option.setCorrect(true)
                option.setContent(NAME + it)
                optionRepository.save(option)

                Question question = new Question()
                question.setKey(topic.getId() * 5 + it)
                question.setCourse(course)
                question.setTitle(NAME + it)
                questionRepository.save(question)
                option.setQuestion(question)

                question.addTopic(topic)
            })

            topics.add(topic)
        })
        "Create a Tournament"
        tournament = new Tournament()
        tournament.setName(T_NAME)
        tournament.setCreator(player)
        tournament.setTopics(topics)
        tournament.setNrQuestions(10)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartTime(NOW.plusMinutes(10))
        tournament.setEndTime(NOW.plusMinutes(20))
        "Store data in DB"
        userRepository.save(player)
        tournamentRepository.save(tournament)
    }

    @Unroll("Enroll #Role in a Tournament at #StartTime - #EndTime || Expecting #Message Message")
    def "Invalid Tournament Enroll Arguments" () {

        given: "Set Players Role & Tournaments State"
        player.setRole(Role)
        tournament.setStartTime(StartTime)
        tournament.setEndTime(EndTime)

        when: "Enroll Players in Tournaments"
        tournamentService.enroll(player.getId(), tournament.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == Message

        where:
        Role       | StartTime              | EndTime               || Message
        STUDENT    | NOW.plusMinutes(-20)   | NOW.plusMinutes(-10)  || TOURNAMENT_NOT_OPENED
        STUDENT    | NOW.plusMinutes(-10)   | NOW.plusMinutes(10)   || TOURNAMENT_NOT_OPENED
        ADMIN      | NOW.plusMinutes(10)    | NOW.plusMinutes(20)   || USER_NOT_STUDENT
        DEMO_ADMIN | NOW.plusMinutes(10)    | NOW.plusMinutes(20)   || USER_NOT_STUDENT
        TEACHER    | NOW.plusMinutes(10)    | NOW.plusMinutes(20)   || USER_NOT_STUDENT
        null       | NOW.plusMinutes(10)    | NOW.plusMinutes(20)   || USER_NOT_STUDENT
    }

    def "Enroll a Student in an Opened Tournament" () {

        when: "Enroll Players in Tournaments"
        def tournamentDto = tournamentService.enroll(player.getId(), tournament.getId())

        then: "Check DB data"
        tournamentDto.getPlayersId().contains(player.getId())
    }

    def "Enroll a Student in two different Opened Tournaments" () {

        given: "Create Second Tournament"
        def tournament_2 = new Tournament()
        tournament_2.setName(T_NAME)
        tournament_2.setCreator(player)
        tournament_2.setTopics(topics)
        tournament_2.setNrQuestions(10)
        tournament_2.setCourseExecution(courseExecution)
        tournament_2.setStartTime(NOW.plusMinutes(10))
        tournament_2.setEndTime(NOW.plusMinutes(20))
        and: "Save in DB"
        tournamentRepository.save(tournament_2)

        when: "Enroll Players in Tournaments"
        def tournamentDto_1 = tournamentService.enroll(player.getId(), tournament.getId())
        def tournamentDto_2 = tournamentService.enroll(player.getId(), tournament_2.getId())

        then: "Check DB data"
        tournamentDto_1.getPlayersId().contains(player.getId())
        tournamentDto_2.getPlayersId().contains(player.getId())
    }

    def "Enroll a Student twice in the same Opened Tournament" () {

        when: "Enroll Players in Tournaments"
        tournamentService.enroll(player.getId(), tournament.getId())
        tournamentService.enroll(player.getId(), tournament.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == DUPLICATE_TOURNAMENT_ENROLL
    }

    def "Enroll two Students in the same Opened Tournament" () {

        given: "Create Second Player"
        def player_2 = new User(NAME, USERNAME_2, KEY_2, STUDENT)
        player_2.addCourse(courseExecution)
        and: "Save in DB"
        userRepository.save(player_2)

        when: "Enroll Players in Tournaments"
        def tournamentDto_1 = tournamentService.enroll(player.getId(), tournament.getId())
        def tournamentDto_2 = tournamentService.enroll(player_2.getId(), tournament.getId())

        then: "Check DB data"
        tournamentDto_1.getPlayersId().contains(player.getId())
        tournamentDto_2.getPlayersId().contains(player_2.getId())
    }

    @TestConfiguration
    static class EnrollTournamentConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
