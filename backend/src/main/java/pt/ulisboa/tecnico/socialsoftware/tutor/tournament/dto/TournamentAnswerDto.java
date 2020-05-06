package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentAnswer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentAnswerDto implements Serializable {
    private TournamentDto tournament;
    private String finishTime;
    private Set<QuestionAnswerDto> questionAnswer;

    public TournamentAnswerDto() {}

    public TournamentAnswerDto(TournamentAnswer tournamentAnswer) {
        setTournament(new TournamentDto(tournamentAnswer.getTournament()));
        setFinishTime(tournamentAnswer.getFinishTime());
        setQuestionAnswer(tournamentAnswer.getQuestionAnswers().stream().map(QuestionAnswerDto::new).collect(Collectors.toSet()));
    }

    public TournamentDto getTournament() { return tournament; }

    public void setTournament(TournamentDto tournament) { this.tournament = tournament; }

    public String getFinishTime() { return finishTime; }

    public void setFinishTime(String finishTime) { this.finishTime = finishTime; }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = DateHandler.toISOString(finishTime);
    }

    public Set<QuestionAnswerDto> getQuestionAnswer() { return questionAnswer; }

    public void setQuestionAnswer(Set<QuestionAnswerDto> questionAnswer) {
        this.questionAnswer = questionAnswer;
    }
}