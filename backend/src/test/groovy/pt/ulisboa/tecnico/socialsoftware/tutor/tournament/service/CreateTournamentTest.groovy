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

import java.util.stream.Collectors

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

import java.time.LocalDateTime

@DataJpaTest
class CreateTournamentTest extends Specification {
    public static final String NAME_1 = "Name_1"
    public static final String NAME_2 = "Name_2"
    public static final String USERNAME = "Username"
    public static final Integer KEY = 1
    public static final LocalDateTime NOW = LocalDateTime.now()

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    def creator
    def topic

    def setup() {
        "Create a objects"
        creator = new User(NAME_1, USERNAME, KEY, User.Role.STUDENT)
        topic = new Topic()
        topic.setName(NAME_1)
        "Store data in DB"
        userRepository.save(creator)
        topicRepository.save(topic)
    }

    def "Create a tournament with valid inputs" () {

        given: "a tournament dto"
        def dto = new TournamentDto()
        Set<Integer> topicsId = new HashSet<>()
        topicsId.add(topic.getId())
        dto.setCreatorId(creator.getId())
        dto.setTopicsId(topicsId)
        dto.setStartTime(NOW.plusHours(1))
        dto.setEndTime(dto.getStartTime().plusMinutes(5))
        dto.setNrQuestions(5)

        when: "given a dto to tournament service"
        def dtoCreated = tournamentService.createTournament(dto)

        then: "check if the tournament was created correctly"
        Tournament tournament = tournamentRepository.findById(dtoCreated.getId()).orElse(null)
        tournament != null
        tournament.getCreator().getId() == dto.getCreatorId()
        tournament.getStartTime() == dto.getStartTime()
        tournament.getEndTime() == dto.getEndTime()
        tournament.getNrQuestions() == dto.getNrQuestions()
        tournament.getTopics().stream().map({ topic -> topic.getId() })
                .collect(Collectors.toSet()) == dto.getTopicsId()
    }

    @Unroll("Invalid inputs: #Role User | #TopicStatus Topic | #nQuestions Questions | #StartTime start time | #EndTime end time || #Message")
    def "Create a tournament with invalid inputs" () {

        given: "a set of invalid inputs"
        creator.setRole(Role)
        topic.setStatus(TopicStatus)
        and: 'a tournament dto'
        def dto = new TournamentDto()
        Set<Integer> topicsId = new HashSet<>()
        topicsId.add(topic.getId())
        dto.setCreatorId(creator.getId())
        dto.setTopicsId(topicsId)
        dto.setStartTime(StartTime)
        dto.setEndTime(EndTime)
        dto.setNrQuestions(nQuestions)

        when: "given a tournament dto to the service"
        tournamentService.createTournament(dto)

        then: "check if an exception was thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == Message

        where:
        Role                    | TopicStatus               | nQuestions    | StartTime             | EndTime               || Message
        User.Role.ADMIN         | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || USER_NOT_STUDENT
        User.Role.TEACHER       | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || USER_NOT_STUDENT
        User.Role.DEMO_ADMIN    | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || USER_NOT_STUDENT
        null                    | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || USER_NOT_STUDENT
        User.Role.STUDENT       | Topic.Status.DISABLED     | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || TOPIC_NOT_AVAILABLE
        User.Role.STUDENT       | Topic.Status.REMOVED      | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || TOPIC_NOT_AVAILABLE
        User.Role.STUDENT       | null                      | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || TOPIC_NOT_AVAILABLE
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 0             | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || TOURNAMENT_NR_QUESTIONS_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | -1            | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || TOURNAMENT_NR_QUESTIONS_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | null          | NOW.plusMinutes(10)   | NOW.plusMinutes(20)   || TOURNAMENT_NR_QUESTIONS_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(-10)  | NOW.plusMinutes(20)   || TOURNAMENT_START_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | NOW                   | NOW.plusMinutes(20)   || TOURNAMENT_START_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | null                  | NOW.plusMinutes(20)   || TOURNAMENT_START_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(5)    || TOURNAMENT_END_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(10)   | NOW.plusMinutes(10)   || TOURNAMENT_END_TIME_INVALID
        User.Role.STUDENT       | Topic.Status.AVAILABLE    | 5             | NOW.plusMinutes(10)   | null                  || TOURNAMENT_END_TIME_INVALID
    }

