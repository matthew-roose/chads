package chads.model.survivor;

import chads.enums.PoolJoinType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "survivor_pool")
public class SurvivorPool {
    @Id
    @Column(name = "pool_name")
    private String poolName;

    @Column(name = "creator_username")
    private String creatorUsername;

    @Column(name = "buy_in")
    private Integer buyIn;

    @Column(name = "join_type")
    @Enumerated(EnumType.STRING)
    private PoolJoinType joinType;

    @Column(name = "password")
    private String password;
}
