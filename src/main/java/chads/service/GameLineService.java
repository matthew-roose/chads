package chads.service;

import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.ScoreUpdate;
import chads.model.User;
import chads.repository.GameLineRepository;
import chads.util.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class GameLineService {

    private final GameLineRepository gameLineRepository;

    public List<GameLine> getLinesByWeekNumber(Integer weekNumber) {
        return gameLineRepository.findAllByWeekNumber(weekNumber);
    }

    public List<GameLine> getCurrentWeekLines() {
        return gameLineRepository.findAllInCurrentWeek();
    }

    public List<GameLine> postLines(String googleJwt, Integer weekNumber, List<GameLine> gameLines) throws UnauthorizedException {
        User postingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!postingUser.getUserSecret().equals("109251928136244659820")) { // TODO: put as env variable
            throw new UnauthorizedException();
        }
        gameLines.forEach(gameLine -> gameLine.setWeekNumber(weekNumber));
        return gameLineRepository.saveAll(gameLines);
    }

    public List<GameLine> scoreGames(String googleJwt, List<ScoreUpdate> scoreUpdates) {
        User updatingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!updatingUser.getUserSecret().equals("109251928136244659820")) { // TODO: put as env variable
            throw new UnauthorizedException();
        }
        List<GameLine> gameLines = gameLineRepository.findAllInCurrentWeek();
        scoreUpdates.forEach(scoreUpdate -> gameLines.stream().filter(gameLine ->
                gameLine.getId().equals(scoreUpdate.getGameId())).forEach(gameLine -> {
                    gameLine.setHomeScore(scoreUpdate.getHomeTeamScore());
                    gameLine.setAwayScore(scoreUpdate.getAwayTeamScore());
                }));
        return gameLineRepository.saveAll(gameLines);
    }
}
