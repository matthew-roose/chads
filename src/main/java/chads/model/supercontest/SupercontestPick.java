package chads.model.supercontest;

import chads.enums.Result;
import chads.enums.Team;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "supercontest_pick")
public class SupercontestPick {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "entry_week_id")
    private Integer entryWeekId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "picked_team")
    @Enumerated(EnumType.STRING)
    private Team pickedTeam;

    @Column(name = "opposing_team")
    @Enumerated(EnumType.STRING)
    private Team opposingTeam;

    @Column(name = "home_team")
    @Enumerated(EnumType.STRING)
    private Team homeTeam;

    @Column(name = "away_team")
    @Enumerated(EnumType.STRING)
    private Team awayTeam;

    @Column(name = "home_spread")
    private Double homeSpread;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "entry_week_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SupercontestEntryWeekAndPicks entryWeek;

    public Result calculateResult() {
        if (homeScore == null) {
            return null;
        }
        double homeAdjustedScore = homeScore + homeSpread;
        if (pickedTeam == homeTeam) {
            if (homeAdjustedScore > awayScore) {
                return Result.WIN;
            } else if (homeAdjustedScore < awayScore) {
                return Result.LOSS;
            } else {
                return Result.PUSH;
            }
        } else if (pickedTeam == awayTeam) {
            if (awayScore > homeAdjustedScore) {
                return Result.WIN;
            } else if (awayScore < homeAdjustedScore) {
                return Result.LOSS;
            } else {
                return Result.PUSH;
            }
        }
        // picked team must be home team or away team
        throw new IllegalArgumentException();
    }
}
