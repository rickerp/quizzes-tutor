package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

public class StudentQuestion {

    private User student;
    private Question question;

    public StudentQuestion(User user, Question question) {
        this.student = user;
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public User getStudent() {
        return student;
    }

}
