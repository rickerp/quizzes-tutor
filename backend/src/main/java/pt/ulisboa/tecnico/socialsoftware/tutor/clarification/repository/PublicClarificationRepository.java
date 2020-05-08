package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.PublicClarification;

public interface PublicClarificationRepository extends JpaRepository<PublicClarification, Integer> {
}
