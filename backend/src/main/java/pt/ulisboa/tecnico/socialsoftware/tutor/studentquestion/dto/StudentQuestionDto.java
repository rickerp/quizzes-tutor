package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion;

import java.io.Serializable;

public class StudentQuestionDto implements Serializable {

    private Integer id;
    private QuestionDto question;
    private EvaluationDto evaluation;
    private int student;

    public StudentQuestionDto() {

    }
    public StudentQuestionDto(StudentQuestion studentQuestion) {
        this.id = studentQuestion.getId();

        if (studentQuestion.getEvaluation() != null)
            this.evaluation = new EvaluationDto(studentQuestion.getEvaluation());

        this.question = new QuestionDto(studentQuestion.getQuestion());
        this.student = studentQuestion.getStudent().getId();
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

    public int getStudent() {
        return student;
    }

    public void setStudent(int student) {
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

    public EvaluationDto getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDto evaluation) {
        this.evaluation = evaluation;
    }
}
