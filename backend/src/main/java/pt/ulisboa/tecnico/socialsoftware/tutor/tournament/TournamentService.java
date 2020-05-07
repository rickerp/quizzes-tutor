package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentQuizRepository;
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

    @Autowired
    private TournamentQuizRepository tournamentQuizRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private TournamentQuestionRepository tournamentQuestionRepository;

    @Autowired
    TournamentAnswerRepository tournamentAnswerRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto dto) {
        if (dto == null) { throw new TutorException(INVALID_DTO); }

        User creator = userRepository.findById(dto.getCreatorId())
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, dto.getCreatorId()));

        Set<Topic> topics = dto.getTopicsId().stream()
                .map(topicId -> topicRepository.findById(topicId)
                .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicId)))
                .collect(Collectors.toSet());

        CourseExecution courseExecution = courseExecutionRepository.findById(dto.getCourseExecutionId())
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, dto.getCourseExecutionId()));

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
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, playerId));
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        tournament.enroll(player);
        if (!tournament.quizIsGenerated() && tournament.getTournamentAnswers().size() > 1) {
            this.generateQuiz(tournamentId);
        }
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
    public List<TournamentDto> getExecutionInProgressNonFinishedTournaments(int executionId, int userId) {

        return tournamentRepository.findInProgressTournaments(DateHandler.now(), executionId).stream()
                .filter(tournament -> {
                    TournamentAnswer tournamentAnswer = tournament.getTournamentAnswer(userId);
                    return !(tournamentAnswer == null || tournamentAnswer.isFinished());
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
        return new TournamentQuizDto(tournamentAnswer);
	}

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void selectQuestionOption(int tournamentId, int userId, StatementAnswerDto answer) {

        /* For Verifications */
        findTournamentAnswer(tournamentId, userId);

        QuestionAnswer questionAnswer = questionAnswerRepository.findById(answer.getQuestionAnswerId())
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answer.getQuestionAnswerId()));

        Option option = null;
        if (answer.getOptionId() != null) {
            option = optionRepository.findById(answer.getOptionId())
                    .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, answer.getOptionId()));

            if (!questionAnswer.getTournamentQuestion().isQuestionOption(option)) {
                throw new TutorException(QUESTION_OPTION_MISMATCH, questionAnswer.getQuestion().getId(), option.getId());
            }
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
        tournamentAnswer.finish();

        return tournamentAnswer.getQuestionAnswers().stream()
                .map(CorrectAnswerDto::new)
                .sorted(Comparator.comparing(CorrectAnswerDto::getSequence))
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
    public void removeTournament(int tournamentId, int userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if (!tournament.getCreator().getId().equals(userId)) { throw new TutorException(INVALID_TOURNAMENT_CREATOR); }
        /* Remove Tournament Sub-Components */
        tournament.remove();

        for (TournamentQuestion qa : tournament.getTournamentQuestions()) {
            questionAnswerRepository.deleteAll(qa.getQuestionAnswers());
        }
        tournamentQuestionRepository.deleteAll(tournament.getTournamentQuestions());
        tournamentAnswerRepository.deleteAll(tournament.getTournamentAnswers());
        if (tournament.quizIsGenerated()) tournamentQuizRepository.delete(tournament.getQuiz());
        tournamentRepository.delete(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void generateQuiz(int tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        tournamentQuizRepository.save(tournament.createQuiz());
    }

     @Retryable(
             value = { SQLException.class },
             backoff = @Backoff(delay = 5000))
     @Transactional(isolation = Isolation.REPEATABLE_READ)
     public TournamentDashboardDto getTournamentDashboard(int userId, int executionId) {

         User user = userRepository.findById(userId)
                 .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

         return new TournamentDashboardDto(user, executionId);
     }

    @Retryable(
        value = { SQLException.class },
        backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Boolean setDashboardPrivacy(int userId, Boolean isPublic) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        user.setTournamentDashboardPrivacy(isPublic);
        return user.isTournamentDashboardPublic();
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean isEnrolled(int tournamentId, int userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

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
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }
}