    def "Create an empty tournament" () {

        when: "given a null dto to tournament service"
        tournamentService.createTournament(null)

        then: "check if an exception was thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == INVALID_DTO
    }

    def "A student can create duplicate tournaments" () {

        given: "a tournament dto"
        def dto = new TournamentDto()
        Set<Integer> topicsId = new HashSet<>()
        topicsId.add(topic.getId())
        dto.setCreatorId(creator.getId())
        dto.setTopicsId(topicsId)
        dto.setStartTime(NOW.plusHours(1))
        dto.setEndTime(dto.getStartTime().plusMinutes(5))
        dto.setNrQuestions(5)

        when: "given a dto to tournament service"
        def dtoCreated_1 = tournamentService.createTournament(dto)
        def dtoCreated_2 = tournamentService.createTournament(dto)

        then: "check if the tournament was created correctly"
        Tournament tournament_1 = tournamentRepository.findById(dtoCreated_1.getId()).orElse(null)
        tournament_1 != null
        tournament_1.getCreator().getId() == dto.getCreatorId()
        tournament_1.getStartTime() == dto.getStartTime()
        tournament_1.getEndTime() == dto.getEndTime()
        tournament_1.getNrQuestions() == dto.getNrQuestions()
        tournament_1.getTopics().stream().map({ topic -> topic.getId() })
                .collect(Collectors.toSet()) == dto.getTopicsId()
        Tournament tournament_2 = tournamentRepository.findById(dtoCreated_2.getId()).orElse(null)
        tournament_2 != null
        tournament_2.getCreator().getId() == dto.getCreatorId()
        tournament_2.getStartTime() == dto.getStartTime()
        tournament_2.getEndTime() == dto.getEndTime()
        tournament_2.getNrQuestions() == dto.getNrQuestions()
        tournament_2.getTopics().stream().map({ topic -> topic.getId() })
                .collect(Collectors.toSet()) == dto.getTopicsId()
    }

    def "Create a tournament with non-existent topic" () {

        given: "a tournament dto"
        def dto = new TournamentDto()
        Set<Integer> topicsId = new HashSet<>()
        dto.setCreatorId(creator.getId())
        dto.setTopicsId(topicsId)
        dto.setStartTime(NOW.plusHours(1))
        dto.setEndTime(dto.getStartTime().plusMinutes(5))
        dto.setNrQuestions(5)

        when: "given a dto with no topic to tournament service"
        tournamentService.createTournament(dto)

        then: "check if an exception was thrown"
        def error = thrown(TutorException)
        error.getErrorMessage() == TOPIC_NOT_FOUND
    }

    def "Create a tournament with more than one topic" () {

        given: "a new topic"
        def topic_2 = new Topic()
        topic_2.setName(NAME_2)
        topicRepository.save(topic_2)
        and: "a tournament dto"
        def dto = new TournamentDto()
        Set<Integer> topicsId = new HashSet<>()
        topicsId.add(topic.getId())
        topicsId.add(topic_2.getId())
        dto.setCreatorId(creator.getId())
        dto.setTopicsId(topicsId)
        dto.setStartTime(NOW.plusHours(1))
        dto.setEndTime(dto.getStartTime().plusMinutes(5))
        dto.setNrQuestions(5)

        when: "given a dto to tournament service"
        def dtoCreated = tournamentService.createTournament(dto)

        then: "check if the tournament was created correctly"
        Tournament tournament = tournamentRepository.findById(dtoCreated.getId()).orElse(null)
        tournament != null
        tournament.getCreator().getId() == dto.getCreatorId()
        tournament.getStartTime() == dto.getStartTime()
        tournament.getEndTime() == dto.getEndTime()
        tournament.getNrQuestions() == dto.getNrQuestions()
        tournament.getTopics().stream().map({ topic -> topic.getId() })
                .collect(Collectors.toSet()) == dto.getTopicsId()
    }

    @TestConfiguration
    static class CreateTournamentConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
