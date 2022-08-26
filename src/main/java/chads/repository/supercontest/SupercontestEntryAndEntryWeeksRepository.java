package chads.repository.supercontest;

import chads.model.supercontest.SupercontestEntryAndEntryWeeks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupercontestEntryAndEntryWeeksRepository
        extends JpaRepository<SupercontestEntryAndEntryWeeks, String> {
    SupercontestEntryAndEntryWeeks findByUsername(String username);
}
