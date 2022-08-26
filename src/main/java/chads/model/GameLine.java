package chads.model;

import chads.enums.BetLegType;
import chads.enums.Result;
import chads.enums.Team;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "game_line")
public class GameLine {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer gameId;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "home_team")
    @Enumerated(EnumType.STRING)
    private Team homeTeam;

    @Column(name = "away_team")
    @Enumerated(EnumType.STRING)
    private Team awayTeam;

    @Column(name = "home_spread")
    private Double homeSpread;

    @Column(name = "home_moneyline")
    private Double homeMoneyline;

    @Column(name = "away_moneyline")
    private Double awayMoneyline;

    @Column(name = "game_total")
    private Double gameTotal;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    public Team calculateCoveringTeam() {
        double homeAdjustedScore = homeScore + homeSpread;
        if (homeAdjustedScore > awayScore) {
            return homeTeam;
        } else if (homeAdjustedScore < awayScore) {
            return awayTeam;
        } else {
            return null; // represents push
        }
    }

    public Double getOddsByBetLegType(BetLegType betLegType) {
        switch (betLegType) {
            case HOME_SPREAD:
            case AWAY_SPREAD:
            case OVER_TOTAL:
            case UNDER_TOTAL:
                return 1.91;
            case HOME_MONEYLINE:
                return homeMoneyline;
            case AWAY_MONEYLINE:
                return awayMoneyline;
            default:
                // unreachable
                throw new IllegalArgumentException();
        }
    }

    public Result calculateBetLegResult(BetLegType betLegType) {
        if (homeScore == null) {
            // bet shouldn't be graded if game hasn't been scored
            throw new IllegalArgumentException();
        }
        Team coveringTeam = calculateCoveringTeam();
        Team winningTeam = calculateWinningTeam();
        switch (betLegType) {
            case HOME_SPREAD:
                if (coveringTeam == homeTeam) {
                    return Result.WIN;
                } else if (coveringTeam == awayTeam) {
                    return Result.LOSS;
                } else {
                    return Result.PUSH;
                }
            case AWAY_SPREAD:
                if (coveringTeam == awayTeam) {
                    return Result.WIN;
                } else if (coveringTeam == homeTeam) {
                    return Result.LOSS;
                } else {
                    return Result.PUSH;
                }
            case HOME_MONEYLINE:
                if (winningTeam == homeTeam) {
                    return Result.WIN;
                } else if (winningTeam == awayTeam) {
                    return Result.LOSS;
                } else {
                    return Result.PUSH;
                }
            case AWAY_MONEYLINE:
                if (winningTeam == awayTeam) {
                    return Result.WIN;
                } else if (winningTeam == homeTeam) {
                    return Result.LOSS;
                } else {
                    return Result.PUSH;
                }
            case OVER_TOTAL:
                if (homeScore + awayScore > gameTotal) {
                    return Result.WIN;
                } else if (homeScore + awayScore < gameTotal) {
                    return Result.LOSS;
                } else {
                    return Result.PUSH;
                }
            case UNDER_TOTAL:
                if (homeScore + awayScore < gameTotal) {
                    return Result.WIN;
                } else if (homeScore + awayScore > gameTotal) {
                    return Result.LOSS;
                } else {
                    return Result.PUSH;
                }
            default:
                // unreachable
                throw new IllegalArgumentException();
        }
    }

    private Team calculateWinningTeam() {
        if (homeScore > awayScore) {
            return homeTeam;
        } else if (homeScore < awayScore) {
            return awayTeam;
        } else {
            return null;
        }
    }
}
