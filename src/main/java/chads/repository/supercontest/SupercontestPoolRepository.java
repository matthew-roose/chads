package chads.repository.supercontest;

import chads.model.supercontest.SupercontestPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupercontestPoolRepository extends JpaRepository<SupercontestPool, String> {
}
