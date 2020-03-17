package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
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
    public static final String NAME = "Name"
    public static final String USERNAME_1 = "Username_1"
    public static final String USERNAME_2 = "Username_2"
    public static final Integer KEY_1 = 1
    public static final Integer KEY_2 = 2
    public static final LocalDateTime NOW = LocalDateTime.now()

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    def player
    def tournament

    def setup() {
        "Create User"
        player = new User(NAME, USERNAME_1, KEY_1, STUDENT)
        "Create Tournament"
        tournament = new Tournament()
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
        tournamentService.enrollPlayer(player.getId(), tournament.getId())

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
        tournamentService.enrollPlayer(player.getId(), tournament.getId())

        then: "Get DB data"
        def playerSet = tournament.getPlayers()
        def tournamentSet = player.getTournaments()
        and: "Check DB data"
        playerSet.contains(player)
        tournamentSet.contains(tournament)
    }

    def "Enroll a Student in two different Opened Tournaments" () {

        given: "Create Second Tournament"
        def tournament_2 = new Tournament()
        tournament_2.setStartTime(NOW.plusMinutes(10))
        tournament_2.setEndTime(NOW.plusMinutes(20))
        and: "Save in DB"
        tournamentRepository.save(tournament_2)

        when: "Enroll Players in Tournaments"
        tournamentService.enrollPlayer(player.getId(), tournament.getId())
        tournamentService.enrollPlayer(player.getId(), tournament_2.getId())

        then: "Get DB data"
        def tournamentSet = player.getTournaments()
        def playerSet = tournament.getPlayers()
        def playerSet_2 = tournament_2.getPlayers()
        and: "Check DB data"
        tournamentSet.contains(tournament)
        tournamentSet.contains(tournament_2)
        playerSet.contains(player)
        playerSet_2.contains(player)
    }

    def "Enroll a Student twice in the same Opened Tournament" () {

        when: "Enroll Players in Tournaments"
        tournamentService.enrollPlayer(player.getId(), tournament.getId())
        tournamentService.enrollPlayer(player.getId(), tournament.getId())

        then: "Check Exception"
        def error = thrown(TutorException)
        error.getErrorMessage() == DUPLICATE_TOURNAMENT_ENROLL
    }

    def "Enroll two Students in the same Opened Tournament" () {

        given: "Create Second Player"
        def player_2 = new User(NAME, USERNAME_2, KEY_2, STUDENT)
        and: "Save in DB"
        userRepository.save(player_2)

        when: "Enroll Players in Tournaments"
        tournamentService.enrollPlayer(player.getId(), tournament.getId())
        tournamentService.enrollPlayer(player_2.getId(), tournament.getId())

        then: "Check DB data"
        def playerSet = tournament.getPlayers()
        def tournamentSet = player.getTournaments()
        def tournamentSet_2 = player_2.getTournaments()
        and: "Check DB data"
        playerSet.contains(player)
        playerSet.contains(player_2)
        tournamentSet.contains(tournament)
        tournamentSet_2.contains(tournament)
    }

    @TestConfiguration
    static class EnrollTournamentConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
