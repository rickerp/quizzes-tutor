package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import java.io.Serializable;

public class CorrectAnswerDto implements Serializable {
    private Integer correctOptionId;
    private Integer sequence;

    public CorrectAnswerDto(QuestionAnswer questionAnswer) {

        this.sequence = questionAnswer.getSequence();

        Question question = questionAnswer.getQuestion();

        this.correctOptionId = question.getCorrectOptionId();
    }

    public Integer getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Integer correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "CorrectAnswerDto{" +
                "correctOptionId=" + correctOptionId +
                ", sequence=" + sequence +
                '}';
    }
}