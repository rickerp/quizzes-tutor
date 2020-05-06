package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.stats.ClarificationStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRequestRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.PublicClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_NOT_FOUND;


@Service
public class ClarificationRequestService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private ClarificationRequestRepository clarificationRequestRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private PublicClarificationRepository publicClarificationRepository;

    @Autowired
    private PublicClarificationService publicClarificationService;

    @PersistenceContext
    EntityManager entityManager;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto createClarificationRequest(int questionAnswerId, ClarificationRequestDto clarificationRequestDto) {

        if (clarificationRequestDto == null){
            throw new TutorException(ErrorMessage.CLARIFICATION_IS_EMPTY);
        }

        User user = userRepository.findById(clarificationRequestDto.getUser().getId())
                .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_INVALID_USER));

        QuestionAnswer questionAnswer = questionAnswerRepository.findById(questionAnswerId)
                .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_INVALID_QUESTION_ANSWER));

        if (!questionAnswer.getQuizAnswer().isCompleted())
            throw new TutorException(ErrorMessage.CLARIFICATION_QUIZ_NOT_COMPLETED);

        ClarificationRequest clarificationRequest = new ClarificationRequest(clarificationRequestDto, user, questionAnswer);

        if (!user.getQuizAnswers().contains(clarificationRequest.getQuestionAnswer().getQuizAnswer()))
            throw new TutorException(ErrorMessage.CLARIFICATION_QUESTION_ANSWER_NOT_IN_USER, clarificationRequest.getQuestionAnswer().getId());

        user.addClarification(clarificationRequest);
        questionAnswer.addClarification(clarificationRequest);

        this.entityManager.persist(clarificationRequest);
        return new ClarificationRequestDto(clarificationRequest);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<ClarificationRequestDto> getClarificationRequests(int userId, int executionId) {

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_INVALID_QUESTION_ANSWER));
        return user.getRole() == User.Role.STUDENT ? getClarificationsOfStudent(user, executionId) : getClarificationsOfTeacher(executionId);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto changeClarificationState(int clarificationId,String state) {
        ClarificationRequest clarificationRequest = clarificationRequestRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_NOT_FOUND));

        if (clarificationRequest.getType() == ClarificationRequest.Type.PUBLIC) {
            throw new TutorException(ErrorMessage.CLARIFICATION_CANNOT_CHANGE_STATE);
        }

        state = state.toUpperCase();
        switch (state) {
            case "UNRESOLVED":
                clarificationRequest.setState(ClarificationRequest.State.UNRESOLVED);
                break;
            case "RESOLVED":
                clarificationRequest.setState(ClarificationRequest.State.RESOLVED);
                break;
            default: throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_STATE);
        }

        entityManager.persist(clarificationRequest);
        return new ClarificationRequestDto(clarificationRequest);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationRequestDto changeClarificationType(int clarificationId, String type) {
        ClarificationRequest clarificationRequest = clarificationRequestRepository.findById(clarificationId)
                .orElseThrow(() -> new TutorException(ErrorMessage.CLARIFICATION_NOT_FOUND));

        CourseExecution courseExecution = clarificationRequest.getQuestionAnswer()
                .getQuizAnswer()
                .getQuiz()
                .getCourseExecution();

        Question question = clarificationRequest.getQuestionAnswer().getQuizQuestion().getQuestion();

        type = type.toUpperCase();
        switch (type) {
            case "PUBLIC":
                clarificationRequest.setType(ClarificationRequest.Type.PUBLIC);
                publicClarificationService.createPublicClarification(clarificationRequest, question, courseExecution);
                break;
            case "PRIVATE":
                PublicClarification publicClarification = clarificationRequest.getPublicClarification();
                clarificationRequest.setType(ClarificationRequest.Type.PRIVATE);
                publicClarificationService.removePublicClarification(publicClarification);
                break;
            default: throw new TutorException(ErrorMessage.CLARIFICATION_INVALID_TYPE);
        }
        entityManager.persist(clarificationRequest);
        return new ClarificationRequestDto(clarificationRequest);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ClarificationStatsDto getClarificationsStats(int userId, int executionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND));

        if (user.getRole() != User.Role.STUDENT)
            throw new TutorException(ErrorMessage.USER_NOT_STUDENT, user.getId());

        CourseExecution courseExec = courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<ClarificationRequest> execClarifications =  user.getClarificationRequests().stream()
                .filter(entry -> entry.getQuestionAnswer().getQuizAnswer().getQuiz().getCourseExecution().getId().equals(courseExec.getId()))
                .collect(Collectors.toList());

        int totalClarifications = execClarifications.size();

        int publicClarifications = (int)execClarifications.stream().
                filter(entry -> entry.getType() == ClarificationRequest.Type.PUBLIC)
                .count();

        float percentagePublicClr = totalClarifications > 0 ?
                (float)publicClarifications/totalClarifications * 100 : 0;

        ClarificationStatsDto statsDto = new ClarificationStatsDto();
        statsDto.setUsername(user.getUsername());
        statsDto.setName(user.getName());
        statsDto.setTotalClarificationRequests(totalClarifications);
        statsDto.setPublicClarificationRequests(publicClarifications);
        statsDto.setPercentageOfPublicClarifications(percentagePublicClr);

        return statsDto;
    }

    private List<ClarificationRequestDto> getClarificationsOfStudent(User user, int executionId) {
        return filterClarifications(user.getClarificationRequests().stream(), executionId);
    }

    private List<ClarificationRequestDto> getClarificationsOfTeacher(int executionId) {
        return filterClarifications(courseExecutionRepository.findById(executionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND))
                .getUsers().stream()
                .map(User::getClarificationRequests)
                .flatMap(Collection::stream), executionId);
    }

    private List<ClarificationRequestDto> filterClarifications(Stream<ClarificationRequest> clarificationRequests, int executionId) {
        return clarificationRequests.filter(clarification -> clarification.getQuestionAnswer().getQuizAnswer().getQuiz()
                .getCourseExecution().getId() == executionId)
                .map(ClarificationRequestDto::new)
                .sorted(Comparator.comparing(ClarificationRequestDto::getCreationDate).reversed())
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findClarificationCourseExecution(int clarificationRequestId) {
        return new CourseDto(clarificationRequestRepository.findById(clarificationRequestId)
                .map(ClarificationRequest::getQuestionAnswer)
                .map(QuestionAnswer::getQuizAnswer)
                .map(QuizAnswer::getQuiz)
                .map(Quiz::getCourseExecution)
                .orElseThrow(() -> new TutorException((ErrorMessage.CLARIFICATION_NOT_FOUND))));
    }
}
