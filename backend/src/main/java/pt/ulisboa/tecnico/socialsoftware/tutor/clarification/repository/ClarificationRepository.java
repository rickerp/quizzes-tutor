package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;

import java.util.List;


public interface ClarificationRepository extends JpaRepository<Clarification, Integer> {
    @Query(value = "SELECT * FROM clarifications AS clr WHERE clr.question_Answer_Id = :questionAnswerId", nativeQuery = true)
    List<Clarification> findClarificationByQuestionAnswer(int questionAnswerId);
}
