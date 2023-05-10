package chads.model;

import chads.enums.BetLegType;
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

    public Double getOddsByBetLegType(BetLegType betLegType, Double teaserPoints, Double boughtPoints) {
        if (teaserPoints == null && boughtPoints == null) {
            switch (betLegType) {
                case HOME_SPREAD:
                case AWAY_SPREAD:
                case OVER_TOTAL:
                case UNDER_TOTAL:
                    return 1.90909;
                case HOME_MONEYLINE:
                    return homeMoneyline;
                case AWAY_MONEYLINE:
                    return awayMoneyline;
                default:
                    // unreachable
                    throw new IllegalArgumentException();
            }
        }
        // can't tease or buy points on moneylines
        if (betLegType == BetLegType.HOME_MONEYLINE || betLegType == BetLegType.AWAY_MONEYLINE) {
            throw new IllegalArgumentException();
        }
        // can't both tease and buy points
        if (teaserPoints != null && boughtPoints != null) {
            throw new IllegalArgumentException();
        }

        if (teaserPoints != null) {
            if (teaserPoints == 6.0) {
                return 1.38461;
            } else if (teaserPoints == 6.5) {
                return 1.35714;
            } else if (teaserPoints == 7.0) {
                return 1.32258;
            } else if (teaserPoints == 7.5) {
                return 1.28571;
            } else if (teaserPoints == 8.0) {
                return 1.26666;
            } else if (teaserPoints == 8.5) {
                return 1.25;
            } else if (teaserPoints == 9.0) {
                return 1.22222;
            } else if (teaserPoints == 9.5) {
                return 1.21276;
            } else if (teaserPoints == 10.0) {
                return 1.2;
            }
            // illegal number of teaser points
            throw new IllegalArgumentException();
        }

        if (boughtPoints == 0.5) {
            return 1.8;
        } else if (boughtPoints == 1.0) {
            return 1.66667;
        } else if (boughtPoints == 1.5) {
            return 1.57143;
        } else if (boughtPoints == 2.0) {
            return 1.5;
        }
        // illegal number of bought points
        throw new IllegalArgumentException();
    }
}
