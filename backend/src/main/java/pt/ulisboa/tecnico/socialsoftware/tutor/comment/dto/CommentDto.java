package pt.ulisboa.tecnico.socialsoftware.tutor.comment.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.comment.domain.Comment;

import java.time.LocalDateTime;

public class CommentDto {

    private int id;
    private String content;
    private String userName;
    private int clarificationId;
    private LocalDateTime creationDate;

    public CommentDto() {}

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userName = comment.getUser().getUsername();
        this.clarificationId = comment.getClarification().getId();
        this.creationDate = comment.getCreationDate();
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
