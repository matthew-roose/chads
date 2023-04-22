package chads.repository.survivor;

import chads.model.survivor.SurvivorEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurvivorEntryRepository extends JpaRepository<SurvivorEntry, String> {
}
