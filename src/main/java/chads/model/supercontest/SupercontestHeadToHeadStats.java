package chads.model.supercontest;

import chads.enums.Result;
import chads.enums.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Immutable
@Table(name = "sc_head_to_head_stats")
@IdClass(SupercontestHeadToHeadStatsId.class)
public class SupercontestHeadToHeadStats {
    @Id
    @Column(name = "username")
    private String username;

    @Id
    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "picked_team")
    @Enumerated(EnumType.STRING)
    private Team pickedTeam;

    @Column(name = "home_spread")
    private Double homeSpread;

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
}
