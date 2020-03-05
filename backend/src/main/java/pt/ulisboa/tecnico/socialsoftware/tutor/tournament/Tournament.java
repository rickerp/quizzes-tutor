package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import javax.persistence.*;

@Entity
@Table(name = "tournaments")
public class Tournament {

    public enum State {OPENED, CLOSED}

    private Tournament.State state;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
}
