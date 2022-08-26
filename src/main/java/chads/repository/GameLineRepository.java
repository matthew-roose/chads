package chads.repository;

import chads.model.GameLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameLineRepository extends JpaRepository<GameLine, Integer> {
    @Query(value = "SELECT MAX(week_number) FROM game_line", nativeQuery = true)
    Integer findCurrentWeekNumber();

    List<GameLine> findAllByWeekNumber(Integer weekNumber);

    @Query(value = "SELECT * FROM game_line WHERE week_number = (SELECT MAX(week_number) FROM game_line)",
            nativeQuery = true)
    List<GameLine> findAllInCurrentWeek();
}
