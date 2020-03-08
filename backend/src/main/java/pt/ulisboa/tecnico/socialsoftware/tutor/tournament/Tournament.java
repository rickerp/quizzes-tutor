package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournaments")
public class Tournament {

    public enum State {OPENED, IN_PROGRESS, CLOSED}

    private Tournament.State state;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(mappedBy = "tournaments")
    private Set<User> players = new HashSet<>();

    public Tournament() {
    }

    public Tournament.State getState() {
        return this.state;
    }

    public void setState(Tournament.State state) {
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void playerEnroll(User player) {
        if (!players.add(player)) {
            throw new TutorException(ErrorMessage.DUPLICATE_TOURNAMENT_ENROLL);
        }
    }

    public HashSet<User> getPlayers() {
        return new HashSet<>(this.players);
    }
}
