package chads.model.supercontest;

import chads.enums.Team;
import lombok.Data;

import java.io.Serializable;

@Data
public class SupercontestPublicPickStatsId implements Serializable {
    private Integer weekNumber;
    private Team pickedTeam;
}
