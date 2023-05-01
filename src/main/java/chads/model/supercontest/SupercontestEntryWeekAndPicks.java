package chads.model.supercontest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "supercontest_entry_week")
public class SupercontestEntryWeekAndPicks {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "week_score")
    private Double weekScore;

    @Column(name = "week_wins")
    private Integer weekWins;

    @Column(name = "week_losses")
    private Integer weekLosses;

    @Column(name = "week_pushes")
    private Integer weekPushes;

    @Column(name = "has_made_picks")
    private Boolean hasMadePicks;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "entryWeek", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupercontestPick> picks;

    public void updatePicks(List<SupercontestPick> newPicks) {
        picks.clear();
        picks.addAll(newPicks);
    }

    public void recordWin() {
        weekWins++;
        weekScore += 1.0;
    }

    public void recordLoss() {
        weekLosses++;
    }

    public void recordPush() {
        weekPushes++;
        weekScore += .5;
    }
}
