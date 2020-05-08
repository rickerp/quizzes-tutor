package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.TournamentQuiz;

@Repository
@Transactional
public interface TournamentQuizRepository extends JpaRepository<TournamentQuiz, Integer> {
}
