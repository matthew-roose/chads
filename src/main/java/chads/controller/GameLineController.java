package chads.controller;

import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.ScoreUpdate;
import chads.service.GameLineService;
import chads.service.NotificationService;
import chads.service.SportsbookService;
import chads.service.SupercontestService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class GameLineController {

    private final GameLineService gameLineService;
    private final NotificationService notificationService;
    private final SportsbookService sportsbookService;
    private final SupercontestService supercontestService;
    private final SurvivorService survivorService;

    @GetMapping("/current-week-number")
    public ResponseEntity<Integer> getCurrentWeekNumber() {
        try {
            Integer currentWeekNumber = gameLineService.getCurrentWeekNumber();
            return new ResponseEntity<>(currentWeekNumber, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lines/{weekNumber}")
    public ResponseEntity<List<GameLine>> getLinesByWeekNumber(@PathVariable Integer weekNumber) {
        try {
            List<GameLine> gameLines = gameLineService.getLinesByWeekNumber(weekNumber);
            return new ResponseEntity<>(gameLines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/lines")
    public ResponseEntity<List<GameLine>> getCurrentWeekLines() {
        try {
            List<GameLine> gameLines = gameLineService.getCurrentWeekLines();
            return new ResponseEntity<>(gameLines, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/lines/{weekNumber}")
    public ResponseEntity<List<GameLine>> postLines(
            @RequestHeader("Authorization") String googleJwt,
            @PathVariable Integer weekNumber,
            @RequestBody List<GameLine> gameLines) {
        try {
            List<GameLine> savedLines = gameLineService.postLines(googleJwt, weekNumber, gameLines);
            notificationService.sendNewGamesNotification(googleJwt);
            return new ResponseEntity<>(savedLines, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/lines")
    public ResponseEntity<List<GameLine>> scoreGames(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody List<ScoreUpdate> scoreUpdates) {
        try {
            List<GameLine> updatedLines = gameLineService.scoreGames(googleJwt, scoreUpdates);
            return new ResponseEntity<>(updatedLines, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/grade-all-contests")
    public ResponseEntity<HttpStatus> gradeAllContests(@RequestHeader("Authorization") String googleJwt) {
        try {
            sportsbookService.gradeBets(googleJwt);
            supercontestService.gradePicks(googleJwt);
            survivorService.gradePicks(googleJwt);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/health-check")
    public ResponseEntity<HttpStatus> healthCheck() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
