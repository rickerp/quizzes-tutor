package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationCommentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.PublicClarificationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationCommentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationRequestDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.PublicClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.stats.ClarificationStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class ClarificationController {

    @Autowired
    ClarificationRequestService clarificationRequestService;

    @Autowired
    ClarificationCommentService clarificationCommentService;

    @Autowired
    PublicClarificationService publicClarificationService;

    @PostMapping("/questionAnswers/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationRequestDto createClarification(Principal principal, @PathVariable int questionAnswerId, @RequestBody ClarificationRequestDto clarificationRequestDto) {

        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);

        clarificationRequestDto.setUser(new UserDto(user));
        return clarificationRequestService.createClarificationRequest(questionAnswerId, clarificationRequestDto);
    }

    @GetMapping("/executions/{executionId}/clarifications")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<ClarificationRequestDto> getClarifications(Principal principal, @PathVariable int executionId) {

        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);
        return clarificationRequestService.getClarificationRequests(user.getId(), executionId);
    }

    @PostMapping("/clarifications/{clarificationRequestId}/comment")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#clarificationRequestId, 'CLARIFICATION.ACCESS')")
    public ClarificationCommentDto createClarificationComment(Principal principal, @PathVariable int clarificationRequestId, @RequestBody ClarificationCommentDto clarificationCommentDto) {

        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);

        clarificationCommentDto.setUser(new UserDto(user));
        return clarificationCommentService.createClarificationComment(clarificationRequestId, clarificationCommentDto);
    }

    @PostMapping("/clarifications/{clarificationRequestId}/state/{state}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#clarificationRequestId, 'CLARIFICATION.ACCESS')")
    public ClarificationRequestDto changeClarificationState(@PathVariable int clarificationRequestId, @PathVariable String state) {
        return clarificationRequestService.changeClarificationState(clarificationRequestId, state);
    }

    @PostMapping("/clarifications/{clarificationRequestId}/type/{type}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#clarificationRequestId, 'CLARIFICATION.ACCESS')")
    public ClarificationRequestDto changeClarificationType(@PathVariable int clarificationRequestId, @PathVariable String type) {
        return clarificationRequestService.changeClarificationType(clarificationRequestId, type);
    }

    @GetMapping("/questions/{questionId}/publicClarifications/executions/{execId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public List<PublicClarificationDto> listPublicClarifications(Principal principal, @PathVariable int execId,
                                                                 @PathVariable int questionId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return publicClarificationService.listAllClarifications(user, execId, questionId);
    }

    @PostMapping("/questions/{questionId}/publicClarifications/{pClrId}/add/executions/{execId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public PublicClarificationDto addCourseExecToPublicClrf(@PathVariable int questionId, @PathVariable int pClrId,
                                                                @PathVariable int execId) {

        return publicClarificationService.addCourseExecToPublicClrf(pClrId, execId);
    }

    @PostMapping("/questions/{questionId}/publicClarifications/{pClrId}/remove/executions/{execId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public PublicClarificationDto rmvCourseExecToPublicClrf(@PathVariable int questionId, @PathVariable int pClrId,
                                                                   @PathVariable int execId) {
        return publicClarificationService.rmvCourseExecToPublicClrf(pClrId, execId);
    }

    @GetMapping("/executions/{executionId}/clarifications/clarificationsStats")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationStatsDto getClarificationsStates(Principal principal, @PathVariable int executionId) {

        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return clarificationRequestService.getClarificationsStats(user.getId());
    }
}
