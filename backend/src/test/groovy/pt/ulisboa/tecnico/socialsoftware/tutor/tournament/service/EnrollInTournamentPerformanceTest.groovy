package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
class EnrollInTournamentPerformanceTest extends Specification {
    public static final String NAME = "Name"
    public static final String USERNAME = "Username"
    public static final Integer KEY = 1
    public static final LocalDateTime NOW = LocalDateTime.now()

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
    def usersIds
    def tournamentsIds

    def setup() {
        "Create Course Execution"
        courseExecution = new CourseExecution()
        courseExecution.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution)
        "Create User Creator"
        creator = new User(NAME, USERNAME, KEY, STUDENT)
        creator.addCourse(courseExecution)
        userRepository.save(creator)
        "Create Topics"
        topics = new HashSet<Topic>()
        1.upto(5, {
            Topic topic = new Topic()
            topic.setName(NAME + it)
            topicRepository.save(topic)
            topics.add(topic)
        })
        "Create empty list of users and tournaments"
        usersIds = new ArrayList<Integer>()
        tournamentsIds = new ArrayList<Integer>()
    }

    def "Performance Test to 1000 User(s) Enroll in 100 Tournament(s)"() {

        given: "100 tournament(s)"
        1.upto(1, {
            def tournament = new Tournament(creator, topics, courseExecution, 10, NOW.plusMinutes(10), NOW.plusMinutes(20))
            tournamentRepository.save(tournament)
            tournamentsIds.add(tournament.getId())
        })

        and: "1000 user(s)"
        1.upto(1, {
            def user = new User(NAME, USERNAME+it, KEY+it, STUDENT)
            user.addCourse(courseExecution)
            userRepository.save(user)
            usersIds.add(user.getId())
        })

        when: "each user enrolls in one tournament"
        for (Integer uId : usersIds) {
            for (Integer tId : tournamentsIds) {
                tournamentService.enrollPlayer(uId, tId)
            }
        }

        then: true
    }

    @TestConfiguration
    static class EnrollInPerformanceConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
