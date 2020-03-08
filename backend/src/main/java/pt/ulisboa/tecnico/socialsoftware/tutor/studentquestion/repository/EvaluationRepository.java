package pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentquestion.domain.Evaluation;

@Repository
@Transactional
public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {
}
