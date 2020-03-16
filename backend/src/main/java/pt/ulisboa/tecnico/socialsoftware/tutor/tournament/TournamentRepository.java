package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query(value = "SELECT * FROM tournaments t " +
            "WHERE t.start_time > now() AND t.course_execution_id IN :executionsId",
            nativeQuery = true)
    List<Tournament> findOpenedTournaments(Set<Integer> executionsId);
}
