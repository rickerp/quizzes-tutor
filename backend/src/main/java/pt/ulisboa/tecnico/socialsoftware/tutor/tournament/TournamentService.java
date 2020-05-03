package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TournamentService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto dto) {
        if (dto == null) throw new TutorException(ErrorMessage.INVALID_DTO);

        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, dto.getCreatorId()));

        Set<Topic> topics = dto.getTopicsId().stream()
                .map(topicId -> topicRepository.findById(topicId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOPIC_NOT_FOUND, topicId)))
                .collect(Collectors.toSet());

        CourseExecution courseExecution = courseExecutionRepository.findById(dto.getCourseExecutionId())
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, dto.getCourseExecutionId()));

        LocalDateTime startTime = DateHandler.toLocalDateTime(dto.getStartTime());
        LocalDateTime endTime = DateHandler.toLocalDateTime(dto.getEndTime());

        Tournament tournament = new Tournament(dto.getName(), creator, topics, courseExecution, dto.getNrQuestions(), startTime, endTime);
        entityManager.persist(tournament);
        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollPlayer(Integer playerId, Integer tournamentId) {

        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, playerId));
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));

        tournament.playerEnroll(player);
        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getExecutionOpenedTournaments(Integer executionId) {

        return tournamentRepository.findOpenedTournaments(DateHandler.now(), executionId)
                .stream()
                .map(TournamentDto::new)
                .sorted(Comparator.comparing(TournamentDto::getStartTime))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findTournamentCourseExecution(int tournamentId) {
        return this.tournamentRepository.findById(tournamentId)
                .map(Tournament::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
    }
}
