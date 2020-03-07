package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLException;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDTO;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;




@Service
public class ClarificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void createClarification(ClarificationDTO clarificationsDto) {
        User user = getUser(clarificationsDto.getUserName());
        System.out.println(clarificationsDto.getQuestionAnswerId());
        System.out.flush();

        QuestionAnswer questionAnswer = questionAnswerRepository.findById(
                clarificationsDto.getQuestionAnswerId()).orElseThrow(() ->
                new TutorException(ErrorMessage.CLARIFICATION_INVALID_QUESTION_ANSWER));

        if (!questionAnswer.getQuizAnswer().isCompleted())
            throw new TutorException(ErrorMessage.CLARIFICATION_QUIZ_NOT_COMPLETED);

        Clarification clarification = new Clarification(clarificationsDto, user, questionAnswer);

        user.addClarification(clarification);

        if(questionAnswer.getClarification() != null)
            throw new TutorException(ErrorMessage.CLARIFICATION_QUESTION_ANSWER_HAS_CLARIFICATION);
        questionAnswer.setClarification(clarification);

        Question question = questionAnswer.getQuizQuestion().getQuestion();
        question.addClarification(clarification);

        this.entityManager.persist(clarification);
    }

    private User getUser(String username) {

        User user = userRepository.findByUsername(username);
        System.out.println(user);
        if (user == null)
            throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_USER);
        return user;
    }


}

