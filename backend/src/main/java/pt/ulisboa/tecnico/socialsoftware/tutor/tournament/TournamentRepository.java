package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
}
