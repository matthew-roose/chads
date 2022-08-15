package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookBetLeg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SportsbookBetLegRepository extends JpaRepository<SportsbookBetLeg, Integer> {
    List<SportsbookBetLeg> findAllByResultIsNull();
}
