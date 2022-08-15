package chads.model.sportsbook;

import chads.enums.BetLegType;
import chads.enums.Result;
import chads.enums.Team;
import chads.model.GameLine;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sportsbook_bet_leg")
public class SportsbookBetLeg {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "bet_id")
    private Integer betId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "bet_leg_type")
    @Enumerated(EnumType.STRING)
    private BetLegType betLegType;

    @Column(name = "odds")
    private Double odds;

    @Column(name = "home_spread")
    private Double homeSpread;

    @Column(name = "game_total")
    private Double gameTotal;

    @Column(name = "home_team")
    @Enumerated(EnumType.STRING)
    private Team homeTeam;

    @Column(name = "away_team")
    @Enumerated(EnumType.STRING)
    private Team awayTeam;

    @Column(name = "home_team_score")
    private Integer homeTeamScore;

    @Column(name = "away_team_score")
    private Integer awayTeamScore;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bet_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SportsbookBet bet;

    public void setScoresAndResult(GameLine gameLine) {
        homeTeamScore = gameLine.getHomeScore();
        awayTeamScore = gameLine.getAwayScore();
        result = gameLine.calculateBetLegResult(betLegType);
    }
}
