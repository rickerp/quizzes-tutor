package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
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

    @PutMapping("/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public TournamentDto enrollPlayer(Principal principal, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        return tournamentService.enrollPlayer(user.getId(), tournamentId);
    }

    @GetMapping("/tournaments")
    public List<TournamentDto> getUserOpenedTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if(user == null) { throw new TutorException(AUTHENTICATION_ERROR); }

        return tournamentService.getUserOpenedTournaments(user.getId());
    }
}
