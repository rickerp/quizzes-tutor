package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.SQLException;

@Service
public class StudentQuestionService {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    StudentQuestionRepository studentQuestionRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> list(int courseId, int userId) {
        return studentQuestionRepository.find(userId).stream()
                .filter(s -> s.getQuestion().getCourse().getId() == courseId)
                .map(StudentQuestionDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto createStudentQuestion(int courseId, StudentQuestionDto studentQuestionDto) {

        if (studentQuestionDto == null)
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_IS_EMPTY);

        QuestionDto questionDto = studentQuestionDto.getQuestion();

        if (questionDto == null)
            throw new TutorException(ErrorMessage.QUESTION_IS_EMPTY);

        User user = userRepository.findById(studentQuestionDto.getStudent())
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));

        QuestionDto createdQuestionDto = questionService.createQuestion(courseId, questionDto);
        int questionId = createdQuestionDto.getId();
        questionService.questionSetStatus(questionId, Question.Status.PENDING);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));

        StudentQuestion studentQuestion = new StudentQuestion(user, question);
        this.entityManager.persist(studentQuestion);
        return new StudentQuestionDto(studentQuestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto findById(Integer studentQuestionId) {
        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_FOUND));
        return new StudentQuestionDto(studentQuestion);
    }


}
