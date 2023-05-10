package chads.repository;

import chads.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserSecret(String userSecret);
    List<User> findAllByOptInNewGamesNotificationIsTrue();
    List<User> findAllByOptInMissingPicksNotificationIsTrue();
}
