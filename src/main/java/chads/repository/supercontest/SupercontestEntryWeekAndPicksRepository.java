package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntryWeekAndPicks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupercontestEntryWeekAndPicksRepository
        extends JpaRepository<SupercontestEntryWeekAndPicks, Integer> {
    Optional<SupercontestEntryWeekAndPicks> findByUsernameAndWeekNumber(String username, Integer weekNumber);
    Optional<SupercontestEntryWeekAndPicks> findByUserSecretAndWeekNumber(String userSecret, Integer weekNumber);
    List<SupercontestEntryWeekAndPicks> findAllByWeekNumber(Integer weekNumber);
    @Query(value = "SELECT * FROM supercontest_entry_week WHERE week_number = :weekNumber " +
            "AND week_score = (SELECT MAX(week_score) FROM supercontest_entry_week " +
            "WHERE week_number = :weekNumber)", nativeQuery = true)
    List<SupercontestEntryWeekAndPicks> getBestPicksOfTheWeek(Integer weekNumber);
}
