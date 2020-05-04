package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.PublicClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.PublicClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.QuestionController;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicClarificationService {

    @Autowired
    PublicClarificationRepository publicClarificationRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @Autowired
    QuestionRepository questionRepository;

    public void createPublicClarification(ClarificationRequest clarificationRequest, Question question, CourseExecution courseExecution) {
        PublicClarification publicClarification = new PublicClarification(clarificationRequest, question, courseExecution);
        clarificationRequest.setPublicClarification(publicClarification);
        this.entityManager.persist(publicClarification);
    }

    public void removePublicClarification(PublicClarification publicClarification) {
        publicClarification.remove();
        publicClarificationRepository.delete(publicClarification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<PublicClarificationDto> listAllClarifications(User user, int courseExecutionId, int questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND, questionId));
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        if (user.getRole() == User.Role.STUDENT) { return listAllClarificationsStudent(courseExecution, question); }
        else { return listAllClarificationsTeacher(courseExecution, question); }
    }

    private List<PublicClarificationDto> listAllClarificationsStudent(CourseExecution courseExecution, Question question) {
        return question.getPublicClarifications().stream()
                .filter(clr -> clr.getCourseExecutions().contains(courseExecution))
                .map(PublicClarificationDto::new)
                .sorted(Comparator.comparing((PublicClarificationDto p1) -> p1.getClarificationRequestDto()
                .getCreationDate())
                .reversed())
                .collect(Collectors.toList());
    }

    private List<PublicClarificationDto> listAllClarificationsTeacher(CourseExecution courseExecution, Question question) {
        return  question.getPublicClarifications().stream()
                .map(p1 -> new PublicClarificationDto(p1, courseExecution))
                .sorted(Comparator.comparing((PublicClarificationDto p1) -> p1.getClarificationRequestDto()
                .getCreationDate())
                .reversed())
                .collect(Collectors.toList());
    }
}

