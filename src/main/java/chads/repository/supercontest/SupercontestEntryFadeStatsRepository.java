package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntryFadeStats;
import chads.model.supercontest.SupercontestEntryFadeStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupercontestEntryFadeStatsRepository
        extends JpaRepository<SupercontestEntryFadeStats, SupercontestEntryFadeStatsId> {
    List<SupercontestEntryFadeStats> findAllByUsername(String username);

}
