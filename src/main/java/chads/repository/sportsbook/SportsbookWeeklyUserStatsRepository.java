package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookWeeklyUserStats;
import chads.model.sportsbook.SportsbookWeeklyUserStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsbookWeeklyUserStatsRepository
        extends JpaRepository<SportsbookWeeklyUserStats, SportsbookWeeklyUserStatsId> {
    List<SportsbookWeeklyUserStats> findAllByUsername(String username);
    List<SportsbookWeeklyUserStats> findAllByWeekNumber(Integer weekNumber);
    @Query(value = "SELECT * FROM sb_weekly_user_stats oq WHERE oq.best_parlay_odds = (SELECT MAX(best_parlay_odds) " +
            "FROM sb_weekly_user_stats sq WHERE sq.week_number = oq.week_number)", nativeQuery = true)
    List<SportsbookWeeklyUserStats> getBestParlayForEveryWeek();
    @Query(value = "SELECT * FROM sb_weekly_user_stats oq WHERE oq.profit = (SELECT MAX(profit) " +
            "FROM sb_weekly_user_stats sq WHERE sq.week_number = oq.week_number)", nativeQuery = true)
    List<SportsbookWeeklyUserStats> getBiggestWinForEveryWeek();
    @Query(value = "SELECT * FROM sb_weekly_user_stats oq WHERE oq.profit = (SELECT MIN(profit) " +
            "FROM sb_weekly_user_stats sq WHERE sq.week_number = oq.week_number)", nativeQuery = true)
    List<SportsbookWeeklyUserStats> getBiggestLossForEveryWeek();
    @Query(value = "SELECT '' as username, week_number, SUM(amount_won) as amount_won, SUM(amount_lost) as amount_lost, " +
            "SUM(profit) as profit, MAX(best_parlay_odds) as best_parlay_odds FROM sb_weekly_user_stats " +
            "GROUP BY week_number", nativeQuery = true)
    List<SportsbookWeeklyUserStats> getPublicWeeklyStats();
}
