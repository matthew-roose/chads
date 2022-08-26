package chads.controller;

import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.ScoreUpdate;
import chads.service.GameLineService;
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
            return new ResponseEntity<>(savedLines, HttpStatus.OK);
        } catch (UnauthorizedException e) {
            // called by user other than admin
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
}
