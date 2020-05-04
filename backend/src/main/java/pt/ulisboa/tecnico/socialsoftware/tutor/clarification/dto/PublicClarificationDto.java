package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;

public class PublicClarificationDto {
    public enum Availability {VISIBLE, NOT_VISIBLE}

    private int id;
    private ClarificationRequestDto clarificationRequestDto;
    private Availability availability;

    public PublicClarificationDto() {}

    public PublicClarificationDto(PublicClarification publicClarification, CourseExecution courseExecution) {
        setId(publicClarification.getId());
        if (publicClarification.getCourseExecutions().contains(courseExecution)) {
            setAvailability(Availability.VISIBLE);
        } else { setAvailability(Availability.NOT_VISIBLE); }

        setClarificationRequestDto(new ClarificationRequestDto(publicClarification.getClarificationRequest()));
    }

    public PublicClarificationDto(PublicClarification publicClarification) {
        setId(publicClarification.getId());
        setAvailability(Availability.VISIBLE);
        setClarificationRequestDto(new ClarificationRequestDto(publicClarification.getClarificationRequest()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClarificationRequestDto getClarificationRequestDto() {
        return clarificationRequestDto;
    }

    public void setClarificationRequestDto(ClarificationRequestDto clarificationRequestDto) {
        this.clarificationRequestDto = clarificationRequestDto;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }
}
