package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class ClarificationController {

    @Autowired
    ClarificationRequestService clarificationRequestService;

    @GetMapping("/questionAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationRequestDto createClarification(@PathVariable int questionAnswerId, @RequestBody ClarificationRequestDto clarificationRequestDto) {
        return clarificationRequestService.createClarificationRequest(questionAnswerId, clarificationRequestDto);
    }

    @GetMapping("/executions/{executionId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<ClarificationRequestDto> getClarifications(Principal principal, @PathVariable int executionId) {

        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return clarificationRequestService.getClarificationRequests(user.getUsername(), executionId);
    }

    @GetMapping("/clarifications/{clarificationId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER') and hasPermission(#clarificationId, 'CLARIFICATION.ACCESS')")
    public ClarificationRequestDto getClarification(int clarificationId) {
        return clarificationRequestService.getClarificationRequest(clarificationId);
    }
}
