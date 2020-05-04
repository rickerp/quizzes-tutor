package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import java.time.format.DateTimeFormatter;

public class ClarificationRequestDto {

    private int id;
    private ClarificationRequest.State state;
    private ClarificationRequest.Type type;
    private String content;
    private String creationDate;
    private UserDto user;
    private QuestionAnswerDto questionAnswer;
    private ClarificationCommentDto clarificationComment;
    private ImageDto image;
    public ClarificationRequestDto() {
    }

    public ClarificationRequestDto(ClarificationRequest clarificationRequest) {
        this.setId(clarificationRequest.getId());
        this.setState(clarificationRequest.getState());
        this.setType(clarificationRequest.getType());
        this.setContent(clarificationRequest.getContent());

        if (clarificationRequest.getCreationDate() != null) {
            this.setCreationDate(DateHandler.toISOString(clarificationRequest.getCreationDate()));
        }
        this.setUser(new UserDto(clarificationRequest.getUser()));
        this.setQuestionAnswer(new QuestionAnswerDto(clarificationRequest.getQuestionAnswer()));
        if (clarificationRequest.getClarificationComment() != null) {
            setClarificationComment(new ClarificationCommentDto(clarificationRequest.getClarificationComment()));
        }

        if (clarificationRequest.getImage() != null) { this.setImage(new ImageDto(clarificationRequest.getImage())); }
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

    public ClarificationRequest.Type getType() {
        return type;
    }

    public void setType(ClarificationRequest.Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public UserDto getUser() { return user; }

    public void setUser(UserDto user) { this.user = user; }

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