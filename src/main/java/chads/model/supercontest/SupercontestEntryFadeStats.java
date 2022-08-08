package chads.model.supercontest;

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
@Table(name = "sc_entry_fade_stats")
@IdClass(SupercontestEntryFadeStatsId.class)
public class SupercontestEntryFadeStats {
    @Id
    @Column(name = "username")
    private String username;

    @Id
    @Column(name = "faded_team")
    @Enumerated(EnumType.STRING)
    private Team fadedTeam;

    @Column(name = "total")
    private Integer total;

    @Column(name = "wins")
    private Integer wins;

    @Column(name = "losses")
    private Integer losses;

    @Column(name = "pushes")
    private Integer pushes;
}