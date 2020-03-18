package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.ImportExportController;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;

import java.io.Serializable;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private AdministrationService administrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private ClarificationRequestService clarificationRequestService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        String username = ((User) authentication.getPrincipal()).getUsername();

        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(username).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
                case "DEMO.ACCESS":
                    return courseDto.getName().equals("Demo Course");
                default:
                    return false;
            }
        }

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "DEMO.ACCESS":
                    CourseDto courseDto = administrationService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(username, id);
                case "QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, questionService.findQuestionCourse(id).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(username, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(username, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "QUESTION_ANSWER.ACCESS":
                    return userRespondedToThisQuestion(username, answerService.findQuizAnswer(id).getId());
                default: return false;
            }
        }
        return false;
    }

    private boolean userHasAnExecutionOfTheCourse(String username, int id) {
        return userService.getCourseExecutions(username).stream()
                .anyMatch(course -> course.getCourseId() == id);
    }

    private boolean userHasThisExecution(String username, int id) {
        return userService.getCourseExecutions(username).stream()
                .anyMatch(course -> course.getCourseExecutionId() == id);
    }

    private boolean userRespondedToThisQuestion(String username, int id) {
        return userService.getQuizAnswers(username).stream()
                .anyMatch(quizAnswer -> quizAnswer.getId() == id);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

}
