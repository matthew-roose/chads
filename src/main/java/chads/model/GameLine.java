package chads.model;

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
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
}
