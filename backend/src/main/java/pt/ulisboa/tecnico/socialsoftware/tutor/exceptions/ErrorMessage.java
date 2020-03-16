package pt.ulisboa.tecnico.socialsoftware.tutor.exceptions;

public enum ErrorMessage {
    QUIZ_NOT_FOUND("Quiz not found with id %d"),
    QUIZ_QUESTION_NOT_FOUND("Quiz question not found with id %d"),
    QUIZ_ANSWER_NOT_FOUND("Quiz answer not found with id %d"),
    QUESTION_ANSWER_NOT_FOUND("Question answer not found with id %d"),
    OPTION_NOT_FOUND("Option not found with id %d"),
    QUESTION_NOT_FOUND("Question not found with id %d"),
    USER_NOT_FOUND("User not found with id %d"),
    TOPIC_NOT_FOUND("Topic not found with id %d"),
    ASSESSMENT_NOT_FOUND("Assessment not found with id %d"),
    TOPIC_CONJUNCTION_NOT_FOUND("Topic Conjunction not found with id %d"),
    COURSE_EXECUTION_NOT_FOUND("Course execution not found with id %d"),

    COURSE_NOT_FOUND("Course not found with name %s"),
    COURSE_NAME_IS_EMPTY("The course name is empty"),
    COURSE_TYPE_NOT_DEFINED("The course type is not defined"),
    COURSE_EXECUTION_ACRONYM_IS_EMPTY("The course execution acronym is empty"),
    COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY("The course execution academic term is empty"),
    CANNOT_DELETE_COURSE_EXECUTION("The course execution cannot be deleted %s"),
    USERNAME_NOT_FOUND("Username %s not found"),

    QUIZ_USER_MISMATCH("Quiz %s is not assigned to student %s"),
    QUIZ_MISMATCH("Quiz Answer Quiz %d does not match Quiz Question Quiz %d"),
    QUESTION_OPTION_MISMATCH("Question %d does not have option %d"),
    COURSE_EXECUTION_MISMATCH("Course Execution %d does not have quiz %d"),

    DUPLICATE_TOPIC("Duplicate topic: %s"),
    DUPLICATE_USER("Duplicate user: %s"),
    DUPLICATE_COURSE_EXECUTION("Duplicate course execution: %s"),

    USERS_IMPORT_ERROR("Error importing users: %s"),
    QUESTIONS_IMPORT_ERROR("Error importing questions: %s"),
    TOPICS_IMPORT_ERROR("Error importing topics: %s"),
    ANSWERS_IMPORT_ERROR("Error importing answers: %s"),
    QUIZZES_IMPORT_ERROR("Error importing quizzes: %s"),

    QUESTION_IS_USED_IN_QUIZ("Question is used in quiz %s"),
    QUIZ_NOT_CONSISTENT("Field %s of quiz is not consistent"),
    USER_NOT_ENROLLED("%s - Not enrolled in any available course"),
    QUIZ_NO_LONGER_AVAILABLE("This quiz is no longer available"),
    QUIZ_NOT_YET_AVAILABLE("This quiz is not yet available"),

    NO_CORRECT_OPTION("Question does not have a correct option"),
    NOT_ENOUGH_QUESTIONS("Not enough questions to create a quiz"),
    QUESTION_MISSING_DATA("Missing information for quiz"),
    QUESTION_MULTIPLE_CORRECT_OPTIONS("Questions can only have 1 correct option"),
    QUESTION_CHANGE_CORRECT_OPTION_HAS_ANSWERS("Can not change correct option of answered question"),
    QUIZ_HAS_ANSWERS("Quiz already has answers"),
    QUIZ_ALREADY_COMPLETED("Quiz already completed"),
    QUIZ_QUESTION_HAS_ANSWERS("Quiz question has answers"),
    FENIX_ERROR("Fenix Error"),
    AUTHENTICATION_ERROR("Authentication Error"),
    FENIX_CONFIGURATION_ERROR("Incorrect server configuration files for fenix"),

    INVALID_DTO("The dto given is invalid"),
    ACCESS_DENIED("You do not have permission to view this resource"),
    CANNOT_OPEN_FILE("Cannot open file"),

    TOURNAMENT_NOT_FOUND("Tournament not found with id %d"),
    TOURNAMENT_NOT_OPENED("The Tournament is not Opened for Enrollments"),
    USER_NOT_STUDENT("The user %d should be a student"),
    DUPLICATE_TOURNAMENT_ENROLL("A Student can not enroll twice in the same Tournament"),
    TOURNAMENT_NR_QUESTIONS_INVALID("The tournament number of questions should be greater than zero. Given: %d"),
    TOURNAMENT_START_TIME_INVALID("The tournament start time should be greater than the system's time. Given: %s"),
    TOURNAMENT_END_TIME_INVALID("The tournament end time should be greater than it's start time. Given: %s"),
    TOPIC_NOT_AVAILABLE("The topic %d not available"),
    CREATOR_DOES_NOT_FREQUENTS_COURSE_EXECUTION("The creator with id %d does not frequents the course execution with id %d"),
    COURSE_EXECUTION_NOT_ACTIVE("The course execution with id %d is not active"),

    CLARIFICATION_INVALID_USER("User associated to clarification is invalid"),
    CLARIFICATION_INVALID_CONTENT("Clarification content invalid"),
    CLARIFICATION_INVALID_QUESTION_ANSWER("Question Answer associated to clarification is invalid"),
    CLARIFICATION_INVALID_STATE("Clarification state invalid"),
    CLARIFICATION_QUESTION_ANSWER_NOT_IN_USER("User has no question answer with id %d"),
    CLARIFICATION_QUIZ_NOT_COMPLETED("Quiz must be completed to write a clarification"),
    CLARIFICATION_IS_EMPTY("Clarification is empty"),

    COMMENT_INVALID_CLARIFICATION("Clarification associated to comment is invalid"),
    COMMENT_INVALID_CONTENT("ClarificationComment content is invalid"),
    COMMENT_IS_EMPTY("ClarificationComment is empty"),
    COMMENT_INVALID_USER_COURSE("User is not associated with clarification course"),
    COMMENT_INVALID_CLARIFICATION_STATE("Clarification state invalid for comment"),
    COMMENT_INVALID_USER("User associated to comment is invalid"),

    QUESTION_IS_EMPTY("The question is empty"),
    STUDENT_QUESTION_IS_EMPTY("Student question is empty"),
    STUDENT_QUESTION_NOT_FOUND("Student question not in the database"),
    JUSTIFICATION_NOT_FOUND("Justification not found");

    public final String label;

    ErrorMessage(String label) {
        this.label = label;
    }
}