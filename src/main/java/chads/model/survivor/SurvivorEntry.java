package chads.model.survivor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "survivor_entry")
public class SurvivorEntry {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "score")
    private Double score;

    @Column(name = "wins")
    private Integer wins;

    @Column(name = "losses")
    private Integer losses;

    @Column(name = "pushes")
    private Integer pushes;

    @Column(name = "current_streak")
    private Integer currentStreak;
}
