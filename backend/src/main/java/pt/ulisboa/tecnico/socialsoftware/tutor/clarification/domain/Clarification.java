package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDTO;
import pt.ulisboa.tecnico.socialsoftware.tutor.comment.domain.Comment;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.domain.ClarificationImage;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Clarifications")
public class Clarification {

    public enum State {UNRESOLVED, RESOLVED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Clarification.State state;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(cascade = CascadeType.ALL)
    private QuestionAnswer questionAnswer;

    private LocalDateTime creationDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarification")
    private ClarificationImage clarificationImage;

    @OneToOne(mappedBy = "clarification")
    private Comment comment;

    public Clarification() {}

    public Clarification(ClarificationDTO clarificationsDto, User user, QuestionAnswer questionAnswer) {

        if (clarificationsDto.getContent() == null) throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_CONTENT);
        this.content = clarificationsDto.getContent();

        if (!(clarificationsDto.getState() == State.UNRESOLVED))
            throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_STATE);

        this.state = State.UNRESOLVED;
        this.user = user;
        this.questionAnswer = questionAnswer;

        if (clarificationsDto.getImage() != null) {
            ClarificationImage img = new ClarificationImage(clarificationsDto.getImage());
            this.clarificationImage = img;
            img.setClarification(this);
        }

        this.creationDate = clarificationsDto.getCreationDate() == null ? LocalDateTime.now() : clarificationsDto.getCreationDate();

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public QuestionAnswer getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public ClarificationImage getClarificationImage() {
        return clarificationImage;
    }

    public void setClarificationImage(ClarificationImage clarificationImage) { this.clarificationImage = clarificationImage; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
