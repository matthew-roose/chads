package chads.service;

import chads.enums.BetLegType;
import chads.enums.PoolJoinType;
import chads.enums.Result;
import chads.enums.Team;
import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.User;
import chads.model.sportsbook.*;
import chads.repository.GameLineRepository;
import chads.repository.UserRepository;
import chads.repository.sportsbook.SportsbookAccountAndPoolsRepository;
import chads.repository.sportsbook.SportsbookAccountRepository;
import chads.repository.sportsbook.SportsbookBetLegRepository;
import chads.repository.sportsbook.SportsbookBetRepository;
import chads.repository.sportsbook.SportsbookPoolAndAccountsRepository;
import chads.repository.sportsbook.SportsbookPoolRepository;
import chads.repository.sportsbook.SportsbookWeeklyUserStatsRepository;
import chads.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SportsbookService {

    private final UserRepository userRepository;
    private final GameLineRepository gameLineRepository;
    private final SportsbookAccountRepository sportsbookAccountRepository;
    private final SportsbookAccountAndPoolsRepository sportsbookAccountAndPoolsRepository;
    private final SportsbookBetRepository sportsbookBetRepository;
    private final SportsbookBetLegRepository sportsbookBetLegRepository;
    private final SportsbookPoolRepository sportsbookPoolRepository;
    private final SportsbookPoolAndAccountsRepository sportsbookPoolAndAccountsRepository;
    private final SportsbookWeeklyUserStatsRepository sportsbookWeeklyUserStatsRepository;

    @Value("${adminId}")
    private String adminId;

    @Autowired
    public SportsbookService(UserRepository userRepository, GameLineRepository gameLineRepository,
                             SportsbookAccountRepository sportsbookAccountRepository,
                             SportsbookAccountAndPoolsRepository sportsbookAccountAndPoolsRepository,
                             SportsbookBetRepository sportsbookBetRepository,
                             SportsbookBetLegRepository sportsbookBetLegRepository,
                             SportsbookPoolRepository sportsbookPoolRepository,
                             SportsbookPoolAndAccountsRepository sportsbookPoolAndAccountsRepository,
                             SportsbookWeeklyUserStatsRepository sportsbookWeeklyUserStatsRepository) {
        this.userRepository = userRepository;
        this.gameLineRepository = gameLineRepository;
        this.sportsbookAccountRepository = sportsbookAccountRepository;
        this.sportsbookAccountAndPoolsRepository = sportsbookAccountAndPoolsRepository;
        this.sportsbookBetRepository = sportsbookBetRepository;
        this.sportsbookBetLegRepository = sportsbookBetLegRepository;
        this.sportsbookPoolRepository = sportsbookPoolRepository;
        this.sportsbookPoolAndAccountsRepository = sportsbookPoolAndAccountsRepository;
        this.sportsbookWeeklyUserStatsRepository = sportsbookWeeklyUserStatsRepository;
    }

    public void createAccount(String googleJwt) {
        User creatingUser = JwtUtils.getUserFromJwt(googleJwt);
        SportsbookAccount newAccount =
                new SportsbookAccount(creatingUser.getUsername(), creatingUser.getUserSecret());
        sportsbookAccountRepository.save(newAccount);
    }

    public SportsbookAccountAndPools getAccountAndPools(String username) {
        Optional<SportsbookAccountAndPools> accountAndPoolsOptional =
                sportsbookAccountAndPoolsRepository.findById(username);
        if (accountAndPoolsOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookAccountAndPools accountAndPools = accountAndPoolsOptional.get();
        accountAndPools.setUserSecret(null);
        accountAndPools.getPools().forEach(pool -> pool.setPassword(null));
        return accountAndPools;
    }

    public List<SportsbookBet> getAllBetsForUser(String googleJwt, String username) {
        List<SportsbookBet> bets = sportsbookBetRepository.findAllByUsernameOrderByIdDesc(username);
        bets.forEach(bet -> bet.obscureBetLegsForOtherViewers(googleJwt));
        return bets;
    }

    public List<SportsbookBet> getAllBetsForUserAndWeekNumber(String googleJwt, String username, Integer weekNumber) {
        List<SportsbookBet> bets =
                sportsbookBetRepository.findAllByUsernameAndWeekNumberOrderByIdDesc(username, weekNumber);
        bets.forEach(bet -> bet.obscureBetLegsForOtherViewers(googleJwt));
        return bets;
    }

    public List<SportsbookBet> getOpenBetsForUser(String googleJwt, String username) {
        List<SportsbookBet> bets = sportsbookBetRepository.findAllByUsernameAndResultIsNull(username);
        bets.forEach(bet -> bet.obscureBetLegsForOtherViewers(googleJwt));
        return bets;
    }

    public List<SportsbookWeeklyUserStats> getAllWeeklyStatsForUser(String username) {
        List<SportsbookWeeklyUserStats> userWeeks = sportsbookWeeklyUserStatsRepository.findAllByUsername(username);
        userWeeks.forEach(this::setNullsToZero);
        return userWeeks;
    }

    public SportsbookSeasonBreakdownStats getSeasonBreakdownStatsForUser(String username) {
        SportsbookSeasonBreakdownStats stats = new SportsbookSeasonBreakdownStats(username);
        List<SportsbookBet> gradedBets = sportsbookBetRepository.findAllByUsernameAndResultIsNotNull(username);
        gradedBets.forEach(bet -> {
            SportsbookWinLossProfit statsForBetType = stats.getWinsAndLossesByBetType().get(bet.getBetType());
            statsForBetType.addAmountWagered(bet.getWager());
            if (bet.getResult() == Result.WIN) {
                statsForBetType.addAmountWon(bet.getEffectiveToWinAmount());
                statsForBetType.addAmountProfited(bet.getEffectiveToWinAmount());
            } else if (bet.getResult() == Result.LOSS) {
                statsForBetType.addAmountLost(bet.getWager());
                statsForBetType.subtractAmountProfited(bet.getWager());
            }
            bet.getBetLegs().forEach(betLeg -> {
                if (betLeg.getBetLegType() == BetLegType.OVER_TOTAL ||
                        betLeg.getBetLegType() == BetLegType.UNDER_TOTAL) {
                    SportsbookWinLossProfit statsForPickedTotal =
                            stats.getWinsAndLossesByTotal().get(betLeg.getBetLegType());
                    statsForPickedTotal.addAmountWagered(bet.getWager());
                    if (betLeg.getResult() == Result.WIN) {
                        double amountWon = bet.getWager() * (betLeg.getOdds() - 1);
                        statsForPickedTotal.addAmountWon(amountWon);
                        statsForPickedTotal.addAmountProfited(amountWon);
                    } else if (betLeg.getResult() == Result.LOSS) {
                        double amountLost = bet.getWager();
                        statsForPickedTotal.addAmountLost(amountLost);
                        statsForPickedTotal.subtractAmountProfited(amountLost);
                    }
                    return;
                }
                Team pickedTeam = null;
                Team fadedTeam = null;
                if (betLeg.getBetLegType() == BetLegType.HOME_SPREAD ||
                        betLeg.getBetLegType() == BetLegType.HOME_MONEYLINE) {
                    pickedTeam = betLeg.getHomeTeam();
                    fadedTeam = betLeg.getAwayTeam();
                } else if (betLeg.getBetLegType() == BetLegType.AWAY_SPREAD ||
                        betLeg.getBetLegType() == BetLegType.AWAY_MONEYLINE) {
                    pickedTeam = betLeg.getAwayTeam();
                    fadedTeam = betLeg.getHomeTeam();
                }
                // update stats for picked team
                SportsbookWinLossProfit statsForPickedTeam = stats.getWinsAndLossesByPickedTeam().get(pickedTeam);
                statsForPickedTeam.addAmountWagered(bet.getWager());
                // update stats for faded team
                SportsbookWinLossProfit statsForFadedTeam = stats.getWinsAndLossesByFadedTeam().get(fadedTeam);
                statsForFadedTeam.addAmountWagered(bet.getWager());
                // update stats for both
                if (betLeg.getResult() == Result.WIN) {
                    double amountWon = bet.getWager() * (betLeg.getOdds() - 1);
                    statsForPickedTeam.addAmountWon(amountWon);
                    statsForPickedTeam.addAmountProfited(amountWon);
                    statsForFadedTeam.addAmountWon(amountWon);
                    statsForFadedTeam.addAmountProfited(amountWon);
                } else if (betLeg.getResult() == Result.LOSS) {
                    double amountLost = bet.getWager();
                    statsForPickedTeam.addAmountLost(amountLost);
                    statsForPickedTeam.subtractAmountProfited(amountLost);
                    statsForFadedTeam.addAmountLost(amountLost);
                    statsForFadedTeam.subtractAmountProfited(amountLost);
                }
            });
        });
        EnumSet.allOf(Team.class).forEach(team -> {
            if (stats.getWinsAndLossesByPickedTeam().get(team).getAmountWagered() == 0.0) {
                stats.getWinsAndLossesByPickedTeam().remove(team);
            }
            if (stats.getWinsAndLossesByFadedTeam().get(team).getAmountWagered() == 0.0) {
                stats.getWinsAndLossesByFadedTeam().remove(team);
            }

        });
        return stats;
    }

    public SportsbookAccount deposit(String googleJwt) {
        User depositingUser = JwtUtils.getUserFromJwt(googleJwt);
        Optional<SportsbookAccount> accountOptional =
                sportsbookAccountRepository.findByUserSecret(depositingUser.getUserSecret());
        if (accountOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookAccount account = accountOptional.get();
        account.deposit(1000);
        return sportsbookAccountRepository.save(account);
    }

    public SportsbookAccount cashOut(String googleJwt, Integer cashOutAmount) {
        User cashingOutUser = JwtUtils.getUserFromJwt(googleJwt);
        Optional<SportsbookAccount> accountOptional =
                sportsbookAccountRepository.findByUserSecret(cashingOutUser.getUserSecret());
        if (accountOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookAccount account = accountOptional.get();
        account.cashOut(cashOutAmount);
        return sportsbookAccountRepository.save(account);
    }

    // bet must include wager and betLegs
    // bet legs must include gameId and betLegType
    public SportsbookBet placeBet(String googleJwt, SportsbookBet bet) {
        // wager must be at least 1.00
        if (bet.getWager() == null || bet.getWager() < 1) {
            throw new IllegalArgumentException();
        }
        // allow up to 10 legs for parlay
        if (bet.getBetLegs().size() > 10) {
            throw new IllegalArgumentException();
        }
        // get user by access token's user secret
        Optional<User> bettingUserOptional =
                userRepository.findByUserSecret(JwtUtils.getUserFromJwt(googleJwt).getUserSecret());
        if (bettingUserOptional.isEmpty()) {
            throw new NotFoundException();
        }
        User bettingUser = bettingUserOptional.get();
        // get sportsbook account and make sure available balance is sufficient
        Optional<SportsbookAccount> accountOptional =
                sportsbookAccountRepository.findByUserSecret(bettingUser.getUserSecret());
        if (accountOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookAccount account = accountOptional.get();
        if (bet.getWager() > account.getAvailableBalance()) {
            throw new IllegalArgumentException();
        }
        List<GameLine> officialGameLines = gameLineRepository.findAllInCurrentWeek();
        bet.getBetLegs().forEach(betLeg -> {
            // check if bet includes multiple legs for same game
            long countOfGameId = bet.getBetLegs().stream().filter(otherBetLeg ->
                    otherBetLeg.getGameId().equals(betLeg.getGameId())).count();
            if (countOfGameId > 1) {
                throw new IllegalArgumentException();
            }
            // get corresponding game line
            Optional<GameLine> pickedGameOptional = officialGameLines.stream().filter(gameLine ->
                    gameLine.getGameId().equals(betLeg.getGameId())).findAny();
            // invalid gameId for bet leg
            if (pickedGameOptional.isEmpty()) {
                throw new IllegalArgumentException();
            }
            GameLine pickedGame = pickedGameOptional.get();
            // set fields on bet legs
            betLeg.setTimestamp(pickedGame.getTimestamp());
            betLeg.setOdds(pickedGame.getOddsByBetLegType(betLeg.getBetLegType()));
            betLeg.setHomeSpread(pickedGame.getHomeSpread());
            betLeg.setGameTotal(pickedGame.getGameTotal());
            betLeg.setHomeTeam(pickedGame.getHomeTeam());
            betLeg.setAwayTeam(pickedGame.getAwayTeam());
            // check if any bet leg has already started
            if (betLeg.getTimestamp() <= Instant.now().toEpochMilli()) {
                throw new IllegalArgumentException();
            }
        });
        // set fields on bet
        bet.setUsername(bettingUser.getUsername());
        bet.setUserSecret(bettingUser.getUserSecret());
        bet.setPlacedTimestamp(Instant.now().toEpochMilli());
        int currentWeekNumber = officialGameLines.get(0).getWeekNumber();
        bet.setWeekNumber(currentWeekNumber);
        bet.calculateAndSetBetType();
        bet.calculateAndSetOddsAndToWinAmount();
        // need to get autogenerated betId (should figure out how to cascade)
        SportsbookBet placedBet = sportsbookBetRepository.save(new SportsbookBet(bet));
        // using autogenerated betId, set fk on bet legs
        bet.getBetLegs().forEach(betLeg -> betLeg.setBetId(placedBet.getId()));
        placedBet.setBetLegs(bet.getBetLegs());
        SportsbookBet placedBetAndBetLegs = sportsbookBetRepository.save(placedBet);
        // if bet and bet legs saved successfully, update balances
        account.applyPlacedBet(placedBetAndBetLegs.getWager());
        sportsbookAccountRepository.save(account);
        return placedBetAndBetLegs;
    }

    public List<SportsbookAccount> getSeasonLeaderboard() {
        return sportsbookAccountRepository.findAllByUsernameIsNotNullOrderByWinLossTotal();
    }

    public List<SportsbookWeeklyUserStats> getWeeklyLeaderboard(Integer weekNumber) {
        List<SportsbookWeeklyUserStats> weeklyLeaderboard =
                sportsbookWeeklyUserStatsRepository.findAllByWeekNumber(weekNumber);
        weeklyLeaderboard.forEach(this::setNullsToZero);
        return weeklyLeaderboard;
    }

    public List<SportsbookBet> getBestParlays() {
        List<SportsbookBet> bestParlays = sportsbookBetRepository.getBestParlays();
        bestParlays.forEach(parlay -> parlay.setUserSecret(null));
        return bestParlays;
    }

    public List<SportsbookWeeklyUserStats> getBestWeeks() {
        List<SportsbookWeeklyUserStats> winningWeeks = sportsbookWeeklyUserStatsRepository.getBestWeeks();
        winningWeeks.forEach(this::setNullsToZero);
        return winningWeeks;
    }

    public List<SportsbookWeeklyUserStats> getWorstWeeks() {
        List<SportsbookWeeklyUserStats> losingWeeks = sportsbookWeeklyUserStatsRepository.getWorstWeeks();
        losingWeeks.forEach(this::setNullsToZero);
        return losingWeeks;
    }

    public List<SportsbookBet> getBestParlayOfTheWeek(Integer weekNumber) {
        if (weekNumber < 1) {
            throw new IllegalArgumentException();
        }
        return sportsbookBetRepository.getBestParlayOfTheWeek(weekNumber);
    }

    public List<SportsbookWeeklyUserStats> getPublicWeeklyStats() {
        List<SportsbookWeeklyUserStats> publicWeeks = sportsbookWeeklyUserStatsRepository.getPublicWeeklyStats();
        publicWeeks.forEach(this::setNullsToZero);
        return publicWeeks;
    }

    public List<SportsbookPublicMoneyStats> getPublicMoneyStats(Integer weekNumber) {
        Map<Integer, SportsbookPublicMoneyStats> weekStats = new HashMap<>();
        List<SportsbookBet> publicBets = sportsbookBetRepository.findAllByWeekNumber(weekNumber);
        publicBets.forEach(bet -> {
            double wagerAmount = bet.getWager();
            bet.getBetLegs().forEach(betLeg -> {
                if (weekStats.get(betLeg.getGameId()) == null) {
                    weekStats.put(betLeg.getGameId(), new SportsbookPublicMoneyStats(betLeg.getGameId()));
                }
                SportsbookPublicMoneyStats gameStats = weekStats.get(betLeg.getGameId());
                if (betLeg.getBetLegType() == BetLegType.HOME_SPREAD) {
                    gameStats.addHomeSpreadMoney(wagerAmount);
                } else if (betLeg.getBetLegType() == BetLegType.HOME_MONEYLINE) {
                    gameStats.addHomeMoneylineMoney(wagerAmount);
                } else if (betLeg.getBetLegType() == BetLegType.AWAY_SPREAD) {
                    gameStats.addAwaySpreadMoney(wagerAmount);
                } else if (betLeg.getBetLegType() == BetLegType.AWAY_MONEYLINE) {
                    gameStats.addAwayMoneylineMoney(wagerAmount);
                } else if (betLeg.getBetLegType() == BetLegType.OVER_TOTAL) {
                    gameStats.addOverMoney(wagerAmount);
                } else if (betLeg.getBetLegType() == BetLegType.UNDER_TOTAL) {
                    gameStats.addUnderMoney(wagerAmount);
                }
            });
        });
        return new ArrayList<>(weekStats.values());
    }

    public SportsbookPoolAndAccounts getPoolAndAccounts(String poolName) {
        Optional<SportsbookPoolAndAccounts> poolAndAccountsOptional =
                sportsbookPoolAndAccountsRepository.findById(poolName);
        if (poolAndAccountsOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookPoolAndAccounts poolAndAccounts = poolAndAccountsOptional.get();
        poolAndAccounts.setPassword(null);
        poolAndAccounts.getAccounts().forEach(account -> account.setUserSecret(null));
        return poolAndAccounts;
    }

    public List<SportsbookPool> getAllPools() {
        List<SportsbookPool> pools = sportsbookPoolRepository.findAll();
        pools.forEach(pool -> pool.setPassword(null));
        return pools;
    }

    public SportsbookPool createPool(String googleJwt, SportsbookPool pool) {
        // max buy in is 100
        if (pool.getBuyIn() > 100) {
            throw new IllegalArgumentException();
        }
        // prize percentages should add up to 100
        if (pool.getWinLossPrizePct() + pool.getBestParlayPrizePct() != 100) {
            throw new IllegalArgumentException();
        }
        // can't create private pool without password
        if (pool.getJoinType() == PoolJoinType.PRIVATE &&
                (pool.getPassword() == null || pool.getPassword().isEmpty())) {
            throw new IllegalArgumentException();
        }
        // pool name is taken
        if (sportsbookPoolRepository.existsById(pool.getPoolName())) {
            throw new IllegalArgumentException();
        }
        Optional<User> creatingUserOptional =
                userRepository.findByUserSecret(JwtUtils.getUserFromJwt(googleJwt).getUserSecret());
        if (creatingUserOptional.isEmpty()) {
            throw new NotFoundException();
        }
        User creatingUser = creatingUserOptional.get();
        pool.setCreatorUsername(creatingUser.getUsername());
        sportsbookPoolRepository.save(pool);
        // now join pool
        Optional<SportsbookAccountAndPools> creatingAccountOptional =
                sportsbookAccountAndPoolsRepository.findByUserSecret(creatingUser.getUserSecret());
        if (creatingAccountOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookAccountAndPools creatingAccount = creatingAccountOptional.get();
        creatingAccount.joinPool(pool);
        sportsbookAccountAndPoolsRepository.save(creatingAccount);
        return pool;
    }

    public SportsbookAccountAndPools joinPool(String googleJwt, String poolName, String password) {
        if (Instant.now().toEpochMilli() > 1662682800000L) {
            throw new UnauthorizedException();
        }
        Optional<SportsbookPool> poolToBeJoinedOptional =
                sportsbookPoolRepository.findById(poolName);
        if (poolToBeJoinedOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookPool poolToBeJoined = poolToBeJoinedOptional.get();
        if (poolToBeJoined.getJoinType() == PoolJoinType.PRIVATE && !poolToBeJoined.getPassword().equals(password)) {
            throw new UnauthorizedException();
        }
        // after password check, pool password can be scrubbed
        poolToBeJoined.setPassword(null);

        User enrollingUser = JwtUtils.getUserFromJwt(googleJwt);
        Optional<SportsbookAccountAndPools> joiningAccountOptional =
                sportsbookAccountAndPoolsRepository.findByUserSecret(enrollingUser.getUserSecret());
        if (joiningAccountOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SportsbookAccountAndPools joiningAccount = joiningAccountOptional.get();
        joiningAccount.joinPool(poolToBeJoined);
        return sportsbookAccountAndPoolsRepository.save(joiningAccount);
    }

    public List<SportsbookAccount> gradeBets(String googleJwt) {
        User gradingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!gradingUser.getUserSecret().equals(adminId)) {
            throw new UnauthorizedException();
        }

        List<SportsbookBetLeg> allOpenBetLegs = sportsbookBetLegRepository.findAllByResultIsNull();
        List<GameLine> officialGameLines = gameLineRepository.findAllInCurrentWeek();
        allOpenBetLegs.forEach(betLeg -> {
            // get corresponding game line
            Optional<GameLine> pickedGameOptional = officialGameLines.stream().filter(gameLine ->
                    gameLine.getGameId().equals(betLeg.getGameId())).findAny();
            // invalid gameId for bet leg
            if (pickedGameOptional.isEmpty()) {
                throw new IllegalArgumentException();
            }
            GameLine pickedGame = pickedGameOptional.get();
            // game hasn't been scored so can't grade bet leg
            if (pickedGame.getHomeScore() == null) {
                return;
            }
            betLeg.setScoresAndResult(pickedGame);
        });
        sportsbookBetLegRepository.saveAll(allOpenBetLegs);

        List<SportsbookBet> allOpenBets = sportsbookBetRepository.findAllByResultIsNull();
        List<SportsbookAccount> allAccounts = sportsbookAccountRepository.findAll();
        allOpenBets.forEach(openBet -> {
            openBet.updateResultOddsAndWinAmount();
            // if bet was graded, update account balances
            if (openBet.getResult() != null) {
                Optional<SportsbookAccount> accountToUpdateOptional = allAccounts.stream().filter(account ->
                                account.getUsername().equals(openBet.getUsername())).findAny();
                if (accountToUpdateOptional.isEmpty()) {
                    throw new NotFoundException();
                }
                SportsbookAccount accountToUpdate = accountToUpdateOptional.get();
                accountToUpdate.applyGradedBet(openBet);
            }
        });
        sportsbookBetRepository.saveAll(allOpenBets);
        return sportsbookAccountRepository.saveAll(allAccounts);
    }

    private void setNullsToZero(SportsbookWeeklyUserStats week) {
        if (week.getAmountWagered() == null) {
            week.setAmountWagered(0.0);
        }
        if (week.getAmountWon() == null) {
            week.setAmountWon(0.0);
        }
        if (week.getAmountLost() == null) {
            week.setAmountLost(0.0);
        }
        if (week.getProfit() == null) {
            week.setProfit(0.0);
        }
    }
}
