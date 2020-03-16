package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion;

import java.io.Serializable;

public class StudentQuestionDto implements Serializable {

    private Integer id;
    private QuestionDto question;
    private String student;

    public StudentQuestionDto() {

    }
    public StudentQuestionDto(StudentQuestion studentQuestion) {
        this.id = studentQuestion.getId();
        this.question = new QuestionDto(studentQuestion.getQuestion());
        this.student = studentQuestion.getStudent().getUsername();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public QuestionDto getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDto question) {
        this.question = question;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "StudentQuestionDto{" +
                "id=" + id +
                ", student=" + student +
                ", question=" + question +
                '}';
    }

}
