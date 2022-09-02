package chads.service;

import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.ScoreUpdate;
import chads.model.User;
import chads.repository.GameLineRepository;
import chads.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameLineService {

    private final GameLineRepository gameLineRepository;

    @Value("${adminId}")
    private String adminId;

    @Autowired
    public GameLineService(GameLineRepository gameLineRepository) {
        this.gameLineRepository = gameLineRepository;
    }

    public Integer getCurrentWeekNumber() {
        return gameLineRepository.findCurrentWeekNumber();
    }

    public List<GameLine> getLinesByWeekNumber(Integer weekNumber) {
        return gameLineRepository.findAllByWeekNumber(weekNumber);
    }

    public List<GameLine> getCurrentWeekLines() {
        return gameLineRepository.findAllInCurrentWeek();
    }

    public List<GameLine> postLines(String googleJwt, Integer weekNumber, List<GameLine> gameLines) throws UnauthorizedException {
        User postingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!postingUser.getUserSecret().equals(adminId)) {
            throw new UnauthorizedException();
        }
        gameLines.forEach(gameLine -> gameLine.setWeekNumber(weekNumber));
        return gameLineRepository.saveAll(gameLines);
    }

    public List<GameLine> scoreGames(String googleJwt, List<ScoreUpdate> scoreUpdates) {
        User updatingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!updatingUser.getUserSecret().equals(adminId)) {
            throw new UnauthorizedException();
        }
        List<GameLine> gameLines = gameLineRepository.findAllInCurrentWeek();
        scoreUpdates.forEach(scoreUpdate -> gameLines.stream().filter(gameLine ->
                gameLine.getGameId().equals(scoreUpdate.getGameId())).forEach(gameLine -> {
                    gameLine.setHomeScore(scoreUpdate.getHomeScore());
                    gameLine.setAwayScore(scoreUpdate.getAwayScore());
                }));
        return gameLineRepository.saveAll(gameLines);
    }
}
