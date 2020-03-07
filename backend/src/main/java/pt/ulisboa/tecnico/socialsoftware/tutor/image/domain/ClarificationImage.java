package pt.ulisboa.tecnico.socialsoftware.tutor.image.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.image.dto.ImageDto;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "image_clarification")
public class ClarificationImage extends Image {
    @OneToOne
    @JoinColumn(name="clarification_id")
    private Clarification clarification;

    public ClarificationImage(){}

    public ClarificationImage(ImageDto imageDto) {
        super(imageDto);
    }

    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(Clarification clarification) {
        this.clarification = clarification;
    }
}

