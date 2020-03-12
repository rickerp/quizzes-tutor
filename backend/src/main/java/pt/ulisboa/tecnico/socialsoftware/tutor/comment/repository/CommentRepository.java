package pt.ulisboa.tecnico.socialsoftware.tutor.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.ulisboa.tecnico.socialsoftware.tutor.comment.domain.Comment;


public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query (value = "SELECT * FROM Comments WHERE clarification_id = :clarificationId", nativeQuery = true)
    Comment findComment(int clarificationId);
}
