package chads.model.sportsbook;

import chads.enums.BetLegType;
import chads.enums.BetType;
import chads.enums.Result;
import chads.model.User;
import chads.util.JwtUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sportsbook_bet")
public class SportsbookBet {
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "placed_timestamp")
    private Long placedTimestamp;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "bet_type")
    @Enumerated(EnumType.STRING)
    private BetType betType;

    @Column(name = "odds")
    private Double odds;

    @Column(name = "effective_odds")
    private Double effectiveOdds;

    @Column(name = "wager")
    private Double wager;

    @Column(name = "to_win_amount")
    private Double toWinAmount;

    @Column(name = "effective_to_win_amount")
    private Double effectiveToWinAmount;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    private Result result;

    @Column(name = "profit")
    private Double profit;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "bet", cascade = CascadeType.ALL)
    private List<SportsbookBetLeg> betLegs;


    public SportsbookBet(SportsbookBet betToCopy) {
        this.id = null;
        this.username = betToCopy.getUsername();
        this.userSecret = betToCopy.getUserSecret();
        this.placedTimestamp = betToCopy.getPlacedTimestamp();
        this.weekNumber = betToCopy.getWeekNumber();
        this.betType = betToCopy.getBetType();
        this.odds = betToCopy.getOdds();
        this.effectiveOdds = betToCopy.getEffectiveOdds();
        this.wager = betToCopy.getWager();
        this.toWinAmount = betToCopy.getToWinAmount();
        this.effectiveToWinAmount = betToCopy.getEffectiveToWinAmount();
        this.result = null;
        this.profit = null;
        this.betLegs = null;
    }

    public void obscureBetLegsForOtherViewers(String googleJwt) {
        User viewingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!userSecret.equals(viewingUser.getUserSecret())) {
            betLegs.forEach(betLeg -> {
                if (betLeg.getTimestamp() > Instant.now().toEpochMilli()) {
                    betLeg.setGameId(null);
                    betLeg.setTimestamp(null);
                    betLeg.setBetLegType(null);
                    betLeg.setOdds(null);
                    betLeg.setHomeTeam(null);
                    betLeg.setAwayTeam(null);
                    betLeg.setHomeSpread(null);
                    betLeg.setGameTotal(null);
                }
            });
        }
        userSecret = null;
    }

    public void calculateAndSetBetType() {
        if (betLegs.size() > 1) {
            betType = BetType.PARLAY;
        } else if (betLegs.size() == 1) {
            betType = BetType.STRAIGHT;
        } else {
            // no bet legs provided
            throw new IllegalArgumentException();
        }
    }

    public void calculateAndSetOddsAndToWinAmount() {
        odds = 1.0;
        effectiveOdds = 1.0;
        betLegs.forEach(betLeg -> {
            if (betLeg.getBetLegType() == BetLegType.HOME_SPREAD ||
                    betLeg.getBetLegType() == BetLegType.AWAY_SPREAD ||
                    betLeg.getBetLegType() == BetLegType.OVER_TOTAL ||
                    betLeg.getBetLegType() == BetLegType.UNDER_TOTAL) {
                // use 1.90909 as truer representation of -110 than 1.91 (rounded in DB)
                odds *= 1.90909;
                if (betLeg.getResult() != Result.PUSH) {
                    effectiveOdds *= 1.90909;
                }
            } else  {
                // can use ML odds
                odds *= betLeg.getOdds();
                if (betLeg.getResult() != Result.PUSH) {
                    effectiveOdds *= betLeg.getOdds();
                }
            }
        });
        toWinAmount = Double.valueOf(df.format(odds * wager - wager));
        if (toWinAmount > 1000000) {
            throw new IllegalArgumentException();
        }
        odds = Double.valueOf(df.format(odds));
        effectiveToWinAmount = Double.valueOf(df.format(effectiveOdds * wager - wager));
        effectiveOdds = Double.valueOf(df.format(effectiveOdds));
    }

    public void updateResultOddsAndWinAmount() {
        calculateAndSetOddsAndToWinAmount();
        if (areAnyBetLegsLosses()) {
            result = Result.LOSS;
            profit = wager * -1;
        } else if (areAllBetLegsPushes()) {
            result = Result.PUSH;
            profit = 0.0;
        } else if (areAllBetLegsWinsOrPushes()) {
            result = Result.WIN;
            profit = effectiveToWinAmount;
        }
    }

    private boolean areAllBetLegsWinsOrPushes() {
        for (SportsbookBetLeg betLeg : betLegs) {
            if (betLeg.getResult() != Result.WIN && betLeg.getResult() != Result.PUSH) {
                return false;
            }
        }
        return true;
    }

    private boolean areAnyBetLegsLosses() {
        for (SportsbookBetLeg betLeg : betLegs) {
            if (betLeg.getResult() == Result.LOSS) {
                return true;
            }
        }
        return false;
    }

    private boolean areAllBetLegsPushes() {
        for (SportsbookBetLeg betLeg : betLegs) {
            if (betLeg.getResult() != Result.PUSH) {
                return false;
            }
        }
        return true;
    }
}
