import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicConjunctionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.EnrollService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class EnrollTournamentServiceTest extends Specification {

    @Autowired
    UserService userService

    @Autowired
    EnrollService enrollService

    def user
    def tournament

    def setup() {}

    def 'Enroll #Role User in #State Tournament' () {
        /*  A Student can enroll in an opened Tournament
            The Others combinations should throw an exception */
        expect: false;
    }

    def 'Enroll a Student in two different Tournaments' () {
        /*  A Student should can enroll in two different Tournaments */
        expect: false;
    }

    def 'Enroll a Student twice in the same Tournament' () {
        /*  A Student should not can enroll twice in the same Tournament */
        expect: false;
    }

    def 'Enroll two Students in the same Tournament' () {
        /*  Two Students should can enroll in the same Tournament */
        expect: false;
    }

    def cleanup() {}

    @TestConfiguration
    static class EnrollTournamentServiceTestConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        EnrollService enrollService() {
            return new EnrollService()
        }
    }
}
