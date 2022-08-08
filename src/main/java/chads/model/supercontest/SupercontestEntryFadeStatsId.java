package chads.model.supercontest;

import chads.enums.Team;
import lombok.Data;

import java.io.Serializable;

@Data
public class SupercontestEntryFadeStatsId implements Serializable {
    private String username;
    private Team fadedTeam;
}
