package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment;

import java.time.LocalDateTime;

public class ClarificationCommentDto {

    private int id;
    private String content;
    private String userName;
    private int clarificationId;
    private LocalDateTime creationDate;

    public ClarificationCommentDto() {}

    public ClarificationCommentDto(ClarificationComment clarificationComment) {
        this.id = clarificationComment.getId();
        this.content = clarificationComment.getContent();
        this.userName = clarificationComment.getUser().getUsername();
        this.clarificationId = clarificationComment.getClarificationRequest().getId();
        this.creationDate = clarificationComment.getCreationDate();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getClarificationId() {
        return clarificationId;
    }

    public void setClarificationId(int clarificationId) {
        this.clarificationId = clarificationId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
