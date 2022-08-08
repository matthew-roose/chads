package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntryAndPools;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupercontestEntryAndPoolsRepository extends JpaRepository<SupercontestEntryAndPools, String> {
    Optional<SupercontestEntryAndPools> findByUserSecret(String userSecret);
}
