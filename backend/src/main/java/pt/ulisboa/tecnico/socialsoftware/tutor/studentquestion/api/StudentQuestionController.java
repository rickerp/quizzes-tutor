package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto;

import javax.validation.Valid;

@RestController
public class StudentQuestionController {

    @Autowired
    private StudentQuestionService studentQuestionService;

    @Autowired
    private QuestionService questionService;

    @PostMapping("/courses/{courseId}/studentquestion")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto submitStudentQuestion(@PathVariable int courseId, @Valid @RequestBody StudentQuestionDto studentQuestionDto) {
        return studentQuestionService.createStudentQuestion(courseId, studentQuestionDto);
    }

}