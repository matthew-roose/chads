package chads.repository.supercontest;

import chads.model.supercontest.SupercontestPoolAndEntries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupercontestPoolAndEntriesRepository extends JpaRepository<SupercontestPoolAndEntries, String> {
}
