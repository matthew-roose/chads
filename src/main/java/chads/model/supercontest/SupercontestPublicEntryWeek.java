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
@Table(name = "sc_public_entry_weeks")
public class SupercontestPublicEntryWeek {
    @Id
    @Column
    private Integer weekNumber;

    @Column(name = "week_wins")
    private Integer weekWins;

    @Column(name = "week_losses")
    private Integer weekLosses;

    @Column(name = "week_pushes")
    private Integer weekPushes;
}
