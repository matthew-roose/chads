package chads.controller;

import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.supercontest.*;
import chads.service.SupercontestService;
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
@RequestMapping("/supercontest")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class SupercontestController {

    private final SupercontestService supercontestService;

    @GetMapping("/entry/{username}")
    public ResponseEntity<SupercontestEntryAndPools> getEntryAndPools(
            @PathVariable String username) {
        try {
            SupercontestEntryAndPools entry = supercontestService.getEntryAndPools(username);
            return new ResponseEntity<>(entry, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/entry/{username}/weekly-stats")
    public ResponseEntity<SupercontestEntryAndEntryWeeks> getAllEntryWeeksForUser(@PathVariable String username) {
        try {
            SupercontestEntryAndEntryWeeks allEntryWeeks = supercontestService.getAllEntryWeeksForUser(username);
            return new ResponseEntity<>(allEntryWeeks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/entry/{username}/pick-stats")
    public ResponseEntity<List<SupercontestEntryPickStats>> getEntryPickStats(@PathVariable String username) {
        try {
            List<SupercontestEntryPickStats> stats = supercontestService.getEntryPickStats(username);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/entry/{username}/fade-stats")
    public ResponseEntity<List<SupercontestEntryFadeStats>> getEntryFadeStats(@PathVariable String username) {
        try {
            List<SupercontestEntryFadeStats> stats = supercontestService.getEntryFadeStats(username);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/public-picks/week/{weekNumber}")
    public ResponseEntity<List<SupercontestPublicPickStats>> getPublicPicksByWeekNumber(
            @PathVariable Integer weekNumber) {
        try {
            List<SupercontestPublicPickStats> publicPicks = supercontestService.getPublicPicksByWeekNumber(weekNumber);
            return new ResponseEntity<>(publicPicks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/public-picks/season")
    public ResponseEntity<List<SupercontestPublicPickStats>> getMostPopularPicksOfSeason() {
        try {
            List<SupercontestPublicPickStats> publicPicks = supercontestService.getMostPopularPicksOfSeason();
            return new ResponseEntity<>(publicPicks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/entry/{username}/week/{weekNumber}")
    public ResponseEntity<SupercontestEntryWeekAndPicks> getEntryWeekAndPicks(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String username,
            @PathVariable Integer weekNumber) {
        try {
            SupercontestEntryWeekAndPicks entry =
                    supercontestService.getEntryWeekAndPicks(googleJwt, username, weekNumber);
            return new ResponseEntity<>(entry, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/entry/{username}/current-week")
    public ResponseEntity<SupercontestEntryWeekAndPicks> saveCurrentEntryWeekAndPicks(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String username,
            @RequestBody List<SupercontestPick> picks) {
        try {
            SupercontestEntryWeekAndPicks savedWeekAndPicks =
                    supercontestService.saveCurrentEntryWeekAndPicks(googleJwt, username, picks);
            return new ResponseEntity<>(savedWeekAndPicks, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard/season")
    public ResponseEntity<List<SupercontestEntry>> getSeasonLeaderboard() {
        try {
            List<SupercontestEntry> seasonLeaderboard = supercontestService.getSeasonLeaderboard();
            return new ResponseEntity<>(seasonLeaderboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard/week/{weekNumber}")
    public ResponseEntity<List<SupercontestEntryWeek>> getWeeklyLeaderboard(
            @PathVariable Integer weekNumber) {
        try {
            List<SupercontestEntryWeek> weeklyLeaderboard = supercontestService.getWeeklyLeaderboard(weekNumber);
            return new ResponseEntity<>(weeklyLeaderboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pool/{poolName}")
    public ResponseEntity<SupercontestPoolAndEntries> getPoolAndEntries(@PathVariable String poolName) {
        try {
            SupercontestPoolAndEntries pool = supercontestService.getPoolAndEntries(poolName);
            return new ResponseEntity<>(pool, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pool/all")
    public ResponseEntity<List<SupercontestPool>> getAllPools() {
        try {
            List<SupercontestPool> pools = supercontestService.getAllPools();
            return new ResponseEntity<>(pools, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/pool")
    public ResponseEntity<SupercontestPool> createPool(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody SupercontestPool newPool) {
        try {
            SupercontestPool pool = supercontestService.createPool(googleJwt, newPool);
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
    public ResponseEntity<SupercontestEntryAndPools> joinPool(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String poolName,
            @RequestBody String password) {
        try {
            SupercontestEntryAndPools entry = supercontestService.joinPool(googleJwt, poolName, password);
            return new ResponseEntity<>(entry, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/grade-picks")
    public ResponseEntity<List<SupercontestEntryWeekAndPicks>> gradePicks(
            @RequestHeader("Authorization") String googleJwt) {
        try {
            List<SupercontestEntryWeekAndPicks> gradedPicks = supercontestService.gradePicks(googleJwt);
            return new ResponseEntity<>(gradedPicks, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
