package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsbookBetRepository extends JpaRepository<SportsbookBet, Integer> {
    List<SportsbookBet> findAllByUsernameOrderByIdDesc(String username);
    List<SportsbookBet> findAllByUsernameAndWeekNumberOrderByIdDesc(String username, Integer weekNumber);
    List<SportsbookBet> findAllByUsernameAndResultIsNull(String username);
    List<SportsbookBet> findAllByUsernameAndResultIsNotNull(String username);
    List<SportsbookBet> findAllByWeekNumber(Integer weekNumber);
    List<SportsbookBet> findAllByResultIsNull();
    @Query(value = "SELECT * FROM sportsbook_bet WHERE week_number = :weekNumber AND bet_type = 'PARLAY' " +
            "AND result = 'WIN' AND effective_odds = (SELECT MAX(effective_odds) FROM sportsbook_bet " +
            "WHERE week_number = :weekNumber AND bet_type = 'PARLAY' AND result = 'WIN')", nativeQuery = true)
    List<SportsbookBet> getBestParlayOfTheWeek(Integer weekNumber);
    @Query(value = "SELECT * FROM sportsbook_bet WHERE bet_type = 'PARLAY' AND result = 'WIN' " +
            "ORDER BY effective_odds DESC LIMIT 10", nativeQuery = true)
    List<SportsbookBet> getBestParlays();
}
