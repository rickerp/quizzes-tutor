package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentDashboardDto implements Serializable {

    private Boolean isPublic;
    private Integer nrQuestions;
    private Integer nrAnswers;
    private Integer nrCorrectAnswers;
    List<TournamentAnswerDto> tournamentScores;

    public TournamentDashboardDto() {}

    public TournamentDashboardDto(User user, int executionId) {

        setIsPublic(user.isTournamentDashboardPublic());
        setNrQuestions(user.getNrTournamentQuestions());
        setNrAnswers(user.getNrTournamentAnswers());
        setNrCorrectAnswers(user.getNrTournamentCorrectAnswers());

        this.tournamentScores = user.getTournamentAnswers().stream()
                .filter(tA -> tA.getTournament().getCourseExecution().getId() == executionId && tA.isFinished())
                .sorted((tCA, tCB) -> tCB.getFinishTime().compareTo(tCA.getFinishTime()))
                .map(TournamentAnswerDto::new)
                .collect(Collectors.toList());
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Integer getNrQuestions() {
        return nrQuestions;
    }

    public void setNrQuestions(Integer nrQuestions) {
        this.nrQuestions = nrQuestions;
    }

    public Integer getNrAnswers() {
        return nrAnswers;
    }

    public void setNrAnswers(Integer nrAnswers) {
        this.nrAnswers = nrAnswers;
    }

    public Integer getNrCorrectAnswers() { return nrCorrectAnswers; }

    public void setNrCorrectAnswers(Integer nrCorrectAnswers) { this.nrCorrectAnswers = nrCorrectAnswers; }

    public List<TournamentAnswerDto> getTournamentScores() { return tournamentScores; }

    public void setTournamentScores(List<TournamentAnswerDto> tournamentScores) {
        this.tournamentScores = tournamentScores;
    }
}
