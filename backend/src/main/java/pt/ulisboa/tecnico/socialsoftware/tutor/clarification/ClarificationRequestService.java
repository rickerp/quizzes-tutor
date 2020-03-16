package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;




@Service
public class ClarificationRequestService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private ClarificationRequestRepository clarificationRequestRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto createClarificationRequest(int questionAnswerId, ClarificationRequestDto clarificationsDto) {
        if (clarificationsDto == null){
            throw new TutorException(ErrorMessage.CLARIFICATION_IS_EMPTY);
        }

        User user = getUser(clarificationsDto.getUserName());

        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId)
                .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_INVALID_QUESTION_ANSWER));

        if (!questionAnswer.getQuizAnswer().isCompleted())
            throw new TutorException(ErrorMessage.CLARIFICATION_QUIZ_NOT_COMPLETED);

        ClarificationRequest clarificationRequest = new ClarificationRequest(clarificationsDto, user, questionAnswer);

        if (!user.getQuizAnswers().contains(clarificationRequest.getQuestionAnswer().getQuizAnswer()))
            throw new TutorException(ErrorMessage.CLARIFICATION_QUESTION_ANSWER_NOT_IN_USER, clarificationRequest.getQuestionAnswer().getId());
        user.addClarification(clarificationRequest);
        questionAnswer.addClarification(clarificationRequest);

        this.entityManager.persist(clarificationRequest);
        return new ClarificationRequestDto(clarificationRequest);
    }

    private User getUser(String username) {

        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_USER);
        return user;
    }

    public List<ClarificationRequestDto> getClarificationRequests(String username, int executionId) {
        return null;
    }

    public ClarificationRequestDto getClarificationRequest(int clarificationRequestId) {
        return null;
    }

}

