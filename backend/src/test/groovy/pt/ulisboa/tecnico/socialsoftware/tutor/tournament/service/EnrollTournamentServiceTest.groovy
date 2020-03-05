package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class EnrollTournamentServiceTest extends Specification {

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserService userService

    @Autowired
    TournamentService tournamentService

    def player_1
    def tournament_1

    def setup() {
        player_1 = new User()
        userRepository.save(player_1)
        tournament_1 = new Tournament()
        tournamentRepository.save(tournament_1)
    }

    @Unroll('Test: #Role User | #State Tournament || #Message')
    def 'Invalid Tournament Enroll Arguments' () {
        /* The User Role & Tournament State Combinations should throw an Exception */
        given: 'Set Players Role & Tournaments State'
        player_1.setRole(Role)
        tournament_1.setState(State)

        when: 'Enroll Players in Tournaments'
        tournamentService.enrollPlayer(player_1.getId(), tournament_1.getId())

        then: 'Check Exception'
        def error = thrown(TutorException)
        error.getErrorMessage() == Message

        where:
        Role                | State                     || Message
        User.Role.ADMIN     | Tournament.State.CLOSED   || ErrorMessage.INVALID_USER_ROLE
        User.Role.ADMIN     | Tournament.State.OPENED   || ErrorMessage.INVALID_USER_ROLE
        User.Role.TEACHER   | Tournament.State.CLOSED   || ErrorMessage.INVALID_USER_ROLE
        User.Role.TEACHER   | Tournament.State.OPENED   || ErrorMessage.INVALID_USER_ROLE
        User.Role.STUDENT   | Tournament.State.CLOSED   || ErrorMessage.INVALID_TOURNAMENT_STATE
    }

    def 'Enroll a Student in an Opened Tournament' () {
        /* A Student should can enroll in an Opened Tournament */
        given: 'Set Players Role & Tournaments State'
        tournament_1.setState(Tournament.State.OPENED)
        player_1.setRole(User.Role.STUDENT)

        when: 'Enroll Players in Tournaments'
        tournamentService.enrollPlayer(player_1.getId(), tournament_1.getId())
        and: 'Get from DB'
        def tournament_1_players = tournamentService.getPlayers(tournament_1.getId())
        def player_1_tournaments = userService.getTournaments(player_1.getId())

        then: 'Check DB data'
        tournament_1_players.contains(player_1)
        player_1_tournaments.contains(tournament_1)
    }

    def 'Enroll a Student in two different Opened Tournaments' () {
        /* A Student should can enroll in two different Opened Tournaments */
        given: 'Create Second Tournament'
        def tournament_2 = new Tournament()
        and: 'Set Players Role & Tournaments State'
        tournament_1.setState(Tournament.State.OPENED)
        tournament_2.setState(Tournament.State.OPENED)
        player_1.setRole(User.Role.STUDENT)
        and: 'Save in DB'
        tournamentRepository.save(tournament_2)

        when: 'Enroll Players in Tournaments'
        tournamentService.enrollPlayer(player_1.getId(), tournament_1.getId())
        tournamentService.enrollPlayer(player_1.getId(), tournament_2.getId())
        and: 'Get from DB'
        def tournament_1_players = tournamentService.getPlayers(tournament_1.getId())
        def tournament_2_players = tournamentService.getPlayers(tournament_2.getId())
        def player_1_tournaments = userService.getTournaments(player_1.getId())

        then: 'Check DB data'
        tournament_1_players.contains(player_1)
        tournament_2_players.contains(player_1)
        player_1_tournaments.contains(tournament_1)
        player_1_tournaments.contains(tournament_2)
    }

    def 'Enroll a Student twice in the same Opened Tournament' () {
        /* The second Enroll should throw an Exception */
        given: 'Set Players Role & Tournaments State'
        tournament_1.setState(Tournament.State.OPENED)
        player_1.setRole(User.Role.STUDENT)

        when: 'Enroll Players in Tournaments'
        tournamentService.enrollPlayer(player_1.getId(), tournament_1.getId())
        tournamentService.enrollPlayer(player_1.getId(), tournament_1.getId())

        then: 'Check Exception'
        def error = thrown(TutorException)
        error.getErrorMessage() == ErrorMessage.DUPLICATE_TOURNAMENT_ENROLL
    }

    def 'Enroll two Students in the same Opened Tournament' () {
        /* Two Students should can enroll in the same Opened Tournament */
        given: 'Create Second Player'
        def player_2 = new User()
        and: 'Set Players Role & Tournaments State'
        tournament_1.setState(Tournament.State.OPENED)
        player_1.setRole(User.Role.STUDENT)
        player_2.setRole(User.Role.STUDENT)
        and: 'Save in DB'
        userRepository.save(player_2)

        when: 'Enroll Players in Tournaments'
        tournamentService.enrollPlayer(player_1.getId(), tournament_1.getId())
        tournamentService.enrollPlayer(player_2.getId(), tournament_1.getId())
        and: 'Get from DB'
        def tournament_1_players = tournamentService.getPlayers(tournament_1.getId())
        def player_1_tournaments = userService.getTournaments(player_1.getId())
        def player_2_tournaments = userService.getTournaments(player_2.getId())

        then: 'Check DB data'
        tournament_1_players.contains(player_1)
        tournament_1_players.contains(player_2)
        player_1_tournaments.contains(tournament_1)
        player_2_tournaments.contains(tournament_1)
    }

    @TestConfiguration
    static class EnrollTournamentConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
