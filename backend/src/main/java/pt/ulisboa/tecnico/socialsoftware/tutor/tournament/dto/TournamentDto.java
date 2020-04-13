package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {

    private int id;
    private String name;
    private int creatorId;
    private String creatorName;
    private int courseExecutionId;
    private Set<Integer> topicsId;
    private Set<String> topicsName;
    private Set<Integer> playersId;
    private Integer nrQuestions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {
        this.id = tournament.getId();
        this.name = tournament.getName();
        this.creatorId = tournament.getCreator().getId();
        this.creatorName = tournament.getCreator().getUsername();
        this.topicsId = tournament.getTopics().stream().map(Topic::getId).collect(Collectors.toSet());
        this.topicsName = tournament.getTopics().stream().map(Topic::getName).collect(Collectors.toSet());
        this.playersId = tournament.getPlayers().stream().map(User::getId).collect(Collectors.toSet());
        this.courseExecutionId = tournament.getCourseExecution().getId();
        this.nrQuestions = tournament.getNrQuestions();
        this.startTime = tournament.getStartTime();
        this.endTime = tournament.getEndTime();
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getCreatorId() { return creatorId; }

    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public String getCreatorName() { return creatorName; }

    public Set<Integer> getTopicsId() { return topicsId; }

    public void setTopicsId(Set<Integer> topicsId) {
        this.topicsId = topicsId;
    }

    public Set<String> getTopicsName() { return topicsName; }

    public Set<Integer> getPlayersId() { return playersId; }

    public void setPlayersId(Set<Integer> playersId) { this.playersId = playersId; }

    public int getCourseExecutionId() { return courseExecutionId; }

    public void setCourseExecutionId(int courseExecutionId) { this.courseExecutionId = courseExecutionId; }

    public Integer getNrQuestions() { return nrQuestions; }

    public void setNrQuestions(Integer nrQuestions) { this.nrQuestions = nrQuestions; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
