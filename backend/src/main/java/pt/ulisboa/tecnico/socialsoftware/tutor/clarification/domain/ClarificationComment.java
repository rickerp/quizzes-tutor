package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationCommentDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Clarification_comments")
public class ClarificationComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "clarification_request_id")
    private ClarificationRequest clarificationRequest;

    private LocalDateTime creationDate;

    public ClarificationComment() {}

    public ClarificationComment(ClarificationCommentDto clarificationCommentDto, User user, ClarificationRequest clarificationRequest) {

        this.setContent(clarificationCommentDto.getContent());
        this.setUser(user);
        this.setClarificationRequest(clarificationRequest);
        if (clarificationCommentDto.getCreationDate() == null) { setCreationDate(DateHandler.now()); }
        else { setCreationDate(DateHandler.toLocalDateTime(clarificationCommentDto.getCreationDate())); }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content == null) {
            throw new TutorException(ErrorMessage.COMMENT_INVALID_CONTENT);
        }
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClarificationRequest getClarificationRequest() {
        return clarificationRequest;
    }

    public void setClarificationRequest(ClarificationRequest clarificationRequest) {
        this.clarificationRequest = clarificationRequest;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}
