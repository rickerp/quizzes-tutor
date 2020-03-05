package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion;

import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class StudentQuestionService {

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    StudentQuestionRepository studentQuestionRepository;

    @PersistenceContext
    EntityManager entityManager;

    public StudentQuestionDto createStudentQuestion(int courseId, StudentQuestionDto studentQuestionDto) {

        if (studentQuestionDto == null)
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_IS_EMPTY);

        QuestionDto questionDto = studentQuestionDto.getQuestion();
        StudentDto studentDto = studentQuestionDto.getStudent();

        if (questionDto == null)
            throw new TutorException(ErrorMessage.QUESTION_IS_EMPTY);

        if (studentDto == null)
            throw new TutorException(ErrorMessage.USER_NOT_FOUND);

        User user = userService.findByUsername(studentDto.getUsername());
        if (user == null)
            throw new TutorException(ErrorMessage.USER_NOT_FOUND);

        QuestionDto createdQuestionDto = questionService.createQuestion(courseId, questionDto);
        int questionId = createdQuestionDto.getId();
        questionService.questionSetStatus(questionId, Question.Status.PENDING);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new TutorException(ErrorMessage.QUESTION_NOT_FOUND));

        StudentQuestion studentQuestion = new StudentQuestion(user, question);
        this.entityManager.persist(studentQuestion);
        return new StudentQuestionDto(studentQuestion);
    }


}
