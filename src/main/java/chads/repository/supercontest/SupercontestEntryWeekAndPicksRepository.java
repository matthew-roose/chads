package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntryWeekAndPicks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupercontestEntryWeekAndPicksRepository
        extends JpaRepository<SupercontestEntryWeekAndPicks, Integer> {
    Optional<SupercontestEntryWeekAndPicks> findByUsernameAndWeekNumber(String username, Integer weekNumber);
    List<SupercontestEntryWeekAndPicks> findAllByWeekNumber(Integer weekNumber);
}
