package chads.model.supercontest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "supercontest_entry_week")
public class SupercontestEntryWeek {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "week_score")
    private Double weekScore;

    @Column(name = "week_wins")
    private Integer weekWins;

    @Column(name = "week_losses")
    private Integer weekLosses;

    @Column(name = "week_pushes")
    private Integer weekPushes;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private SupercontestEntryAndEntryWeeks entry;

    public SupercontestEntryWeek(String username, String userSecret, Integer weekNumber,
                                 Double weekScore, Integer weekWins, Integer weekLosses, Integer weekPushes) {
        this.username = username;
        this.userSecret = userSecret;
        this.weekNumber = weekNumber;
        this.weekScore = weekScore;
        this.weekWins = weekWins;
        this.weekLosses = weekLosses;
        this.weekPushes = weekPushes;
    }
}
