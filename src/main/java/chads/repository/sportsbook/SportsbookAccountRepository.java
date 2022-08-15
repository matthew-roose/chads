package chads.repository.sportsbook;

import chads.model.sportsbook.SportsbookAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SportsbookAccountRepository extends JpaRepository<SportsbookAccount, String> {
    List<SportsbookAccount> findAllByUsernameIsNotNullOrderByWinLossTotal();
    Optional<SportsbookAccount> findByUserSecret(String userSecret);
}
