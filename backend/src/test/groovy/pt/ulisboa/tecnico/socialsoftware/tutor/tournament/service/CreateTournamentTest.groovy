package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

import java.time.LocalDateTime

@DataJpaTest
class CreateTournamentTest extends Specification {

    public static final LocalDateTime now = LocalDateTime.now()

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    def creator, topic

    def setup() {
        'Create a objects'
        creator = new User("Ricardo", "rickerp", 1, User.Role.STUDENT)
        topic = new Topic()

        'Store data in DB'
        userRepository.save(creator)
        topicRepository.save(topic)
    }

    def 'Create a tournament with valid inputs' () {
        /* A Tournament should be properly created */
        given: 'a tournament dto'
        def dto = new TournamentDto()
        dto.setCreatorId(creator.getId())
        dto.setTopicId(topic.getId())
        dto.setStartTime(now.plusHours(1))
        dto.setEndTime(dto.getStartTime().plusMinutes(5))
        dto.setNrQuestions(5)

        when: 'given a dto to tournament service'
        def ret_dto = tournamentService.createTournament(dto)

        then: 'check if the tournament was created correctly'
        def ret = (Tournament)tournamentRepository.findById(ret_dto.getId()).orElse(null)
        ret != null
        ret.getCreator().getId() == dto.getCreatorId()
        ret.getTopic().getId() == dto.getTopicId()
        ret.getStartTime() == dto.getStartTime()
        ret.getEndTime() == dto.getEndTime()
        ret.getNrQuestions() == dto.getNrQuestions()
    }

    @Unroll('Invalid inputs: #Role User | #TopicStatus Topic | #nQuestions Questions | #StartTime start time | #EndTime end time || #Message')
    def 'Create a tournament with invalid inputs' () {
        /* A Tournament should not be created and should throw and exception */
        given: 'a set of invalid inputs'
        creator.setRole(Role)
        topic.setStatus(TopicStatus)
        and: 'a tournament dto'
        def dto = new TournamentDto()
        dto.setCreatorId(creator.getId())
        dto.setTopicId(topic.getId())
        dto.setStartTime(StartTime)
        dto.setEndTime(EndTime)
        dto.setNrQuestions(nQuestions)

        when: 'given a tournament dto to the service'
        tournamentService.createTournament(dto)

        then: 'check if an exception was thrown'
        def error = thrown(TutorException)
        error.getErrorMessage() == Message

        where:
        Role                    | TopicStatus               | nQuestions    | StartTime             | EndTime               || Message
        User.Role.ADMIN         | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(10)   | now.plusMinutes(20)   || USER_NOT_STUDENT
        User.Role.TEACHER       | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(10)   | now.plusMinutes(20)   || USER_NOT_STUDENT
        User.Role.DEMO_ADMIN    | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(10)   | now.plusMinutes(20)   || USER_NOT_STUDENT
        null                    | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(10)   | now.plusMinutes(20)   || USER_NOT_STUDENT
        User.Role.STUDENT       | Topic.Status.DISABLED     | 5             | now.plusMinutes(10)   | now.plusMinutes(20)   || TOPIC_NOT_AVAILABLE
        User.Role.STUDENT       | Topic.Status.REMOVED      | 5             | now.plusMinutes(10)   | now.plusMinutes(20)   || TOPIC_NOT_AVAILABLE
        User.Role.STUDENT       | null                      | 5             | now.plusMinutes(10)   | now.plusMinutes(20)   || TOPIC_NOT_AVAILABLE
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 0             | now.plusMinutes(10)   | now.plusMinutes(20)   || TOURNAMENT_NR_QUESTIONS_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | -1            | now.plusMinutes(10)   | now.plusMinutes(20)   || TOURNAMENT_NR_QUESTIONS_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | null          | now.plusMinutes(10)   | now.plusMinutes(20)   || TOURNAMENT_NR_QUESTIONS_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(-10)  | now.plusMinutes(20)   || TOURNAMENT_START_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | now                   | now.plusMinutes(20)   || TOURNAMENT_START_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | null                  | now.plusMinutes(20)   || TOURNAMENT_START_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(10)   | now.plusMinutes(5)    || TOURNAMENT_END_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(10)   | now.plusMinutes(10)   || TOURNAMENT_END_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | now.plusMinutes(10)   | null                  || TOURNAMENT_END_TIME_INVALID
    }

    def 'Create an empty tournament' () {
        /* A Tournament should not be created and should throw and exception */

        when: 'given a null dto to tournament service'
        tournamentService.createTournament(null)

        then: 'check if an exception was thrown'
        def error = thrown(TutorException)
        error.getErrorMessage() == INVALID_DTO
    }

    def 'A student can create duplicate tournaments' () {
        /* Two Tournaments with the same info should be properly created */
        given: 'a tournament dto'
        def dto = new TournamentDto()
        dto.setCreatorId(creator.getId())
        dto.setTopicId(topic.getId())
        dto.setStartTime(now.plusHours(1))
        dto.setEndTime(dto.getStartTime().plusMinutes(5))
        dto.setNrQuestions(5)

        when: 'given a dto to tournament service'
        def ret_dto_1 = tournamentService.createTournament(dto)
        def ret_dto_2 = tournamentService.createTournament(dto)

        then: 'check if the tournament was created correctly'
        def ret_1 = tournamentRepository.findById(ret_dto_1.getId()).orElse(null)
        ret_1 != null
        ret_1.getCreator().getId() == dto.getCreatorId()
        ret_1.getTopic().getId() == dto.getTopicId()
        ret_1.getStartTime() == dto.getStartTime()
        ret_1.getEndTime() == dto.getEndTime()
        ret_1.getNrQuestions() == dto.getNrQuestions()
        def ret_2 = tournamentRepository.findById(ret_dto_2.getId()).orElse(null)
        ret_2 != null
        ret_2.getCreator().getId() == dto.getCreatorId()
        ret_2.getTopic().getId() == dto.getTopicId()
        ret_2.getStartTime() == dto.getStartTime()
        ret_2.getEndTime() == dto.getEndTime()
        ret_2.getNrQuestions() == dto.getNrQuestions()
    }

    def 'Create a tournament with non-existent topic' () {
        /* A Tournament with a non-existent topic should not be created */
        given: 'a tournament dto'
        def dto = new TournamentDto()
        dto.setCreatorId(creator.getId())
        dto.setStartTime(now.plusHours(1))
        dto.setEndTime(dto.getStartTime().plusMinutes(5))
        dto.setNrQuestions(5)

        when: 'given a dto with no topic to tournament service'
        tournamentService.createTournament(dto)

        then: 'check if an exception was thrown'
        def error = thrown(TutorException)
        error.getErrorMessage() == TOPIC_NOT_FOUND
    }

    @TestConfiguration
    static class CreateTournamentConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
