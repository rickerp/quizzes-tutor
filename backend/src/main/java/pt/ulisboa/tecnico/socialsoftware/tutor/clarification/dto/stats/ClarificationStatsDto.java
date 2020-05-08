package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.stats;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public class ClarificationStatsDto {
    private String username;
    private String name;
    private User.DashBoardState dashboardState;
    private Integer totalClarificationRequests;
    private Integer publicClarificationRequests;
    private float percentageOfPublicClarifications;
    
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

    public User.DashBoardState getDashboardState() {
        return dashboardState;
    }

    public void setDashboardState(User.DashBoardState dashboardState) {
        this.dashboardState = dashboardState;
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
