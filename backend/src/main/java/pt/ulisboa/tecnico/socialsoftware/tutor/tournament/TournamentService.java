package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;


@Service
public class TournamentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto dto) {
        if (dto == null) throw new TutorException(ErrorMessage.INVALID_DTO);

        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, dto.getCreatorId()));

        Set<Topic> topics = dto.getTopicsId().stream()
                .map(topicId -> topicRepository.findById(topicId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOPIC_NOT_FOUND, topicId)))
                .collect(Collectors.toSet());

        CourseExecution courseExecution = courseExecutionRepository.findById(dto.getCourseExecutionId())
                .orElseThrow(() -> new TutorException(ErrorMessage.COURSE_EXECUTION_NOT_FOUND, dto.getCourseExecutionId()));

        LocalDateTime startTime = DateHandler.toLocalDateTime(dto.getStartTime());
        LocalDateTime endTime = DateHandler.toLocalDateTime(dto.getEndTime());

        Tournament tournament = new Tournament(dto.getName(), creator, topics, courseExecution, dto.getNrQuestions(), startTime, endTime);
        tournamentRepository.save(tournament);
        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enroll(int playerId, int tournamentId) {

        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new TutorException(ErrorMessage.USER_NOT_FOUND, playerId));
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));

        tournament.enroll(player);
        if (tournament.getTournamentAnswers().size() > 1) { tournament.createQuiz(); }
        return new TournamentDto(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getExecutionOpenedTournaments(int executionId) {

        return tournamentRepository.findOpenedTournaments(DateHandler.now(), executionId).stream()
                .map(TournamentDto::new)
                .sorted(Comparator.comparing(TournamentDto::getStartTime))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getExecutionInProgressNonStartedTournaments(int executionId, int userId) {

        return tournamentRepository.findInProgressTournaments(DateHandler.now(), executionId).stream()
                .filter(tournament -> {
                    TournamentAnswer tournamentAnswer = tournament.getTournamentAnswer(userId);
                    return !(tournamentAnswer == null || tournamentAnswer.hasStarted());
                })
                .map(TournamentDto::new)
                .sorted(Comparator.comparing(TournamentDto::getEndTime))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentQuizDto beginQuiz(int tournamentId, int userId) {

        TournamentAnswer tournamentAnswer = findTournamentAnswer(tournamentId, userId);

        if (tournamentAnswer.hasStarted()) { throw new TutorException(TOURNAMENT_ALREADY_STARTED); }
        tournamentAnswer.start();

        return new TournamentQuizDto(tournamentAnswer.getTournament().getQuiz());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void addQuestionAnswer(int tournamentId, int userId, StatementAnswerDto answer) {
        if (answer.getOptionId() == null) { throw new TutorException(OPTION_NOT_FOUND, -1); }
        if (answer.getSequence() == null) { throw new TutorException(QUESTION_ANSWER_NOT_FOUND, -1); }

        TournamentAnswer tournamentAnswer = findTournamentAnswer(tournamentId, userId);
        if (!tournamentAnswer.hasStarted()) { throw new TutorException(TOURNAMENT_NOT_STARTED, tournamentId, userId); }

        QuestionAnswer questionAnswer = tournamentAnswer.getQuestionAnswer(answer.getSequence());

        Option option = optionRepository.findById(answer.getOptionId())
                .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, answer.getOptionId()));

        if (questionAnswer.getTournamentQuestion().isQuestionOption(option)) {
            throw new TutorException(QUESTION_OPTION_MISMATCH, questionAnswer.getTournamentQuestion().getQuestion().getId(), option.getId());
        }

        if (questionAnswer.getOption() != null) {
            questionAnswer.getOption().getQuestionAnswers().remove(questionAnswer);
        }

        questionAnswer.setOption(option);
        questionAnswer.setTimeTaken(answer.getTimeTaken());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CorrectAnswerDto> finishQuiz(int tournamentId, int userId) {

        TournamentAnswer tournamentAnswer = findTournamentAnswer(tournamentId, userId);
        if (!tournamentAnswer.hasStarted()) { throw new TutorException(TOURNAMENT_NOT_STARTED, tournamentId, userId); }
        tournamentAnswer.finish();

        return tournamentAnswer.getQuestionsAnswers().stream()
                .sorted(Comparator.comparing(QuestionAnswer::getSequence))
                .map(CorrectAnswerDto::new)
                .collect(Collectors.toList());
    }

    private TournamentAnswer findTournamentAnswer(int tournamentId, int userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if (!tournament.isAcceptingResponses()) {
            throw new TutorException(TOURNAMENT_NOT_ACCEPTING_RESPONSES, tournament.getId());
        }

        TournamentAnswer tournamentAnswer = tournament.getTournamentAnswer(userId);

        if (tournamentAnswer == null) { throw new TutorException(USER_NOT_FOUND, userId); }

        if (tournamentAnswer.isFinished()) {
            throw new TutorException(TOURNAMENT_ALREADY_FINISHED, tournament.getId(), userId);
        }
        return tournamentAnswer;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean isEnrolled(int tournamentId, int userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));

        return tournament.getTournamentAnswers().stream()
                .anyMatch(tournamentAnswer -> tournamentAnswer.getUser().getId().equals(userId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findTournamentCourseExecution(int tournamentId) {
        return this.tournamentRepository.findById(tournamentId)
                .map(Tournament::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(ErrorMessage.TOURNAMENT_NOT_FOUND, tournamentId));
    }
}
