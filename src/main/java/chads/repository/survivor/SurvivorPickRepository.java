package chads.repository.survivor;

import chads.enums.Result;
import chads.model.survivor.SurvivorPick;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurvivorPickRepository extends JpaRepository<SurvivorPick, String> {
    List<SurvivorPick> findAllByUserSecret(String userSecret);
    SurvivorPick findByUserSecretAndWeekNumber(String userSecret, Integer weekNumber);
    List<SurvivorPick> findAllByWeekNumberAndResult(Integer weekNumber, Result loss);
}
