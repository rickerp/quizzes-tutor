package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Clarification_requests")
public class ClarificationRequest {

    public enum State {UNRESOLVED, RESOLVED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ClarificationRequest.State state;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    private QuestionAnswer questionAnswer;

    private LocalDateTime creationDate;

    @OneToOne(cascade=CascadeType.ALL)
    private Image image;

    @OneToOne(cascade=CascadeType.ALL, mappedBy = "clarificationRequest")
    private ClarificationComment clarificationComment;

    public ClarificationRequest() {}

    public ClarificationRequest(ClarificationRequestDto clarificationRequestDto, User user, QuestionAnswer questionAnswer) {

        setContent(clarificationRequestDto.getContent());

        if (clarificationRequestDto.getState() != State.UNRESOLVED)
            throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_STATE);

        setState(State.UNRESOLVED);
        setUser(user);
        setQuestionAnswer(questionAnswer);

        if (clarificationRequestDto.getImage() != null) setImage(new Image(clarificationRequestDto.getImage()));

        if (clarificationRequestDto.getCreationDate() == null) { setCreationDate(DateHandler.now()); }
        else { setCreationDate(DateHandler.toLocalDateTime(clarificationRequestDto.getCreationDate())); }
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
        if (content == null) throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_CONTENT);
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

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) { this.image = image; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ClarificationComment getClarificationComment() {
        return clarificationComment;
    }

    public void setClarificationComment(ClarificationComment clarificationComment) {
        this.clarificationComment = clarificationComment;
    }
}
