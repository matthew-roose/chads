package chads.model.sportsbook;

import lombok.Data;

import java.io.Serializable;

@Data
public class SportsbookWeeklyUserStatsId implements Serializable {
    private Integer weekNumber;
    private String username;
}
