package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query(value = "SELECT * FROM tournaments t " +
            "WHERE t.start_time > :now AND t.course_execution_id = :executionId",
            nativeQuery = true)
    List<Tournament> findOpenedTournaments(LocalDateTime now, Integer executionId);

    @Query(value = "SELECT * FROM tournaments t " +
            "WHERE :now BETWEEN t.start_time AND t.end_time AND t.course_execution_id = :executionId",
            nativeQuery = true)
    List<Tournament> findInProgressTournaments(LocalDateTime now, Integer executionId);
}
