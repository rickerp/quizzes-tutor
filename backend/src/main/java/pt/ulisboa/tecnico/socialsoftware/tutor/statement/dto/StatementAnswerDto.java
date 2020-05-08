package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatementAnswerDto implements Serializable {
    private Integer questionAnswerId;
    private Integer timeTaken;
    private Integer sequence;
    private Integer optionId;
    private List<ClarificationRequestDto> clarificationRequests;

    public StatementAnswerDto() {}

    public StatementAnswerDto(QuestionAnswer questionAnswer) {

        this.timeTaken = questionAnswer.getTimeTaken();
        this.sequence = questionAnswer.getSequence();
        this.questionAnswerId = questionAnswer.getId();

        if (questionAnswer.getOption() != null) {
            this.optionId = questionAnswer.getOption().getId();
        }

        this.clarificationRequests = questionAnswer.getClarificationRequests().stream()
                .map(this::createClarificationDto)
                .sorted(Comparator.comparing(ClarificationRequestDto::getCreationDate).reversed())
                .collect(Collectors.toList());
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public List<ClarificationRequestDto> getClarificationRequests() {
        return clarificationRequests;
    }

    public void setClarificationRequests(List<ClarificationRequestDto> clarificationRequests) {
        this.clarificationRequests = clarificationRequests;
    }

    private ClarificationRequestDto createClarificationDto(ClarificationRequest clarification) {
        ClarificationRequestDto clarificationDto = new ClarificationRequestDto(clarification);
        clarificationDto.setQuestionAnswer(null);
        return clarificationDto;
    }

    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                ", optionId=" + optionId +
                ", timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                '}';
    }
}
