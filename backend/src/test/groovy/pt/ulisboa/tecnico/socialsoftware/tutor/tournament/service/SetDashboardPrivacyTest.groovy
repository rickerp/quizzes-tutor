package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.STUDENT

@DataJpaTest
class SetDashboardPrivacyTest extends Specification {
    public static final String NAME = "Name"
    public static final Integer KEY = 1
    public static final String USERNAME = "Username_" + KEY

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentService tournamentService

    def user

    def setup() {
        user = new User(NAME, USERNAME, KEY, STUDENT)
        userRepository.save(user)
    }

    def "Set Dashboard to Public State"() {

        when:
        Boolean isPublic = user.isTournamentDashboardPublic()
        tournamentService.setDashboardPrivacy(user.getId(), true)

        then: "Check Privacy"
        !isPublic
        user.isTournamentDashboardPublic()
    }

    def "Set Dashboard to Private State"() {

        when:
        Boolean isPublic = user.isTournamentDashboardPublic()
        tournamentService.setDashboardPrivacy(user.getId(), false)

        then: "Check Privacy"
        !isPublic
        !user.isTournamentDashboardPublic()
    }

    def "Set Dashboard to Private and then to Public State"() {

        when:
        tournamentService.setDashboardPrivacy(user.getId(), false)
        tournamentService.setDashboardPrivacy(user.getId(), true)

        then: "Check Privacy"
        user.isTournamentDashboardPublic()
    }

    def "Set Dashboard to Public and then to Private State"() {

        when:
        tournamentService.setDashboardPrivacy(user.getId(), true)
        tournamentService.setDashboardPrivacy(user.getId(), false)

        then: "Check Privacy"
        !user.isTournamentDashboardPublic()
    }

    def "Set Dashboard to Public and then to Public State"() {

        when:
        tournamentService.setDashboardPrivacy(user.getId(), true)
        tournamentService.setDashboardPrivacy(user.getId(), true)

        then: "Check Privacy"
        user.isTournamentDashboardPublic()
    }


    def "Set Dashboard to Private and then to Private State"() {

        when:
        tournamentService.setDashboardPrivacy(user.getId(), false)
        tournamentService.setDashboardPrivacy(user.getId(), false)

        then: "Check Privacy"
        !user.isTournamentDashboardPublic()
    }

    @TestConfiguration
    static class SetDashboardPrivacyConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
