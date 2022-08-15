package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookAccountAndPools;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SportsbookAccountAndPoolsRepository extends JpaRepository<SportsbookAccountAndPools, String> {
    Optional<SportsbookAccountAndPools> findByUserSecret(String userSecret);
}
