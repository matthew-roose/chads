package chads.model.sportsbook;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sportsbook_account")
public class SportsbookAccountAndPools {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "available_balance")
    private Double availableBalance;

    @Column(name = "pending_balance")
    private Double pendingBalance;

    @Column(name = "deposit_total")
    private Integer depositTotal;

    @Column(name = "cash_out_total")
    private Integer cashOutTotal;

    @Column(name = "win_loss_total")
    private Double winLossTotal;

    @Column(name = "best_parlay_odds")
    private Double bestParlayOdds;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "sportsbook_pool_entry",
            joinColumns = @JoinColumn(name="username"),
            inverseJoinColumns = @JoinColumn(name="pool_name"))
    private Set<SportsbookPool> pools;

    public void joinPool(SportsbookPool poolToBeJoined) {
        pools.add(poolToBeJoined);
    }
}
