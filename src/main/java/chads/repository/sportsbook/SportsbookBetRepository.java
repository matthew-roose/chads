package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookBet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsbookBetRepository extends JpaRepository<SportsbookBet, Integer> {
    List<SportsbookBet> findAllByUsername(String username);
    List<SportsbookBet> findAllByUsernameAndWeekNumber(String username, Integer weekNumber);
    List<SportsbookBet> findAllByUsernameAndResultIsNull(String username);
    List<SportsbookBet> findAllByUsernameAndResultIsNotNull(String username);
    List<SportsbookBet> findAllByWeekNumber(Integer weekNumber);
    List<SportsbookBet> findAllByResultIsNull();
}
