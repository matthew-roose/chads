package chads.repository.survivor;

import chads.model.survivor.SurvivorPoolAndEntries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurvivorPoolAndEntriesRepository extends JpaRepository<SurvivorPoolAndEntries, String> {
}
