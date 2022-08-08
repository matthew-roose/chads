package chads.repository.supercontest;

import chads.model.supercontest.SupercontestPublicPickStats;
import chads.model.supercontest.SupercontestPublicPickStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupercontestPublicPickStatsRepository
        extends JpaRepository<SupercontestPublicPickStats, SupercontestPublicPickStatsId> {
    List<SupercontestPublicPickStats> findAllByWeekNumber(Integer weekNumber);
    @Query(value = "SELECT * FROM sc_public_pick_stats LIMIT 10", nativeQuery = true)
    List<SupercontestPublicPickStats> findMostPopularPicksOfSeason();
}
