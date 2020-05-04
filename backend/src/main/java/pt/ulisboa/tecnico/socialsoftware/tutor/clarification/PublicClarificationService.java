package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.PublicClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class PublicClarificationService {

    @Autowired
    PublicClarificationRepository publicClarificationRepository;

    @PersistenceContext
    EntityManager entityManager;

    public void createPublicClarification(ClarificationRequest clarificationRequest, Question question, CourseExecution courseExecution) {
        PublicClarification publicClarification = new PublicClarification(clarificationRequest, question, courseExecution);
        clarificationRequest.setPublicClarification(publicClarification);
        this.entityManager.persist(publicClarification);
    }

    public void removePublicClarification(PublicClarification publicClarification) {
        publicClarification.remove();
        publicClarificationRepository.delete(publicClarification);
    }
}
