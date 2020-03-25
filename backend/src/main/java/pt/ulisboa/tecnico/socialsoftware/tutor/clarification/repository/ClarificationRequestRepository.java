package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationRequest;

import java.util.List;


public interface ClarificationRequestRepository extends JpaRepository<ClarificationRequest, Integer> {
    @Query(value = "SELECT * FROM clarification_requests AS clr WHERE clr.question_Answer_Id = :questionAnswerId", nativeQuery = true)
    List<ClarificationRequest> findClarificationByQuestionAnswer(int questionAnswerId);

}
