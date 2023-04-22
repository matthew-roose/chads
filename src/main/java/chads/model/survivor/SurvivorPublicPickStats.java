package chads.model.survivor;

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
@Table(name = "sv_public_pick_stats")
@IdClass(SurvivorPublicPickStatsId.class)
public class SurvivorPublicPickStats {
    @Id
    @Column(name = "week_number")
    private Integer weekNumber;

    @Id
    @Column(name = "picked_team")
    @Enumerated(EnumType.STRING)
    private Team pickedTeam;

    @Column(name = "opposing_team")
    @Enumerated(EnumType.STRING)
    private Team opposingTeam;

    @Column(name = "home_team")
    @Enumerated(EnumType.STRING)
    private Team homeTeam;

    @Column(name = "times_picked")
    private Integer timesPicked;

    @Column(name = "home_score")
    private Integer homeScore;

    @Column(name = "away_score")
    private Integer awayScore;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result;
}
