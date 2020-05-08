package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable {

    private int id;
    private String name;
    private int creatorId;
    private String creatorName;
    private int courseExecutionId;
    private Set<Integer> topicsId;
    private List<String> topicsName;
    private Set<Integer> playersId;
    private Integer nrQuestions;
    private String startTime;
    private String endTime;

    public TournamentDto() {}

    public TournamentDto(Tournament tournament) {

        setId(tournament.getId());
        setName(tournament.getName());
        setCreatorId(tournament.getCreator().getId());
        setCreatorName(tournament.getCreator().getName());
        setCourseExecutionId(tournament.getCourseExecution().getId());
        setNrQuestions(tournament.getNrQuestions());
        this.topicsId = tournament.getTopics().stream().map(Topic::getId).collect(Collectors.toSet());
        this.topicsName = tournament.getTopics().stream().map(Topic::getName).sorted().collect(Collectors.toList());
        this.playersId = tournament.getPlayers().stream().map(User::getId).collect(Collectors.toSet());

        if (tournament.getStartTime() != null) {
            this.startTime = DateHandler.toISOString(tournament.getStartTime());
        }
        if (tournament.getEndTime() != null) {
            this.endTime = DateHandler.toISOString(tournament.getEndTime());
        }
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getCreatorId() { return creatorId; }

    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }

    public String getCreatorName() { return creatorName; }

    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }

    public Set<Integer> getTopicsId() { return topicsId; }

    public void setTopicsId(Set<Integer> topicsId) {
        this.topicsId = topicsId;
    }

    public List<String> getTopicsName() { return topicsName; }

    public Set<Integer> getPlayersId() { return playersId; }

    public void setPlayersId(Set<Integer> playersId) { this.playersId = playersId; }

    public int getCourseExecutionId() { return courseExecutionId; }

    public void setCourseExecutionId(int courseExecutionId) { this.courseExecutionId = courseExecutionId; }

    public Integer getNrQuestions() { return nrQuestions; }

    public void setNrQuestions(Integer nrQuestions) { this.nrQuestions = nrQuestions; }

    public String getStartTime() { return startTime; }

    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }

    public void setEndTime(String endTime) { this.endTime = endTime; }
}
