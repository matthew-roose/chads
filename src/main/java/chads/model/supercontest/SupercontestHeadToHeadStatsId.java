package chads.model.supercontest;

import lombok.Data;

import java.io.Serializable;

@Data
public class SupercontestHeadToHeadStatsId implements Serializable {
    private String username;
    private Integer gameId;
}
