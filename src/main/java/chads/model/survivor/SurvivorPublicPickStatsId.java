package chads.model.survivor;

import chads.enums.Team;
import lombok.Data;

import java.io.Serializable;

@Data
public class SurvivorPublicPickStatsId implements Serializable {
    private Integer weekNumber;
    private Team pickedTeam;
}
