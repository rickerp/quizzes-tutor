package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ClarificationComment;


public interface ClarificationCommentRepository extends JpaRepository<ClarificationComment, Integer> {
    @Query (value = "SELECT * FROM Comments WHERE clarification_id = :clarificationId", nativeQuery = true)
    ClarificationComment findComment(int clarificationId);
}
