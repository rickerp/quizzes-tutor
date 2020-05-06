package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.Evaluation;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.DashboardDTO;
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

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

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
    public List<StudentQuestionDto> listCourseStudentQuestions(int courseId) {
        return studentQuestionRepository.findAll().stream()
                .filter(s -> s.getQuestion().getCourse().getId() == courseId)
                .filter(s -> s.getQuestion().getStatus() == Question.Status.PENDING)
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
    public StudentQuestionDto editStudentQuestion(StudentQuestionDto studentQuestionDto) {
        if (studentQuestionDto == null)
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_IS_EMPTY);

        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionDto.getId())
                .orElseThrow(() -> new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_FOUND));

        QuestionDto questionDto = studentQuestionDto.getQuestion();

        if (questionDto == null)
            throw new TutorException(ErrorMessage.QUESTION_IS_EMPTY);

        Evaluation evaluation = studentQuestion.getEvaluation();
        if (evaluation == null || !evaluation.isAccepted())
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_ACCEPTED);

        QuestionDto updatedQuestion = questionService.updateQuestion(studentQuestion.getQuestion().getId(), questionDto);

        studentQuestionDto.setQuestion(updatedQuestion);
        return studentQuestionDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto reSubmitStudentQuestion(StudentQuestionDto studentQuestionDto) {
        if (studentQuestionDto == null)
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_IS_EMPTY);

        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionDto.getId())
                .orElseThrow(() -> new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_FOUND));

        QuestionDto questionDto = studentQuestionDto.getQuestion();

        if (questionDto == null)
            throw new TutorException(ErrorMessage.QUESTION_IS_EMPTY);

        Evaluation evaluation = studentQuestion.getEvaluation();
        if (evaluation == null || evaluation.isAccepted())
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_ACCEPTED);

        this.entityManager.remove(studentQuestion.getEvaluation());
        studentQuestion.setEvaluation(null);
        QuestionDto updatedQuestion = questionService.updateQuestion(studentQuestion.getQuestion().getId(), questionDto);

        studentQuestionDto.setQuestion(updatedQuestion);
        studentQuestionDto.setEvaluation(null);
        return studentQuestionDto;
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

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto publishStudentQuestion(Integer studentQuestionId) {
        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_FOUND));

        if (studentQuestion.getEvaluation() == null ||
            !studentQuestion.getEvaluation().isAccepted()) {
           throw new TutorException(ErrorMessage.EVALUATION_NOT_ACCEPTED);
        }

        if (studentQuestion.getQuestion().getStatus() != Question.Status.PENDING) {
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_ALREADY_ADDED);
        }

        studentQuestion.getQuestion().setStatus(Question.Status.AVAILABLE);
        return new StudentQuestionDto(studentQuestion);
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public DashboardDTO getPrivateDashboard(int courseId, int studentQuestionId) {
        var list = list(courseId, studentQuestionId);
        int total = list.size();
        int accepted = (int) list
                .stream()
                .filter(q -> q.getEvaluation() != null && q.getEvaluation().isAccepted())
                .count();
        var dashboardDto =  new DashboardDTO();
        dashboardDto.setAccepted(accepted);
        dashboardDto.setTotal(total);
        return dashboardDto;
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    public Boolean setDashboardVisibility(int userId, Boolean value) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));

        user.setPublicSuggestedQuestionsDashboard(value);
        userRepository.save(user);
        return value;
    }

    @Retryable(value = { SQLException.class }, backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<DashboardDTO> getDashboard(int courseId) {
        return courseExecutionRepository.findAll()
                .stream()
                .filter(c -> c.getCourse().getId() == courseId)
                .flatMap(c -> c.getUsers().stream())
                .filter(e -> e.isPublicSuggestedQuestionsDashboard() != null &&
                        e.isPublicSuggestedQuestionsDashboard() && e.getRole() == User.Role.STUDENT)
                .map(s -> {
                    var d = getPrivateDashboard(courseId, s.getId());
                    d.setName(s.getName());
                    return d;
                })
                .collect(Collectors.toList());
    }

}
