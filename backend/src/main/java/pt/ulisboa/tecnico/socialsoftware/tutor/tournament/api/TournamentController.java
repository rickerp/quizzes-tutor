package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_TOURNAMENT_IN_EXECUTION;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentDto, 'TOURNAMENT.CREATE', #executionId)")
    public TournamentDto createTournament(Principal principal, @RequestBody TournamentDto tournamentDto, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        tournamentDto.setCreatorId(user.getId());
        tournamentDto.setCourseExecutionId(executionId);
        return tournamentService.createTournament(tournamentDto);
    }

    @PutMapping("/executions/{executionId}/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto enroll(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        if (tournamentService.findTournamentCourseExecution(tournamentId).getCourseExecutionId() != executionId) {
            throw new TutorException(NO_TOURNAMENT_IN_EXECUTION, tournamentId);
        }
        return tournamentService.enroll(user.getId(), tournamentId);
    }

    @GetMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getExecutionOpenedTournaments(@PathVariable int executionId) {
        return tournamentService.getExecutionOpenedTournaments(executionId);
    }

    @GetMapping("/executions/{executionId}/tournaments/ongoing")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getExecutionInProgressNonFinishedTournaments(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        return tournamentService.getExecutionInProgressNonFinishedTournaments(executionId, user.getId());
    }

    @GetMapping("/executions/{executionId}/tournaments/{tournamentId}/quiz/begin")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public TournamentQuizDto beginQuiz(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        if (tournamentService.findTournamentCourseExecution(tournamentId).getCourseExecutionId() != executionId) {
            throw new TutorException(NO_TOURNAMENT_IN_EXECUTION, tournamentId);
        }
        return tournamentService.beginQuiz(tournamentId, user.getId());
    }

    @PostMapping("/executions/{executionId}/tournaments/{tournamentId}/quiz/answer")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public void selectQuestionOption(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId, @Valid @RequestBody StatementAnswerDto answer) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        if (tournamentService.findTournamentCourseExecution(tournamentId).getCourseExecutionId() != executionId) {
            throw new TutorException(NO_TOURNAMENT_IN_EXECUTION, tournamentId);
        }
        tournamentService.selectQuestionOption(tournamentId, user.getId(), answer);
    }

    @GetMapping("/executions/{executionId}/tournaments/{tournamentId}/quiz/finish")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public List<CorrectAnswerDto> finishQuiz(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        if (tournamentService.findTournamentCourseExecution(tournamentId).getCourseExecutionId() != executionId) {
            throw new TutorException(NO_TOURNAMENT_IN_EXECUTION, tournamentId);
        }
        return tournamentService.finishQuiz(tournamentId, user.getId());
    }
}
