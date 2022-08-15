package chads.model.sportsbook;

import chads.enums.BetType;
import chads.enums.Result;
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
@Table(name = "sportsbook_account")
public class SportsbookAccount {
    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "available_balance")
    private Double availableBalance;

    @Column(name = "pending_balance")
    private Double pendingBalance;

    @Column(name = "deposit_total")
    private Integer depositTotal;

    @Column(name = "cash_out_total")
    private Integer cashOutTotal;

    @Column(name = "win_loss_total")
    private Double winLossTotal;

    @Column(name = "best_parlay_odds")
    private Double bestParlayOdds;

    public SportsbookAccount(String username, String userSecret) {
        this.username = username;
        this.userSecret = userSecret;
        this.availableBalance = 1000.0;
        this.pendingBalance = 0.0;
        this.depositTotal = 1000;
        this.cashOutTotal = 0;
        this.winLossTotal = 0.0;
        this.bestParlayOdds = 0.0;
    }

    public void deposit(Integer depositAmount) {
        // only allow deposit if total balance is less than 1.00
        if (availableBalance + pendingBalance >= 1) {
            throw new IllegalArgumentException();
        }
        availableBalance += depositAmount;
        depositTotal += depositAmount;
    }

    public void cashOut(Integer cashOutAmount) {
        if (cashOutAmount > availableBalance) {
            throw new IllegalArgumentException();
        }
        availableBalance -= cashOutAmount;
        cashOutTotal += cashOutAmount;
    }

    public void applyPlacedBet(Double wager) {
        availableBalance -= wager;
        pendingBalance += wager;
    }

    public void applyGradedBet(SportsbookBet bet) {
        if (bet.getWager() > pendingBalance) {
            throw new IllegalArgumentException();
        }

        if (bet.getResult() == null) {
            throw new IllegalArgumentException();
        } else if (bet.getResult() == Result.WIN) {
            availableBalance += bet.getWager();
            availableBalance += bet.getEffectiveToWinAmount();
            pendingBalance -= bet.getWager();
            winLossTotal += bet.getEffectiveToWinAmount();
            if (bet.getBetType() == BetType.PARLAY && bet.getEffectiveOdds() > bestParlayOdds) {
                bestParlayOdds = bet.getEffectiveOdds();
            }
        } else if (bet.getResult() == Result.LOSS) {
            pendingBalance -= bet.getWager();
            winLossTotal -= bet.getWager();
        } else if (bet.getResult() == Result.PUSH) {
            availableBalance += bet.getWager();
            pendingBalance -= bet.getWager();
        }
    }
}
