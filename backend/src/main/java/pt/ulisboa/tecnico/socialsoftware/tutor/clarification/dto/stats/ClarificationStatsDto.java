package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.stats;

public class ClarificationStatsDto {
    private String username;
    private String name;
    private Integer totalClarificationRequests;
    private Integer publicClarificationRequests;
    private float percentageOfPublicClarifications;

    public ClarificationStatsDto(){}

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
