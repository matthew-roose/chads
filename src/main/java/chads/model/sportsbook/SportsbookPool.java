package chads.model.sportsbook;

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
@Table(name = "sportsbook_pool")
public class SportsbookPool {
    @Id
    @Column(name = "pool_name")
    private String poolName;

    @Column(name = "creator_username")
    private String creatorUsername;

    @Column(name = "buy_in")
    private Integer buyIn;

    @Column(name = "win_loss_prize_pct")
    private Integer winLossPrizePct;

    @Column(name = "best_parlay_prize_pct")
    private Integer bestParlayPrizePct;

    @Enumerated(EnumType.STRING)
    @Column(name = "join_type")
    private PoolJoinType joinType;

    @Column(name = "password")
    private String password;
}
