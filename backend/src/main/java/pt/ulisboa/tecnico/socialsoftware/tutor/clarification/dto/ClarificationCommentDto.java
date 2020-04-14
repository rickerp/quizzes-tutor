package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.time.LocalDateTime;

public class ClarificationCommentDto {

    private int id;
    private String content;
    private UserDto user;
    private LocalDateTime creationDate;

    public ClarificationCommentDto() {}

    public ClarificationCommentDto(ClarificationComment clarificationComment) {
        this.id = clarificationComment.getId();
        this.content = clarificationComment.getContent();
        this.user = new UserDto(clarificationComment.getUser());
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

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
