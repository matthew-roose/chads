package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupercontestEntryRepository extends JpaRepository<SupercontestEntry, String> {
    List<SupercontestEntry> findAllByUsernameIsNotNullOrderBySeasonScoreDesc();
}
