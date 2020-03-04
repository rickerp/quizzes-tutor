package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.io.Serializable;

public class StudentQuestionDto implements Serializable {

    private Integer id;
    private QuestionDto question;
    private StudentDto student;

    public StudentQuestionDto(StudentQuestion studentQuestion) {
        this.question = new QuestionDto(studentQuestion.getQuestion());
        this.student = new StudentDto(studentQuestion.getStudent());
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

    public StudentDto getStudent() {
        return student;
    }

    public void setStudent(StudentDto user) {
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
