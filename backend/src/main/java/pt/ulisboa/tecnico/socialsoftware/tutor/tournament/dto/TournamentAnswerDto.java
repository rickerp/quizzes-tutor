package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentAnswer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentAnswerDto implements Serializable {
    private int tournamentId;
    private String tournamentName;
    private String[] topicsName;
    private int nrQuestions;
    private int nrQuestionsAnswered;
    private int nrCorrectAnswers;
    private String finishTime;

    public TournamentAnswerDto() {}

    public TournamentAnswerDto(TournamentAnswer tournamentAnswer) {
        Tournament tournament = tournamentAnswer.getTournament();
        setTournamentId(tournament.getId());
        setTournamentName(tournament.getName());
        setTopicsName(tournament.getTopics().stream().map(Topic::getName).toArray(String[]::new));
        setNrQuestions(tournament.getNrQuestions());
        setNrQuestionsAnswered(tournamentAnswer.getQuestionAnswers().stream().filter(qA -> qA.getOption() != null).toArray().length);
        setNrCorrectAnswers(tournamentAnswer.getQuestionAnswers().stream().filter(QuestionAnswer::isCorrect).toArray().length);
        setFinishTime(tournamentAnswer.getFinishTime());
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

    public int getNrQuestions() {
        return nrQuestions;
    }

    public void setNrQuestions(int nrQuestions) {
        this.nrQuestions = nrQuestions;
    }

    public int getNrQuestionsAnswered() {
        return nrQuestionsAnswered;
    }

    public void setNrQuestionsAnswered(int nrQuestionsAnswered) {
        this.nrQuestionsAnswered = nrQuestionsAnswered;
    }

    public int getNrCorrectAnswers() {
        return nrCorrectAnswers;
    }

    public void setNrCorrectAnswers(int nrCorrectAnswers) {
        this.nrCorrectAnswers = nrCorrectAnswers;
    }

    public String getFinishTime() { return finishTime; }

    public void setFinishTime(String finishTime) { this.finishTime = finishTime; }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = DateHandler.toISOString(finishTime);
    }
}