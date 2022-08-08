package chads.model.supercontest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "supercontest_entry")
public class SupercontestEntryAndEntryWeeks {
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "entry", cascade = CascadeType.ALL)
    private List<SupercontestEntryWeek> supercontestEntryWeeks;

    public void initializeEntryWeeks() {
        for (int i = 1; i <= 18; i++) {
            supercontestEntryWeeks.add(new SupercontestEntryWeek(username, userSecret, i,
                    0.0, 0, 0, 0));
        }
    }

    public void recordWins(Integer weekWins) {
        seasonWins += weekWins;
        seasonScore += weekWins;
    }

    public void recordLosses(Integer weekLosses) {
        seasonLosses += weekLosses;
    }

    public void recordPushes(Integer weekPushes) {
        seasonPushes += weekPushes;
        seasonScore += weekPushes * .5;
    }
}
