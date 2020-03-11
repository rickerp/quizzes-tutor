package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament;
import java.io.Serializable;

public class TournamentDto implements Serializable {

    private int id;
    private Tournament.State state;

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.state = tournament.getState();
    }

    public Tournament.State getState() {
        return this.state;
    }

    public Integer getId() { return id; }
}
