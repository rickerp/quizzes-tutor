package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Set;

@Service
public class TournamentService {

    @PersistenceContext
    EntityManager entityManager;

    public void enrollPlayer(Integer userId, Integer tournamentId) {
        /* TODO */
    }

    public Set<User> getPlayers(Integer tournamentId) {
        /* TODO */
        return new HashSet<User>();
    }
}
