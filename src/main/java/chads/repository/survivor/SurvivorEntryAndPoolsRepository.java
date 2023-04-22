package chads.repository.survivor;

import chads.model.survivor.SurvivorEntryAndPools;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SurvivorEntryAndPoolsRepository extends JpaRepository<SurvivorEntryAndPools, String> {
    Optional<SurvivorEntryAndPools> findByUserSecret(String userSecret);
}
