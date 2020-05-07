package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentAnswer;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TournamentAnswerDto implements Serializable {

    private int tournamentId;
    private String tournamentName;
    private String[] topicsName;
    private int nrQuestions;
    private int nrCorrectAnswers;
    private String finishTime;

    public TournamentAnswerDto() {}

    public TournamentAnswerDto(TournamentAnswer tournamentAnswer) {

        Tournament t = tournamentAnswer.getTournament();
        setTournamentId(t.getId());
        setTournamentName(t.getName());
        setTopicsName(t.getTopics().stream().map(Topic::getName).sorted().toArray(String[]::new));
        setNrQuestions(t.getNrQuestions());
        setFinishTime(tournamentAnswer.getFinishTime());
        setNrCorrectAnswers(tournamentAnswer.getNrCorrectAnswers());
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String[] getTopicsName() {
        return topicsName;
    }

    public void setTopicsName(String[] topicsName) {
        this.topicsName = topicsName;
    }

    public Integer getNrQuestions() {
        return nrQuestions;
    }

    public void setNrQuestions(Integer nrQuestions) {
        this.nrQuestions = nrQuestions;
    }

    public Integer getNrCorrectAnswers() {
        return nrCorrectAnswers;
    }

    public void setNrCorrectAnswers(Integer nrCorrectAnswers) { this.nrCorrectAnswers = nrCorrectAnswers; }

    public String getFinishTime() { return finishTime; }

    public void setFinishTime(String finishTime) { this.finishTime = finishTime; }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = DateHandler.toISOString(finishTime);
    }
}
