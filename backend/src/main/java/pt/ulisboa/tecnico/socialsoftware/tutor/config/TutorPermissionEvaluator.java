package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;

import java.io.Serializable;
import java.util.Set;

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

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentQuestionService studentQuestionService;

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
                case "STUDENTQUESTION.ACCESS":
                    int questionId = studentQuestionService.findById(id).getQuestion().getId();
                    return userHasAnExecutionOfTheCourse(username, questionService.findQuestionCourse(questionId).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(username, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(username, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(username, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "QUESTION_ANSWER.ACCESS":
                    return userRespondedToThisQuestionAnswer(username, id);
                case "CLARIFICATION.ACCESS":
                    return userHasThisExecution(username, clarificationRequestService.findClarificationCourseExecution(id).getCourseExecutionId());

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

    private boolean userRespondedToThisQuestionAnswer(String username, int id) {
        return answerService.findQuizAnswer(id).getUsername().equals(username);
    }

    private boolean executionHasTopics(int executionId, Set<Integer> topicsId) {
        int courseId = courseService.getCourseByExecutionId(executionId).getCourseId();
        return topicsId.stream().map(topicId -> topicService.findTopicCourse(topicId))
                .allMatch(courseDto -> courseDto.getCourseId() == courseId);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
         String username = ((User) authentication.getPrincipal()).getUsername();

         if (serializable instanceof TournamentDto) {
             TournamentDto tournamentDto = (TournamentDto) serializable;
             Integer executionId = (Integer) o;
             switch (s) {
                 case "TOURNAMENT.CREATE":
                     return  userHasThisExecution(username, executionId) &&
                             executionHasTopics(executionId, tournamentDto.getTopicsId());
                 default:
                     return false;
             }
         }
        return false;
    }
}
