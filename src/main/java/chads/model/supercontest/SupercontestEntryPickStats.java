package chads.model.supercontest;

import chads.enums.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Table(name = "sc_entry_pick_stats")
@IdClass(SupercontestEntryPickStatsId.class)
public class SupercontestEntryPickStats {
    @Id
    @Column(name = "username")
    private String username;

    @Id
    @Column(name = "picked_team")
    @Enumerated(EnumType.STRING)
    private Team pickedTeam;

    @Column(name = "total")
    private Integer total;

    @Column(name = "wins")
    private Integer wins;

    @Column(name = "losses")
    private Integer losses;

    @Column(name = "pushes")
    private Integer pushes;
}
