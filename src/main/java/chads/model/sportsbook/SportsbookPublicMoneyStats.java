package chads.model.sportsbook;

import lombok.Data;

@Data
public class SportsbookPublicMoneyStats {
    private Integer gameId;
    private Double homeSpreadMoney;
    private Double homeMoneylineMoney;
    private Double awaySpreadMoney;
    private Double awayMoneylineMoney;
    private Double overMoney;
    private Double underMoney;

    public SportsbookPublicMoneyStats(Integer gameId) {
        this.gameId = gameId;
        this.homeSpreadMoney = 0.0;
        this.homeMoneylineMoney = 0.0;
        this.awaySpreadMoney = 0.0;
        this.awayMoneylineMoney = 0.0;
        this.overMoney = 0.0;
        this.underMoney = 0.0;
    }

    public void addHomeSpreadMoney(Double amount) {
        homeSpreadMoney += amount;
    }

    public void addHomeMoneylineMoney(Double amount) {
        homeMoneylineMoney += amount;
    }

    public void addAwaySpreadMoney(Double amount) {
        awaySpreadMoney += amount;
    }

    public void addAwayMoneylineMoney(Double amount) {
        awayMoneylineMoney += amount;
    }

    public void addOverMoney(Double amount) {
        overMoney += amount;
    }

    public void addUnderMoney(Double amount) {
        underMoney += amount;
    }
}
