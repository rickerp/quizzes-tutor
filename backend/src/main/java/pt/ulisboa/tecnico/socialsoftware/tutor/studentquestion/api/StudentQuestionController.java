package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.api;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.EvaluationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;

import java.security.Principal;
import java.util.List;

@RestController
public class StudentQuestionController {

    @Autowired
    private StudentQuestionService studentQuestionService;

    @Autowired
    private QuestionService questionService;

    @PostMapping("/studentquestions/{studentQuestionId}/publish")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#studentQuestionId, 'STUDENTQUESTION.ACCESS')")
    public StudentQuestionDto publish(@PathVariable Integer studentQuestionId){
        return studentQuestionService.publishStudentQuestion(studentQuestionId);
    }

    @PostMapping("/courses/{courseId}/studentquestion")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto submitStudentQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody StudentQuestionDto studentQuestionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        studentQuestionDto.setStudent(user.getId());
        return studentQuestionService.createStudentQuestion(courseId, studentQuestionDto);
    }
    @PutMapping("/courses/{courseId}/editstudentquestion")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionDto editStudentQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody StudentQuestionDto studentQuestionDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        studentQuestionDto.setStudent(user.getId());
        return studentQuestionService.editStudentQuestion(studentQuestionDto);
    }

    @GetMapping("/courses/{courseId}/studentquestions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getStudentQuestions(Principal principal, @PathVariable int courseId){
        User user = (User) ((Authentication) principal).getPrincipal();
        return studentQuestionService.list(courseId, user.getId());
    }

    @GetMapping("/courses/{courseId}/coursestudentquestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<StudentQuestionDto> getCourseStudentQuestions(Principal principal, @PathVariable int courseId){
        return studentQuestionService.listCourseStudentQuestions(courseId);
    }
}