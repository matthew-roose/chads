package chads.controller;

import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.survivor.SurvivorEntryAndPicks;
import chads.model.survivor.SurvivorEntryAndPools;
import chads.model.survivor.SurvivorPick;
import chads.model.survivor.SurvivorPool;
import chads.model.survivor.SurvivorPoolAndEntries;
import chads.model.survivor.SurvivorPublicPickStats;
import chads.service.SurvivorService;
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
@RequestMapping("/survivor")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class SurvivorController {

    private final SurvivorService survivorService;

    @GetMapping("/entry/{username}")
    public ResponseEntity<SurvivorEntryAndPools> getEntryAndPools(@PathVariable String username) {
        try {
            SurvivorEntryAndPools entry = survivorService.getEntryAndPools(username);
            return new ResponseEntity<>(entry, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/entry/{username}/picks")
    public ResponseEntity<SurvivorEntryAndPicks> getEntryAndPicks(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String username) {
        try {
            SurvivorEntryAndPicks entry = survivorService.getEntryAndPicks(googleJwt, username);
            return new ResponseEntity<>(entry, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<SurvivorEntryAndPicks>> getLeaderboard(@RequestHeader("Authorization") String googleJwt) {
        try {
            List<SurvivorEntryAndPicks> leaderboard  = survivorService.getLeaderboard(googleJwt);
            return new ResponseEntity<>(leaderboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/public-picks/week/{weekNumber}")
    public ResponseEntity<List<SurvivorPublicPickStats>> getPublicPicksByWeekNumber(
            @PathVariable Integer weekNumber) {
        try {
            List<SurvivorPublicPickStats> publicPicks = survivorService.getPublicPicksByWeekNumber(weekNumber);
            return new ResponseEntity<>(publicPicks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/public-picks/season")
    public ResponseEntity<List<SurvivorPublicPickStats>> getMostPopularPicksOfSeason() {
        try {
            List<SurvivorPublicPickStats> publicPicks = survivorService.getMostPopularPicksOfSeason();
            return new ResponseEntity<>(publicPicks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/submit-pick")
    public ResponseEntity<SurvivorPick> submitPick(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody SurvivorPick newPick) {
        try {
            SurvivorPick updatedPicks = survivorService.submitPick(googleJwt, newPick);
            return new ResponseEntity<>(updatedPicks, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/losers/week/{weekNumber}")
    public ResponseEntity<List<SurvivorPick>> getLosersByWeek(@PathVariable Integer weekNumber) {
        try {
            List<SurvivorPick> losingPicks = survivorService.getLosersByWeek(weekNumber);
            return new ResponseEntity<>(losingPicks, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pool/{poolName}")
    public ResponseEntity<SurvivorPoolAndEntries> getPoolAndEntries(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String poolName) {
        try {
            SurvivorPoolAndEntries pool = survivorService.getPoolAndEntries(googleJwt, poolName);
            return new ResponseEntity<>(pool, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pool/all")
    public ResponseEntity<List<SurvivorPool>> getAllPools() {
        try {
            List<SurvivorPool> pools = survivorService.getAllPools();
            return new ResponseEntity<>(pools, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/pool")
    public ResponseEntity<SurvivorPool> createPool(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody SurvivorPool newPool) {
        try {
            SurvivorPool pool = survivorService.createPool(googleJwt, newPool);
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
    public ResponseEntity<SurvivorEntryAndPools> joinPool(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable String poolName,
            @RequestBody String password) {
        try {
            SurvivorEntryAndPools entry = survivorService.joinPool(googleJwt, poolName, password);
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
    public ResponseEntity<List<SurvivorEntryAndPicks>> gradePicks(
            @RequestHeader("Authorization") String googleJwt) {
        try {
            List<SurvivorEntryAndPicks> gradedPicks = survivorService.gradePicks(googleJwt);
            return new ResponseEntity<>(gradedPicks, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
