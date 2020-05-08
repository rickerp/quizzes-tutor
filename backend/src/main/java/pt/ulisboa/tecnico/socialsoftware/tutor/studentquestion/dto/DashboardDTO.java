package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto;

import java.io.Serializable;

public class DashboardDTO implements Serializable {

    private String name;
    private int accepted;
    private int total;

    public DashboardDTO() {
        /* empty constructor */
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
