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

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bet_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SportsbookBet bet;

    public void setHomeSpreadAndGameTotal(Double homeSpread, Double gameTotal, Double teaserPoints) {
        if (teaserPoints == null
                || betLegType == BetLegType.HOME_MONEYLINE
                || betLegType == BetLegType.AWAY_MONEYLINE) {
            setHomeSpread(homeSpread);
            setGameTotal(gameTotal);
        } else {
            if (betLegType == BetLegType.HOME_SPREAD) {
                setHomeSpread(homeSpread + teaserPoints);
                setGameTotal(gameTotal);
            } else if (betLegType == BetLegType.AWAY_SPREAD) {
                setHomeSpread(homeSpread - teaserPoints);
                setGameTotal(gameTotal);
            } else if (betLegType == BetLegType.OVER_TOTAL) {
                setHomeSpread(homeSpread);
                setGameTotal(gameTotal - teaserPoints);
            } else if (betLegType == BetLegType.UNDER_TOTAL) {
                setHomeSpread(homeSpread);
                setGameTotal(gameTotal + teaserPoints);
            }
        }
    }

    public void setScoresAndResult(GameLine gameLine) {
        homeScore = gameLine.getHomeScore();
        awayScore = gameLine.getAwayScore();
        result = calculateBetLegResult();
    }

    private Result calculateBetLegResult() {
        if (homeScore == null) {
            // bet shouldn't be graded if game hasn't been scored
            throw new IllegalArgumentException();
        }

        switch (betLegType) {
            case HOME_SPREAD:
            case AWAY_SPREAD:
                if (betLegType == calculateSpreadWinner()) {
                    return Result.WIN;
                } else if (calculateSpreadWinner() == null) {
                    return Result.PUSH;
                } else {
                    return Result.LOSS;
                }
            case HOME_MONEYLINE:
            case AWAY_MONEYLINE:
                if (betLegType == calculateMoneylineWinner()) {
                    return Result.WIN;
                } else if (calculateMoneylineWinner() == null) {
                    return Result.PUSH;
                } else {
                    return Result.LOSS;
                }
            case OVER_TOTAL:
            case UNDER_TOTAL:
                if (betLegType == calculateTotalWinner()) {
                    return Result.WIN;
                } else if (calculateTotalWinner() == null) {
                    return Result.PUSH;
                } else {
                    return Result.LOSS;
                }
            default:
                // unreachable
                throw new IllegalArgumentException();
        }
    }

    private BetLegType calculateSpreadWinner() {
        // homeSpread should already be set to the teased number if applicable
        double homeAdjustedScore = homeScore + homeSpread;
        if (homeAdjustedScore > awayScore) {
            return BetLegType.HOME_SPREAD;
        } else if (homeAdjustedScore < awayScore) {
            return BetLegType.AWAY_SPREAD;
        } else {
            return null; // represents push
        }
    }

    private BetLegType calculateMoneylineWinner() {
        if (homeScore > awayScore) {
            return BetLegType.HOME_MONEYLINE;
        } else if (homeScore < awayScore) {
            return BetLegType.AWAY_MONEYLINE;
        } else {
            return null;
        }
    }

    private BetLegType calculateTotalWinner() {
        if (homeScore + awayScore > gameTotal) {
            return BetLegType.OVER_TOTAL;
        } else if (homeScore + awayScore < gameTotal) {
            return BetLegType.UNDER_TOTAL;
        } else {
            return null;
        }
    }
}
