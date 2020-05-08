package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository;

import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import java.sql.SQLException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationCommentDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

@Service
public class ClarificationCommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClarificationRequestRepository clarificationRequestRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationCommentDto createClarificationComment(int clarificationRequestId, ClarificationCommentDto clarificationCommentDto) {
        if (clarificationCommentDto == null) {
            throw new TutorException(ErrorMessage.COMMENT_IS_EMPTY);
        }

        User user = userRepository.findById(clarificationCommentDto.getUser().getId())
                .orElseThrow(() -> new TutorException(ErrorMessage.COMMENT_INVALID_USER));
        if (user.getRole() != User.Role.TEACHER && user.getRole() != User.Role.STUDENT) {
            throw new TutorException(ErrorMessage.COMMENT_INVALID_USER);
        }

        ClarificationRequest clarificationRequest = getClarification(clarificationRequestId);
        verifyCourse(user, clarificationRequest);

        ClarificationComment clarificationComment = new ClarificationComment(clarificationCommentDto, user, clarificationRequest);
        user.addClarificationComment(clarificationComment);

        clarificationRequest.addClarificationComment(clarificationComment);
        setClarificationState(user, clarificationRequest);

        this.entityManager.persist(clarificationComment);
        return new ClarificationCommentDto(clarificationComment);
    }

    private void verifyCourse(User user, ClarificationRequest clarificationRequest) {
        CourseExecution clarificationCourseExecution = clarificationRequest.getQuestionAnswer().getCourseExecution();
        if (!user.getCourseExecutions().contains(clarificationCourseExecution)) {
            throw new TutorException(ErrorMessage.COMMENT_INVALID_USER_COURSE);
        }
    }

    private ClarificationRequest getClarification(int clarificationRequestId) {
        ClarificationRequest clarificationRequest = clarificationRequestRepository.findById(clarificationRequestId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COMMENT_INVALID_CLARIFICATION));

        if (clarificationRequest.getType() != ClarificationRequest.Type.PRIVATE){
            throw new TutorException(ErrorMessage.COMMENT_INVALID_CLARIFICATION_TYPE);
        }
        return clarificationRequest;
    }

    private void setClarificationState(User user, ClarificationRequest clarificationRequest) {
        if (user.getRole() == User.Role.STUDENT &&
                clarificationRequest.getState() == ClarificationRequest.State.RESOLVED) {
            clarificationRequest.setState(ClarificationRequest.State.UNRESOLVED);
        } else if (user.getRole() == User.Role.TEACHER
                && clarificationRequest.getState() == ClarificationRequest.State.UNRESOLVED) {
            clarificationRequest.setState(ClarificationRequest.State.RESOLVED);
        }
    }
}
