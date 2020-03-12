package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament;
import java.io.Serializable;
import java.time.LocalDateTime;

public class TournamentDto implements Serializable {

    private int id;
    private Tournament.State state;
    private int creatorId;
    private int topicId;
    private Integer nrQuestions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.state = tournament.getState();
        this.creatorId = tournament.getCreator().getId();
        this.topicId = tournament.getTopic().getId();
    }

    public int getId() { return id; }

    public Tournament.State getState() { return state; }

    public int getCreatorId() { return creatorId; }

    public int setCreatorId(int creatorId) { return this.creatorId = creatorId; }

    public int getTopicId() { return topicId; }

    public void setTopicId(int topicId) { this.topicId = topicId; }

    public Integer getNrQuestions() { return nrQuestions; }

    public void setNrQuestions(Integer nrQuestions) { this.nrQuestions = nrQuestions; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
