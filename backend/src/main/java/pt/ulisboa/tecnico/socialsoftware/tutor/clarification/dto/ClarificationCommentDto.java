package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;


public class ClarificationCommentDto {

    private int id;
    private String content;
    private UserDto user;
    private String creationDate;
    private ClarificationRequestDto clarificationRequest;

    public ClarificationCommentDto() {}

    public ClarificationCommentDto(ClarificationComment clarificationComment) {
        this.setId(clarificationComment.getId());
        this.setContent(clarificationComment.getContent());
        this.setUser(new UserDto(clarificationComment.getUser()));

        if (clarificationComment.getCreationDate() != null) {
            this.setCreationDate(DateHandler.toISOString(clarificationComment.getCreationDate()));
        }
        this.clarificationRequest = new ClarificationRequestDto();
        setClarificationDto(clarificationComment.getClarificationRequest());

    }
    private void setClarificationDto(ClarificationRequest clarification){
        clarificationRequest.setId(clarification.getId());
        clarificationRequest.setState(clarification.getState());
        clarificationRequest.setType(clarification.getType());
        clarificationRequest.setContent(clarification.getContent());
        clarificationRequest.setUser(new UserDto(clarification.getUser()));
        clarificationRequest.setQuestionAnswer(new QuestionAnswerDto(clarification.getQuestionAnswer()));
        if (clarification.getCreationDate() != null) {
            clarificationRequest.setCreationDate(DateHandler.toISOString(clarification.getCreationDate()));
        }
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
