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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import static pt.ulisboa.tecnico.socialsoftware.tutor.user.User.Role.STUDENT

@DataJpaTest
class ListTournamentServiceTest extends Specification {
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
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    def creator
    def courseExecution
    def topics
    def opened_t
    def in_progress_t
    def closed_t

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
        "Create Opened Tournaments"
        opened_t = new Tournament()
        opened_t.setName(T_NAME)
        opened_t.setCreator(creator)
        opened_t.setTopics(topics)
        opened_t.setNrQuestions(10)
        opened_t.setCourseExecution(courseExecution)
        opened_t.setStartTime(NOW.plusMinutes(10))
        opened_t.setEndTime(NOW.plusMinutes(20))
        tournamentRepository.save(opened_t)
        "Create In Progress Tournaments"
        in_progress_t = new Tournament()
        in_progress_t.setName(T_NAME)
        in_progress_t.setCreator(creator)
        in_progress_t.setTopics(topics)
        in_progress_t.setNrQuestions(10)
        in_progress_t.setCourseExecution(courseExecution)
        in_progress_t.setStartTime(NOW.plusMinutes(-10))
        in_progress_t.setEndTime(NOW.plusMinutes(10))
        tournamentRepository.save(in_progress_t)
        "Create Closed Tournaments"
        closed_t = new Tournament()
        closed_t.setName(T_NAME)
        closed_t.setCreator(creator)
        closed_t.setTopics(topics)
        closed_t.setNrQuestions(10)
        closed_t.setCourseExecution(courseExecution)
        closed_t.setStartTime(NOW.plusMinutes(-20))
        closed_t.setEndTime(NOW.plusMinutes(-10))
        tournamentRepository.save(closed_t)
    }

    def "List one Opened Tournament"() {

        when: "Get data from DB"
        def tournaments = tournamentService.getExecutionOpenedTournaments(courseExecution.getId())

        then: "Check data in DB"
        tournaments.size() == 1
        tournaments.get(0).getId() == opened_t.getId()
    }

    def 'List two Opened Tournaments ordered by StartTime'() {

        given: "an Opened Tournament"
        def opened_t_2 = new Tournament()
        opened_t_2.setName(T_NAME)
        opened_t_2.setCreator(creator)
        opened_t_2.setTopics(topics)
        opened_t_2.setNrQuestions(10)
        opened_t_2.setCourseExecution(courseExecution)
        opened_t_2.setStartTime(NOW.plusMinutes(20))
        opened_t_2.setEndTime(NOW.plusMinutes(30))
        tournamentRepository.save(opened_t_2)

        when: "Get data from DB"
        def tournaments = tournamentService.getExecutionOpenedTournaments(courseExecution.getId())

        then: "Check data in DB"
        tournaments.size() == 2
        tournaments.get(0).getId() == opened_t.getId()
        tournaments.get(1).getId() == opened_t_2.getId()
    }

    def 'List Zero Opened Tournaments'() {

        given: "a Course Execution"
        def courseExecution_2 = new CourseExecution()
        courseExecution_2.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution_2)

        when: "Get data from DB"
        def tournaments = tournamentService.getExecutionOpenedTournaments(courseExecution_2.getId())

        then: "Check data in DB"
        tournaments.size() == 0
    }

    def 'List the Opened Tournament of a Course Execution in DB'() {

        given: "a Course Execution"
        def courseExecution_2 = new CourseExecution()
        courseExecution_2.setStatus(CourseExecution.Status.ACTIVE)
        courseExecutionRepository.save(courseExecution_2)
        and: "an Opened Tournament"
        def opened_t_2 = new Tournament()
        opened_t_2.setName(T_NAME)
        opened_t_2.setCreator(creator)
        opened_t_2.setTopics(topics)
        opened_t_2.setNrQuestions(10)
        opened_t_2.setCourseExecution(courseExecution_2)
        opened_t_2.setStartTime(NOW.plusMinutes(20))
        opened_t_2.setEndTime(NOW.plusMinutes(30))
        tournamentRepository.save(opened_t_2)

        when: "Get data from DB"
        def tournaments = tournamentService.getExecutionOpenedTournaments(courseExecution_2.getId())

        then: "Check data in DB"
        tournaments.size() == 1
        tournaments.get(0).getId() == opened_t_2.getId()
    }

    @TestConfiguration
    static class ListTournamentConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
