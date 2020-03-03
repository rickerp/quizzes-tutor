package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament;
import java.io.Serializable;

public class TournamentDto implements Serializable {

    private int id;
    private Tournament.State role;

    public TournamentDto(Tournament tournament) {
    }
}
