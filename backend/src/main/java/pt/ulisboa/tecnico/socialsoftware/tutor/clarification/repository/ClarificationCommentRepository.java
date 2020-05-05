package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment;

import java.util.List;


public interface ClarificationCommentRepository extends JpaRepository<ClarificationComment, Integer> {
    @Query (value = "SELECT * FROM Clarification_comments WHERE clarification_request_id = :clarificationRequestId", nativeQuery = true)
    List<ClarificationComment> findComments(int clarificationRequestId);
}
