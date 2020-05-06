package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.time.format.DateTimeFormatter;

public class ClarificationCommentDto {

    private int id;
    private String content;
    private UserDto user;
    private String creationDate;
    private ClarificationRequestDto clarificationRequest;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ClarificationCommentDto() {}

    public ClarificationCommentDto(ClarificationComment clarificationComment) {
        this.id = clarificationComment.getId();
        this.content = clarificationComment.getContent();
        this.user = new UserDto(clarificationComment.getUser());
        this.creationDate = clarificationComment.getCreationDate().format(formatter);
        this.clarificationRequest = new ClarificationRequestDto();
        setClarificationDto(clarificationComment.getClarificationRequest());

    }
    private void setClarificationDto(ClarificationRequest clarification){
        clarificationRequest.setId(clarification.getId());
        clarificationRequest.setState(clarification.getState());
        clarificationRequest.setContent(clarification.getContent());
        clarificationRequest.setUser(new UserDto(clarification.getUser()));
        if (clarification.getCreationDate() != null) {
            clarificationRequest.setCreationDate(clarification.getCreationDate().format(formatter));
        }
        clarificationRequest.setQuestionAnswer(new QuestionAnswerDto(clarification.getQuestionAnswer()));
        if (clarification.getImage() != null) {
            clarificationRequest.setImage(new ImageDto(clarification.getImage()));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public ClarificationRequestDto getClarificationRequest() {
        return clarificationRequest;
    }

    public void setClarificationRequest(ClarificationRequestDto clarificationRequest) {
        this.clarificationRequest = clarificationRequest;
    }
}
