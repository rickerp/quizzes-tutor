package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.Evaluation;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.StudentQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.dto.EvaluationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.EvaluationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository.StudentQuestionRepository;

@Service
public class EvaluationService {
    @Autowired
    private StudentQuestionRepository studentQuestionRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public EvaluationDto createEvaluation(int studentQuestionId, boolean accepted, String justification) {
        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionId).orElse(null);
        if(studentQuestion == null){
            throw new TutorException(ErrorMessage.STUDENT_QUESTION_NOT_FOUND);
        }

        Evaluation evaluation = new Evaluation(studentQuestion, accepted, justification);
        evaluationRepository.save(evaluation);
        return new EvaluationDto(evaluation);
    }
}