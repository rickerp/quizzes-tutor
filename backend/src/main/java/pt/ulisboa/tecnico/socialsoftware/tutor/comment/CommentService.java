package pt.ulisboa.tecnico.socialsoftware.tutor.comment;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;

import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import java.sql.SQLException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;

import pt.ulisboa.tecnico.socialsoftware.tutor.comment.domain.Comment;
import pt.ulisboa.tecnico.socialsoftware.tutor.comment.dto.CommentDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

@Service
public class CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClarificationRepository clarificationRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CommentDto createComment(CommentDto commentDto) {
        if (commentDto == null) {
            throw new TutorException(ErrorMessage.COMMENT_IS_EMPTY);
        }

        User user =  userRepository.findByUsername(commentDto.getUserName());
        if (user == null || user.getRole() != User.Role.TEACHER) {
            throw new TutorException(ErrorMessage.COMMENT_INVALID_USER);
        }

        Clarification clarification = clarificationRepository.findById(commentDto.getClarificationId())
                .orElseThrow(() -> new TutorException(ErrorMessage.COMMENT_INVALID_CLARIFICATION));

        if (clarification.getState() != Clarification.State.UNRESOLVED) {
            throw new TutorException(ErrorMessage.COMMENT_INVALID_CLARIFICATION_STATE);
        }

        CourseExecution clarificationCourseExecution = clarification.getQuestionAnswer().getQuizAnswer().getQuiz().getCourseExecution();
        if (!user.getCourseExecutions().contains(clarificationCourseExecution)) {
            throw new TutorException(ErrorMessage.COMMENT_INVALID_USER_COURSE);
        }

        Comment comment = new Comment(commentDto, user, clarification);

        user.addClarificationComment(comment);
        clarification.setComment(comment);

        this.entityManager.persist(comment);
        return new CommentDto(comment);
    }

}
