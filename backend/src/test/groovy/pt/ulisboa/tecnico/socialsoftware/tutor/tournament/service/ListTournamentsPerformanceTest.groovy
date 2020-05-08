package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.STUDENT

@DataJpaTest
class ListTournamentsPerformanceTest extends Specification {
    public static final String T_NAME = "Demo-Tournament"
    public static final String NAME = "Name"
    public static final String USERNAME = "Username"
    public static final Integer KEY = 1
    public static final LocalDateTime NOW = DateHandler.now()

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    def creator
    def courseExecution
    def topics

    def setup() {
        "Create a Course Execution"
        courseExecution = new CourseExecution()
        courseExecution.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution)
        "Create a User Creator"
        creator = new User(NAME, USERNAME, KEY, STUDENT)
        creator.addCourse(courseExecution)
        userRepository.save(creator)
        "Create 5 Topics"
        topics = new HashSet<Topic>()
        1.upto(5, {
            Topic topic = new Topic()
            topic.setName(NAME + it)
            topicRepository.save(topic)
            topics.add(topic)
        })
    }

    def "Performance Test to List 1000 times 1000 tournaments"() {

        given: "Create 1000 Tournaments"
        1.upto(1, {
            tournamentRepository.save(
                    new Tournament(T_NAME, creator, topics, courseExecution, 10,
                            NOW.plusMinutes(10), NOW.plusMinutes(20)))
        });

        when: "List 1000 Tournaments 1000 times"
        1.upto(1, { tournamentService.getExecutionOpenedTournaments(courseExecution.getId())})

        then: true
    }

    @TestConfiguration
    static class ListTournamentsPerformanceConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
