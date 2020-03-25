package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import javax.persistence.*;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_question_id")
    private StudentQuestion studentQuestion;

    private boolean accepted;
    private String justification;

    public Evaluation(){

    }

    public Evaluation(StudentQuestion studentQuestion, boolean accepted, String justification) {
        if(!accepted && (justification == null)){
            throw new TutorException(ErrorMessage.JUSTIFICATION_NOT_FOUND);
        }

        this.studentQuestion = studentQuestion;
        this.accepted = accepted;
        this.justification = justification;

        studentQuestion.setEvaluation(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StudentQuestion getStudentQuestion() {
        return studentQuestion;
    }

    public void setStudentQuestion(StudentQuestion studentQuestion) {
        this.studentQuestion = studentQuestion;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }
}
