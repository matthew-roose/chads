package chads.model;

import lombok.Data;

@Data
public class ScoreUpdate {
    private Integer gameId;
    private Integer homeScore;
    private Integer awayScore;
}
