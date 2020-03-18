package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto;
import java.time.LocalDateTime;

public class ClarificationRequestDto {

    private int id;
    private ClarificationRequest.State state;
    private String content;
    private String username;
    private LocalDateTime creationDate;
    private QuestionAnswerDto questionAnswer;
    private ClarificationCommentDto clarificationComment;
    private ImageDto image;

    public ClarificationRequestDto() {}

    public ClarificationRequestDto(ClarificationRequest clarificationRequest) {

        this.id =  clarificationRequest.getId();
        this.state = clarificationRequest.getState();
        this.content = clarificationRequest.getContent();
        this.creationDate = clarificationRequest.getCreationDate();
        this.username = clarificationRequest.getUser().getUsername();
        this.questionAnswer = new QuestionAnswerDto(clarificationRequest.getQuestionAnswer());
        if (clarificationRequest.getClarificationComment() != null) {
            this.clarificationComment = new ClarificationCommentDto(clarificationRequest.getClarificationComment());
        }

        if (clarificationRequest.getImage() != null) {
            this.image = new ImageDto(clarificationRequest.getImage());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClarificationRequest.State getState() {
        return state;
    }

    public void setState(ClarificationRequest.State state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public ClarificationCommentDto getClarificationComment() {
        return clarificationComment;
    }

    public void setClarificationComment(ClarificationCommentDto clarificationComment) {
        this.clarificationComment = clarificationComment;
    }

    public QuestionAnswerDto getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswerDto questionAnswer) {
        this.questionAnswer = questionAnswer;
    }
}