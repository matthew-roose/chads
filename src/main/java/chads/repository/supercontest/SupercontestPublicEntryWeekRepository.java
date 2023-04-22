package chads.repository.supercontest;

import chads.model.supercontest.SupercontestPublicEntryWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupercontestPublicEntryWeekRepository extends JpaRepository<SupercontestPublicEntryWeek, Integer> {
    @Query(value = "SELECT * FROM sc_public_entry_weeks", nativeQuery = true)
    List<SupercontestPublicEntryWeek> getPublicEntryWeeks();
}
