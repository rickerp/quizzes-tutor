package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.Tournament;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {

    private int id;
    private int creatorId;
    private Set<Integer> topicsId;
    private Integer nrQuestions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.creatorId = tournament.getCreator().getId();
        this.topicsId = tournament.getTopics().stream().map(Topic::getId).collect(Collectors.toSet());
        this.nrQuestions = tournament.getNrQuestions();
        this.startTime = tournament.getStartTime();
        this.endTime = tournament.getEndTime();
    }

    public int getId() { return id; }

    public int getCreatorId() { return creatorId; }

    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public Set<Integer> getTopicsId() { return topicsId; }

    public void setTopicsId(Set<Integer> topicsId) {
        this.topicsId = topicsId;
    }

    public Integer getNrQuestions() { return nrQuestions; }

    public void setNrQuestions(Integer nrQuestions) { this.nrQuestions = nrQuestions; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
