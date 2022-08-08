package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntryWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupercontestEntryWeekRepository extends JpaRepository<SupercontestEntryWeek, Integer> {
    List<SupercontestEntryWeek> findAllByWeekNumberOrderByWeekScoreDesc(Integer weekNumber);
}
