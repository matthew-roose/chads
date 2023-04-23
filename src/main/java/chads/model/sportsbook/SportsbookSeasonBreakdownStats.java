package chads.model.sportsbook;

import chads.enums.BetLegType;
import chads.enums.BetType;
import chads.enums.Team;
import lombok.Data;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Data
public class SportsbookSeasonBreakdownStats {
    private String username;
    private Map<BetType, SportsbookWinLossProfit> winsAndLossesByBetType; // straight vs parlay
    private Map<Team, SportsbookWinLossProfit> winsAndLossesByPickedTeam;
    private Map<Team, SportsbookWinLossProfit> winsAndLossesByFadedTeam;
    private Map<BetLegType, SportsbookWinLossProfit> winsAndLossesByTotal; // over vs under

    public SportsbookSeasonBreakdownStats(String username) {
        this.username = username;
        this.winsAndLossesByBetType = new HashMap<>();
        winsAndLossesByBetType.put(BetType.STRAIGHT, new SportsbookWinLossProfit());
        winsAndLossesByBetType.put(BetType.PARLAY, new SportsbookWinLossProfit());
        winsAndLossesByBetType.put(BetType.TEASER, new SportsbookWinLossProfit());
        this.winsAndLossesByPickedTeam = new HashMap<>();
        EnumSet.allOf(Team.class).forEach(team -> winsAndLossesByPickedTeam.put(team, new SportsbookWinLossProfit()));
        this.winsAndLossesByFadedTeam = new HashMap<>();
        EnumSet.allOf(Team.class).forEach(team -> winsAndLossesByFadedTeam.put(team, new SportsbookWinLossProfit()));
        this.winsAndLossesByTotal = new HashMap<>();
        winsAndLossesByTotal.put(BetLegType.OVER_TOTAL, new SportsbookWinLossProfit());
        winsAndLossesByTotal.put(BetLegType.UNDER_TOTAL, new SportsbookWinLossProfit());
    }
}
