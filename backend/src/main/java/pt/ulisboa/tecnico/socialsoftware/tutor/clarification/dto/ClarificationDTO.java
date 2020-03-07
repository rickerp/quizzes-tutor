package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto;
import java.time.LocalDateTime;

public class ClarificationDTO {

    private int id;
    private Clarification.State state;
    private String content;
    private String userName;
    private int questionAnswerId;
    private LocalDateTime creationDate;
    private ImageDto image;

    public ClarificationDTO() {}

    public ClarificationDTO(Clarification clarification) {
        this.id =  clarification.getId();
        this.state = clarification.getState();
        this.content = clarification.getContent();
        this.questionAnswerId = clarification.getQuestionAnswer().getId();
        this.creationDate = clarification.getCreationDate();
        this.userName = clarification.getUser().getUsername();

        if (clarification.getClarificationImage() != null) {
            this.image = new ImageDto(clarification.getClarificationImage());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Clarification.State getState() {
        return state;
    }

    public void setState(Clarification.State state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(int questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }
}