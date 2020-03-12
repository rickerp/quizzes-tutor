package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Service
public class TournamentService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    TopicRepository topicRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto dto) {
        if(dto == null) throw new TutorException(ErrorMessage.INVALID_DTO);

        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, dto.getCreatorId()));

        Set<Topic> topics = new HashSet<>();
        for (Integer topicId : dto.getTopicsId()) {
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new TutorException(ErrorMessage.TOPIC_NOT_FOUND, topicId));
            topics.add(topic);
        }

        Tournament tournament = new Tournament(creator, topics, dto.getNrQuestions(), dto.getStartTime(), dto.getEndTime());
        entityManager.persist(tournament);
        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void enrollPlayer(Integer playerId, Integer tournamentId) {

        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, playerId));
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));

        if (!tournament.isOpened()) {
            throw new TutorException(ErrorMessage.TOURNAMENT_NOT_OPENED);
        }
        if (player.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, playerId);
        }
        tournament.playerEnroll(player);
        player.tournamentEnroll(tournament);
    }
}
