package chads.model.supercontest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "supercontest_entry")
public class SupercontestEntry {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "season_score")
    private Double seasonScore;

    @Column(name = "season_wins")
    private Integer seasonWins;

    @Column(name = "season_losses")
    private Integer seasonLosses;

    @Column(name = "season_pushes")
    private Integer seasonPushes;
}
