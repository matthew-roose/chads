package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportsbookPoolRepository extends JpaRepository<SportsbookPool, String> {
}
