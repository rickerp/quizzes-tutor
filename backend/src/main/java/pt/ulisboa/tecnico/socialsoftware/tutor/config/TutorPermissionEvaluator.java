package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationRequestService;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.StudentQuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;

import java.io.Serializable;
import java.util.Set;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private CourseService courseService;

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
    private StudentQuestionService studentQuestionService;

    @Autowired
    private TournamentService tournamentService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        int userId = ((User) authentication.getPrincipal()).getId();
        if (targetDomainObject instanceof CourseDto) {
            CourseDto courseDto = (CourseDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return userService.getEnrolledCoursesAcronyms(userId).contains(courseDto.getAcronym() + courseDto.getAcademicTerm());
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
                    CourseDto courseDto = courseService.getCourseExecutionById(id);
                    return courseDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(userId, id);
                case "QUESTION.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, questionService.findQuestionCourse(id).getCourseId());
                case "STUDENTQUESTION.ACCESS":
                    int questionId = studentQuestionService.findById(id).getQuestion().getId();
                    return userHasAnExecutionOfTheCourse(userId, questionService.findQuestionCourse(questionId).getCourseId());
                case "TOPIC.ACCESS":
                    return userHasAnExecutionOfTheCourse(userId, topicService.findTopicCourse(id).getCourseId());
                case "ASSESSMENT.ACCESS":
                    return userHasThisExecution(userId, assessmentService.findAssessmentCourseExecution(id).getCourseExecutionId());
                case "QUIZ.ACCESS":
                    return userHasThisExecution(userId, quizService.findQuizCourseExecution(id).getCourseExecutionId());
                case "QUESTION_ANSWER.ACCESS":
                    return userRespondedToThisQuestionAnswer(userId, id);
                case "CLARIFICATION.ACCESS":
                    return userHasThisExecution(userId, clarificationRequestService.findClarificationCourseExecution(id).getCourseExecutionId());
                case "TOURNAMENT.ACCESS":
                    return tournamentService.isEnrolled(id, userId);
                default: return false;
            }
        }
        return false;
    }

    private boolean userHasAnExecutionOfTheCourse(int userId, int courseId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseId() == courseId);
    }

    private boolean userHasThisExecution(int userId, int courseExecutionId) {
        return userService.getCourseExecutions(userId).stream()
                .anyMatch(course -> course.getCourseExecutionId() == courseExecutionId);
    }

    private boolean userRespondedToThisQuestionAnswer(int userId, int questionAnswerId) {
        return answerService.isFromUser(questionAnswerId, userId);
    }

    private boolean executionHasTopics(int executionId, Set<Integer> topicsId) {
        int courseId = courseService.getCourseByExecutionId(executionId).getCourseId();
        return topicsId.stream().map(topicId -> topicService.findTopicCourse(topicId))
                .allMatch(courseDto -> courseDto.getCourseId() == courseId);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
         int userId = ((User) authentication.getPrincipal()).getId();

         if (serializable instanceof TournamentDto) {
             TournamentDto tournamentDto = (TournamentDto) serializable;
             Integer executionId = (Integer) o;
             switch (s) {
                 case "TOURNAMENT.CREATE":
                     return  userHasThisExecution(userId, executionId) &&
                             executionHasTopics(executionId, tournamentDto.getTopicsId());
                 default:
                     return false;
             }
         }
        return false;
    }
}
