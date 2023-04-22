package chads.repository.survivor;

import chads.model.survivor.SurvivorPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurvivorPoolRepository extends JpaRepository<SurvivorPool, String> {
}
