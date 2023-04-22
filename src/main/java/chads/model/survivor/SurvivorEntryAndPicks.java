package chads.model.survivor;

import chads.model.User;
import chads.util.JwtUtils;
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
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "survivor_entry")
public class SurvivorEntryAndPicks {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "score")
    private Double score;

    @Column(name = "wins")
    private Integer wins;

    @Column(name = "losses")
    private Integer losses;

    @Column(name = "pushes")
    private Integer pushes;

    @Column(name = "current_streak")
    private Integer currentStreak;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "username", cascade = CascadeType.ALL)
    private List<SurvivorPick> picks;

    public void recordWin() {
        wins++;
        score += 1.0;
        currentStreak++;
    }

    public void recordLoss() {
        losses++;
        currentStreak = 0;
    }

    public void recordPush() {
        pushes++;
        score += .5;
        currentStreak = 0;
    }

    public void obscurePicksForOtherViewers(String googleJwt) {
        User viewingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!userSecret.equals(viewingUser.getUserSecret())) {
            picks.forEach(pick -> {
                pick.setUserSecret(null);
                if (pick.getTimestamp() > Instant.now().toEpochMilli()) {
                    pick.setGameId(null);
                    pick.setTimestamp(null);
                    pick.setPickedTeam(null);
                    pick.setOpposingTeam(null);
                    pick.setHomeTeam(null);
                    pick.setAwayTeam(null);
                }
            });
        }
        userSecret = null;
    }
}
