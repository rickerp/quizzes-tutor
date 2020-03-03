package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import javax.persistence.*;

@Entity
@Table(name = "tournaments")
public class Tournament {
    public enum State {OPENED, CLOSED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
}
