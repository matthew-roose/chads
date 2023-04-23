package chads.model.survivor;

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
@Table(name = "survivor_pick")
public class SurvivorPick {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "week_number")
    private Integer weekNumber;

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

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private SurvivorEntry entry;

    public Result calculateResult() {
        if (homeScore == null) {
            return null;
        }
        if (pickedTeam == homeTeam) {
            if (homeScore > awayScore) {
                return Result.WIN;
            } else if (homeScore < awayScore) {
                return Result.LOSS;
            } else {
                return Result.PUSH;
            }
        } else if (pickedTeam == awayTeam) {
            if (awayScore > homeScore) {
                return Result.WIN;
            } else if (awayScore < homeScore) {
                return Result.LOSS;
            } else {
                return Result.PUSH;
            }
        }
        // picked team must be home team or away team
        throw new IllegalArgumentException();
    }
}
