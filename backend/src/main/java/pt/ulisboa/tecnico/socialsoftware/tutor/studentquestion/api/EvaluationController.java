package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.EvaluationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.EvaluationDto;

@RestController
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    @PostMapping("/studentquestions/{studentQuestionId}/evaluation")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#studentQuestionId, 'STUDENTQUESTION.ACCESS')")
    public EvaluationDto createEvaluation(@RequestBody EvaluationDto evaluationDto, @PathVariable Integer studentQuestionId){
        return evaluationService.createEvaluation(evaluationDto, studentQuestionId);
    }
}
