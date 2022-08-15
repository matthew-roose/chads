package chads.model.sportsbook;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sb_weekly_user_stats")
@IdClass(SportsbookWeeklyUserStatsId.class)
public class SportsbookWeeklyUserStats {
    @Id
    @Column(name = "week_number")
    private Integer weekNumber;

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "amount_won")
    private Double amountWon;

    @Column(name = "amount_lost")
    private Double amountLost;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "best_parlay_odds")
    private Double bestParlayOdds;
}
