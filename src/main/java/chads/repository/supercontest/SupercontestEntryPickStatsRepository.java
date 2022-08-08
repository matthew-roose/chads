package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntryPickStats;
import chads.model.supercontest.SupercontestEntryPickStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupercontestEntryPickStatsRepository
        extends JpaRepository<SupercontestEntryPickStats, SupercontestEntryPickStatsId> {
    List<SupercontestEntryPickStats> findAllByUsername(String username);
}
