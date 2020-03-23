package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

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
    public TournamentDto enrollPlayer(Principal principal, @PathVariable int tournamentId, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        if (tournamentService.findTournamentCourseExecution(tournamentId).getCourseExecutionId() != executionId) {
            throw new TutorException(NO_TOURNAMENT_IN_EXECUTION, tournamentId);
        }

        return tournamentService.enrollPlayer(user.getId(), tournamentId);
    }

    @GetMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getUserOpenedTournaments(@PathVariable int executionId) {
        return tournamentService.getExecutionOpenedTournaments(executionId);
    }
}
