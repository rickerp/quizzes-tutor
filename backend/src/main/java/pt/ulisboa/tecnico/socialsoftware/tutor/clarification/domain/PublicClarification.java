package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "public_clarifications")
public class PublicClarification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private ClarificationRequest clarificationRequest;

    @ManyToMany()
    private Set<CourseExecution> courseExecutions = new HashSet<>();

    @ManyToOne()
    private Question question;

    public PublicClarification() {}

    public PublicClarification(ClarificationRequest clarificationRequest, Question question, CourseExecution courseExecution) {
        this.setClarificationRequest(clarificationRequest);
        this.setQuestion(question);
        this.clarificationRequest.setPublicClarification(this);
        this.addCourseExecution(courseExecution);
        courseExecution.addPublicClarification(this);
    }

    public Integer getId() {
        return id;
    }

    public void addCourseExecution(CourseExecution courseExecution) {
        if (this.courseExecutions.contains(courseExecution)) {
            throw new TutorException(ErrorMessage.EXECUTION_ALREADY_VISIBLE);
        }
        this.courseExecutions.add(courseExecution);
        courseExecution.addPublicClarification(this);
    }

    public void removeCourseExecution(CourseExecution courseExecution) {
        if (!this.courseExecutions.contains(courseExecution)) {
            throw new TutorException(ErrorMessage.EXECUTION_ALREADY_INVISIBLE);
        }
        this.courseExecutions.remove(courseExecution);
        courseExecution.removePublicClarification(this);
    }

    public Set<CourseExecution> getCourseExecutions() {
        return courseExecutions;
    }

    public void setCourseExecutions(Set<CourseExecution> courseExecutions) {
        this.courseExecutions = courseExecutions;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
        question.addPublicClarification(this);
    }

    public ClarificationRequest getClarificationRequest() {
        return this.clarificationRequest;
    }

    public void remove() {
        for(CourseExecution cExec : courseExecutions) {
            cExec.getPublicClarifications().remove(this);
        }
        this.question.getPublicClarifications().remove(this);
    }

    public void setClarificationRequest(ClarificationRequest clarificationRequest) {
        if(clarificationRequest.getType() != ClarificationRequest.Type.PUBLIC)
            throw new TutorException(ErrorMessage.PUBLIC_CLARIFICATION_INVALID_CLARIFICATION);
        this.clarificationRequest = clarificationRequest;
    }

}
