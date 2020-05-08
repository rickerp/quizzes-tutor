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
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDashboardDto;
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
        tournamentDto.setCreatorId(getUser(principal).getId());
        tournamentDto.setCourseExecutionId(executionId);
        return tournamentService.createTournament(tournamentDto);
    }

    @PutMapping("/executions/{executionId}/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto enroll(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        checkPathCoherence(tournamentId, executionId);
        return tournamentService.enroll(getUser(principal).getId(), tournamentId);
    }

    @GetMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getExecutionOpenedTournaments(@PathVariable int executionId) {
        return tournamentService.getExecutionOpenedTournaments(executionId);
    }

    @GetMapping("/executions/{executionId}/tournaments/ongoing")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getExecutionInProgressNonFinishedTournaments(Principal principal, @PathVariable int executionId) {
        return tournamentService.getExecutionInProgressNonFinishedTournaments(executionId, getUser(principal).getId());
    }

    @GetMapping("/executions/{executionId}/tournaments/{tournamentId}/quiz/begin")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public TournamentQuizDto beginQuiz(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        checkPathCoherence(tournamentId, executionId);
        return tournamentService.beginQuiz(tournamentId, getUser(principal).getId());
    }

    @PostMapping("/executions/{executionId}/tournaments/{tournamentId}/quiz/answer")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public void selectQuestionOption(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId, @Valid @RequestBody StatementAnswerDto answer) {
        checkPathCoherence(tournamentId, executionId);
        tournamentService.selectQuestionOption(tournamentId, getUser(principal).getId(), answer);
    }

    @GetMapping("/executions/{executionId}/tournaments/{tournamentId}/quiz/finish")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public List<CorrectAnswerDto> finishQuiz(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        checkPathCoherence(tournamentId, executionId);
        return tournamentService.finishQuiz(tournamentId, getUser(principal).getId());
    }

    @DeleteMapping("/executions/{executionId}/tournaments/{tournamentId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public void removeTournament(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        checkPathCoherence(tournamentId, executionId);
        tournamentService.removeTournament(tournamentId, getUser(principal).getId());
    }

    @GetMapping("/executions/{executionId}/tournaments/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDashboardDto getTournamentDashboard(Principal principal, @PathVariable int executionId) {
        return tournamentService.getTournamentDashboard(getUser(principal).getId(), executionId);
    }

    @PostMapping("/tournaments/privacy")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public Boolean setDashboardPrivacy(Principal principal, @Valid @RequestBody Boolean isPublic) {
        return tournamentService.setDashboardPrivacy(getUser(principal).getId(), isPublic);
    }

    private User getUser(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }
        return user;
    }

    private void checkPathCoherence(int tournamentId, int executionId) {
        if (tournamentService.findTournamentCourseExecution(tournamentId).getCourseExecutionId() != executionId) {
            throw new TutorException(NO_TOURNAMENT_IN_EXECUTION, tournamentId);
        }
    }
}
