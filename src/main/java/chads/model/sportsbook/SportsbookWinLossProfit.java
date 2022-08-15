package chads.model.sportsbook;

import lombok.Data;

@Data
public class SportsbookWinLossProfit {
    private Double amountWagered;
    private Double amountWon;
    private Double amountLost;
    private Double amountProfited;

    public SportsbookWinLossProfit() {
        this.amountWagered = 0.0;
        this.amountWon = 0.0;
        this.amountLost = 0.0;
        this.amountProfited = 0.0;
    }

    public void addAmountWagered(Double amount) {
        amountWagered += amount;
    }

    public void addAmountWon(Double amount) {
        amountWon += amount;
    }

    public void addAmountLost(Double amount) {
        amountLost += amount;
    }

    public void addAmountProfited(Double amount) {
        amountProfited += amount;
    }

    public void subtractAmountProfited(Double amount) {
        amountProfited -= amount;
    }
}
