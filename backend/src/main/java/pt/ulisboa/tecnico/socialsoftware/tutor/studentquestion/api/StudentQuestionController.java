package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StudentQuestionController {

    @Autowired
    private StudentQuestionService studentQuestionService;

    @Autowired
    private QuestionService questionService;

    @PostMapping("/courses/{courseId}/studentquestion")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto submitStudentQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody StudentQuestionDto studentQuestionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        studentQuestionDto.setStudent(user.getUsername());
        return studentQuestionService.createStudentQuestion(courseId, studentQuestionDto);
    }

    @GetMapping("/courses/{courseId}/studentquestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getStudentQuestions(Principal principal, @PathVariable int courseId){
        User user = (User) ((Authentication) principal).getPrincipal();
        return studentQuestionService.list(courseId, user.getId());
    }
}