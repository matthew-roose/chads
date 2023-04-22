package chads.repository.supercontest;

import chads.model.supercontest.SupercontestHeadToHeadStats;
import chads.model.supercontest.SupercontestHeadToHeadStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupercontestHeadToHeadStatsRepository
        extends JpaRepository<SupercontestHeadToHeadStats, SupercontestHeadToHeadStatsId> {
    @Query(value = "SELECT * FROM sc_head_to_head_stats WHERE game_id IN (SELECT game_id FROM " +
            "supercontest_entry_week sew JOIN supercontest_pick sp ON sp.entry_week_id = sew.id " +
            "WHERE username = :user1 OR username = :user2 GROUP BY game_id HAVING COUNT(game_id) > 1)",
            nativeQuery = true)
    List<SupercontestHeadToHeadStats> getHeadToHeadStats(String user1, String user2);
}
