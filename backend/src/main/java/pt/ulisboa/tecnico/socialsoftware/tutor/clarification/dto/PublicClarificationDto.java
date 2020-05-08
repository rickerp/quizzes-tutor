package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;

public class PublicClarificationDto {
    public enum Availability {VISIBLE, INVISIBLE}

    private int id;
    private ClarificationRequestDto clarificationRequest;
    private Availability availability;

    public PublicClarificationDto(PublicClarification publicClarification, CourseExecution courseExecution) {
        setId(publicClarification.getId());
        if (publicClarification.getCourseExecutions().contains(courseExecution)) {
            setAvailability(Availability.VISIBLE);
        } else { setAvailability(Availability.INVISIBLE); }

        setClarificationRequest(new ClarificationRequestDto(publicClarification.getClarificationRequest()));
    }

    public PublicClarificationDto(PublicClarification publicClarification) {
        setId(publicClarification.getId());
        setAvailability(Availability.VISIBLE);
        setClarificationRequest(new ClarificationRequestDto(publicClarification.getClarificationRequest()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ClarificationRequestDto getClarificationRequest() {
        return clarificationRequest;
    }

    public void setClarificationRequest(ClarificationRequestDto clarificationRequestDto) {
        this.clarificationRequest = clarificationRequestDto;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(Availability availability) {
        this.availability = availability;
    }
}
