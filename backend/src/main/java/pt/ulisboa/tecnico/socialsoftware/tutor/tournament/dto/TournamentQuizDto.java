package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentAnswer;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentQuizDto implements Serializable {

    private List<StatementQuestionDto> questions;
    private List<StatementAnswerDto> answers;
    private Long timeToEnd;

    public TournamentQuizDto(TournamentAnswer answer) {

        questions = answer.getQuestionAnswers().stream()
                .map(StatementQuestionDto::new)
                .sorted(Comparator.comparing(StatementQuestionDto::getSequence))
                .collect(Collectors.toList());

        answers = answer.getQuestionAnswers().stream()
                .map(StatementAnswerDto::new)
                .sorted(Comparator.comparing(StatementAnswerDto::getSequence))
                .collect(Collectors.toList());

        this.timeToEnd = ChronoUnit.MILLIS.between(DateHandler.now(), answer.getTournament().getEndTime());
    }

    public Long getTimeToEnd() { return timeToEnd; }

    public void setTimeToEnd(Long timeToEnd) { this.timeToEnd = timeToEnd; }

    public List<StatementQuestionDto> getQuestions() { return questions; }

    public void setQuestions(List<StatementQuestionDto> questions) { this.questions = questions; }

    public List<StatementAnswerDto> getAnswers() { return answers; }

    public void setAnswers(List<StatementAnswerDto> answers) { this.answers = answers; }
}
