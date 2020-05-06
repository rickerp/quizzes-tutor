package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.stats;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public class ClarificationStatsDto {
    private String username;
    private String name;
    private Integer totalClarificationRequests;
    private Integer publicClarificationRequests;
    private float percentageOfPublicClarifications;

    public ClarificationStatsDto(User user){
        this.username = user.getUsername();
        this.name = user.getName();
        this.totalClarificationRequests = user.getClarificationRequests().size();
        this.publicClarificationRequests = (int) user.getClarificationRequests().stream()
                .filter(entry -> entry.getType() == ClarificationRequest.Type.PUBLIC)
                .count();
        this.percentageOfPublicClarifications = this.totalClarificationRequests > 0 ?
                (float)this.publicClarificationRequests / this.totalClarificationRequests * 100 : 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalClarificationRequests() {
        return totalClarificationRequests;
    }

    public void setTotalClarificationRequests(Integer totalClarificationRequests) {
        this.totalClarificationRequests = totalClarificationRequests;
    }

    public Integer getPublicClarificationRequests() {
        return publicClarificationRequests;
    }

    public void setPublicClarificationRequests(Integer publicClarificationRequests) {
        this.publicClarificationRequests = publicClarificationRequests;
    }

    public float getPercentageOfPublicClarifications() {
        return percentageOfPublicClarifications;
    }

    public void setPercentageOfPublicClarifications(float percentageOfPublicClarifications) {
        this.percentageOfPublicClarifications = percentageOfPublicClarifications;
    }
}
