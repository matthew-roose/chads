package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookPoolAndAccounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportsbookPoolAndAccountsRepository extends JpaRepository<SportsbookPoolAndAccounts, String> {
}
