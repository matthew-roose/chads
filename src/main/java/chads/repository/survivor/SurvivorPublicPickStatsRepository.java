package chads.repository.survivor;

import chads.model.survivor.SurvivorPublicPickStats;
import chads.model.survivor.SurvivorPublicPickStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurvivorPublicPickStatsRepository
        extends JpaRepository<SurvivorPublicPickStats, SurvivorPublicPickStatsId> {
    List<SurvivorPublicPickStats> findAllByWeekNumber(Integer weekNumber);
    @Query(value = "SELECT * FROM sv_public_pick_stats LIMIT 10", nativeQuery = true)
    List<SurvivorPublicPickStats> findMostPopularPicksOfSeason();
}
