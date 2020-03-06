package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import javax.persistence.*;

@Entity
@Table(name = "image_question")
public class QuestionImage extends Image {

    @OneToOne
    @JoinColumn(name="question_id")
    private Question question;

    public QuestionImage() {}

    public QuestionImage(ImageDto imageDto) {
        super(imageDto);
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;

    }
}