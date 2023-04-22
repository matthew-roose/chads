package chads.repository.survivor;

import chads.model.survivor.SurvivorEntryAndPicks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurvivorEntryAndPicksRepository extends JpaRepository<SurvivorEntryAndPicks, String> {
}
