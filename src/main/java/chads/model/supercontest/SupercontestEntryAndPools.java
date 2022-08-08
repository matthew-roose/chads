package chads.model.supercontest;

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
@Table(name = "supercontest_entry")
public class SupercontestEntryAndPools {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "season_score")
    private Double seasonScore;

    @Column(name = "season_wins")
    private Integer seasonWins;

    @Column(name = "season_losses")
    private Integer seasonLosses;

    @Column(name = "season_pushes")
    private Integer seasonPushes;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "supercontest_pool_entry",
            joinColumns = @JoinColumn(name="username"),
            inverseJoinColumns = @JoinColumn(name="pool_name"))
    private Set<SupercontestPool> pools;

    public void joinPool(SupercontestPool poolToBeJoined) {
        pools.add(poolToBeJoined);
    }
}
