package chads.model.supercontest;

import chads.enums.PoolJoinType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "supercontest_pool")
public class SupercontestPoolAndEntries {
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

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "supercontest_pool_entry",
            joinColumns = @JoinColumn(name="pool_name"),
            inverseJoinColumns = @JoinColumn(name="username"))
    private Set<SupercontestEntryAndEntryWeeks> entries;
}
