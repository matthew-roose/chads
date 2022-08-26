package chads.controller;

import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.sportsbook.SportsbookAccount;
import chads.model.sportsbook.SportsbookAccountAndPools;
import chads.model.sportsbook.SportsbookBet;
import chads.model.sportsbook.SportsbookPool;
import chads.model.sportsbook.SportsbookPoolAndAccounts;
import chads.model.sportsbook.SportsbookPublicMoneyStats;
import chads.model.sportsbook.SportsbookSeasonBreakdownStats;
import chads.model.sportsbook.SportsbookWeeklyUserStats;
import chads.service.SportsbookService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/sportsbook")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class SportsbookController {

    private final SportsbookService sportsbookService;

    @GetMapping("/account/{username}")
    public ResponseEntity<SportsbookAccountAndPools> getAccountAndPools(@PathVariable String username) {
        try {
            SportsbookAccountAndPools account = sportsbookService.getAccountAndPools(username);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{username}/bets")
    public ResponseEntity<List<SportsbookBet>> getAllBetsForUser(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String username) {
        try {
            List<SportsbookBet> bets = sportsbookService.getAllBetsForUser(googleJwt, username);
            return new ResponseEntity<>(bets, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{username}/week/{weekNumber}/bets")
    public ResponseEntity<List<SportsbookBet>> getAllBetsForUseAndWeekNNumber(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String username,
            @PathVariable Integer weekNumber) {
        try {
            List<SportsbookBet> bets =
                    sportsbookService.getAllBetsForUserAndWeekNumber(googleJwt, username, weekNumber);
            return new ResponseEntity<>(bets, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{username}/open-bets")
    public ResponseEntity<List<SportsbookBet>> getOpenBetsForUser(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String username) {
        try {
            List<SportsbookBet> bets = sportsbookService.getOpenBetsForUser(googleJwt, username);
            return new ResponseEntity<>(bets, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{username}/weekly-stats")
    public ResponseEntity<List<SportsbookWeeklyUserStats>> getAllWeeklyStatsForUser(@PathVariable String username) {
        try {
            List<SportsbookWeeklyUserStats> weeklyUserStats = sportsbookService.getAllWeeklyStatsForUser(username);
            return new ResponseEntity<>(weeklyUserStats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/account/{username}/season-breakdown-stats")
    public ResponseEntity<SportsbookSeasonBreakdownStats> getSeasonBreakdownStatsForUser(
            @PathVariable String username) {
        try {
            SportsbookSeasonBreakdownStats stats = sportsbookService.getSeasonBreakdownStatsForUser(username);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<SportsbookAccount> deposit(@RequestHeader("Authorization") String googleJwt) {
        try {
            SportsbookAccount account = sportsbookService.deposit(googleJwt);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cash-out")
    public ResponseEntity<SportsbookAccount> cashOut(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody int cashOutAmount) {
        try {
            SportsbookAccount account = sportsbookService.cashOut(googleJwt, cashOutAmount);
            return new ResponseEntity<>(account, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/place-bet")
    public ResponseEntity<SportsbookBet> placeBet(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody SportsbookBet bet) {
        try {
            SportsbookBet placedBet = sportsbookService.placeBet(googleJwt, bet);
            return new ResponseEntity<>(placedBet, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard/season")
    public ResponseEntity<List<SportsbookAccount>> getSeasonLeaderboard() {
        try {
            List<SportsbookAccount> seasonLeaderboard = sportsbookService.getSeasonLeaderboard();
            return new ResponseEntity<>(seasonLeaderboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard/week/{weekNumber}")
    public ResponseEntity<List<SportsbookWeeklyUserStats>> getWeeklyLeaderboard(@PathVariable Integer weekNumber) {
        try {
            List<SportsbookWeeklyUserStats> weeklyLeaderboard = sportsbookService.getWeeklyLeaderboard(weekNumber);
            return new ResponseEntity<>(weeklyLeaderboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard/best-parlays")
    public ResponseEntity<List<SportsbookWeeklyUserStats>> getBestParlayForEveryWeek() {
        try {
            List<SportsbookWeeklyUserStats> bestParlays = sportsbookService.getBestParlayForEveryWeek();
            return new ResponseEntity<>(bestParlays, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard/biggest-wins")
    public ResponseEntity<List<SportsbookWeeklyUserStats>> getBiggestWinForEveryWeek() {
        try {
            List<SportsbookWeeklyUserStats> biggestWins = sportsbookService.getBiggestWinForEveryWeek();
            return new ResponseEntity<>(biggestWins, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard/biggest-losses")
    public ResponseEntity<List<SportsbookWeeklyUserStats>> getBiggestLossForEveryWeek() {
        try {
            List<SportsbookWeeklyUserStats> biggestLosses = sportsbookService.getBiggestLossForEveryWeek();
            return new ResponseEntity<>(biggestLosses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/public-weekly-stats")
    public ResponseEntity<List<SportsbookWeeklyUserStats>> getPublicWeeklyStats() {
        try {
            List<SportsbookWeeklyUserStats> totalStats = sportsbookService.getPublicWeeklyStats();
            return new ResponseEntity<>(totalStats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/public-money/week/{weekNumber}")
    public ResponseEntity<List<SportsbookPublicMoneyStats>> getPublicMoneyStats(@PathVariable Integer weekNumber) {
        try {
            List<SportsbookPublicMoneyStats> publicMoneyStats = sportsbookService.getPublicMoneyStats(weekNumber);
            return new ResponseEntity<>(publicMoneyStats, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pool/{poolName}")
    public ResponseEntity<SportsbookPoolAndAccounts> getPoolAndAccounts(@PathVariable String poolName) {
        try {
            SportsbookPoolAndAccounts pool = sportsbookService.getPoolAndAccounts(poolName);
            return new ResponseEntity<>(pool, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pool/all")
    public ResponseEntity<List<SportsbookPool>> getAllPools() {
        try {
            List<SportsbookPool> pools = sportsbookService.getAllPools();
            return new ResponseEntity<>(pools, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/pool")
    public ResponseEntity<SportsbookPool> createPool(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody SportsbookPool newPool) {
        try {
            SportsbookPool pool = sportsbookService.createPool(googleJwt, newPool);
            return new ResponseEntity<>(pool, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/pool/{poolName}/join")
    public ResponseEntity<SportsbookAccountAndPools> joinPool(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String poolName,
            @RequestBody String password) {
        try {
            SportsbookAccountAndPools entry = sportsbookService.joinPool(googleJwt, poolName, password);
            return new ResponseEntity<>(entry, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/grade-bets")
    public ResponseEntity<List<SportsbookAccount>> gradeBets(
            @RequestHeader("Authorization") String googleJwt) {
        try {
            List<SportsbookAccount> updatedAccounts = sportsbookService.gradeBets(googleJwt);
            return new ResponseEntity<>(updatedAccounts, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
