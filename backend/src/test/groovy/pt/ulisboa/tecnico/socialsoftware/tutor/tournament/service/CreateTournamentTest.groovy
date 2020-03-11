import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
class CreateTournamentTest extends Specification {

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TournamentService tournamentService

    def setup() {}

    @Unroll('Test: #Role User | #TopicState Topic | #n Questions | #StartTime start time | #EndTime end time || #Message')
    def 'Create a tournament with invalid inputs' () {}

    def 'Create an empty tournament' () {}

    def 'A student create more than one tournament' () {}

    def 'Create two tournaments with the same topic' () {}

    def 'Create a tournament with non-existent topic' () {}

    @TestConfiguration
    static class CreateTournamentConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
