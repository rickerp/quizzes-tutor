package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDTO;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_Answer_Id")
    private QuestionAnswer questionAnswer;

    private LocalDateTime creationDate;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarification")
    private ClarificationImage clarificationImage;

    public Clarification() {}

    public Clarification(ClarificationDTO clarificationDto, User user, QuestionAnswer questionAnswer) {
        this.content = clarificationDto.getContent();
        this.state = clarificationDto.getState();
        this.user = user;
        this.questionAnswer = questionAnswer;

        if (clarificationDto.getImage() != null) {
            ClarificationImage img = new ClarificationImage(clarificationDto.getImage());
            this.clarificationImage = img;
            img.setClarification(this);
        }

        if (clarificationDto.getCreationDate() == null)
            this.creationDate = LocalDateTime.now();
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

    public void setClarificationImage(ClarificationImage clarificationImage) {
        this.clarificationImage = clarificationImage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
