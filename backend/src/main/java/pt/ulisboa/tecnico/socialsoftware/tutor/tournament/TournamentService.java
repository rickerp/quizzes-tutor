package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class TournamentService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TournamentRepository tournamentRepository;

    @PersistenceContext
    EntityManager entityManager;

    public void enrollPlayer(Integer playerId, Integer tournamentId) {

        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, playerId));
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));

        if (tournament.getState() != Tournament.State.OPENED) {
            throw new TutorException(ErrorMessage.TOURNAMENT_NOT_OPENED);
        }
        if (player.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.INVALID_USER_ROLE);
        }
        tournament.playerEnroll(player);
        player.tournamentEnroll(tournament);
    }
}
