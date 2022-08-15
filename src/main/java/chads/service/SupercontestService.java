package chads.service;

import chads.enums.PoolJoinType;
import chads.enums.Result;
import chads.enums.Team;
import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.User;
import chads.model.supercontest.*;
import chads.repository.GameLineRepository;
import chads.repository.UserRepository;
import chads.repository.supercontest.*;
import chads.util.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class SupercontestService {

    private final UserRepository userRepository;
    private final GameLineRepository gameLineRepository;
    private final SupercontestEntryRepository supercontestEntryRepository;
    private final SupercontestEntryAndEntryWeeksRepository supercontestEntryAndEntryWeeksRepository;
    private final SupercontestEntryAndPoolsRepository supercontestEntryAndPoolsRepository;
    private final SupercontestEntryWeekRepository supercontestEntryWeekRepository;
    private final SupercontestEntryWeekAndPicksRepository supercontestEntryWeekAndPicksRepository;
    private final SupercontestPoolRepository supercontestPoolRepository;
    private final SupercontestPoolAndEntriesRepository supercontestPoolAndEntriesRepository;
    private final SupercontestEntryPickStatsRepository supercontestEntryPickStatsRepository;
    private final SupercontestEntryFadeStatsRepository supercontestEntryFadeStatsRepository;
    private final SupercontestPublicPickStatsRepository supercontestPublicPickStatsRepository;

    public void createEntry(String googleJwt) {
        User creatingUser = JwtUtils.getUserFromJwt(googleJwt);
        SupercontestEntryAndEntryWeeks newEntry =
                new SupercontestEntryAndEntryWeeks(creatingUser.getUsername(), creatingUser.getUserSecret(),
                        0.0, 0, 0, 0, new ArrayList<>());
        newEntry.initializeEntryWeeks();
        // why does this insert each week separately?
        supercontestEntryAndEntryWeeksRepository.save(newEntry);
    }

    public SupercontestEntryAndPools getEntryAndPools(String username) {
        Optional<SupercontestEntryAndPools> entryAndPoolsOptional =
                supercontestEntryAndPoolsRepository.findById(username);
        if (entryAndPoolsOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestEntryAndPools entryAndPools = entryAndPoolsOptional.get();
        entryAndPools.setUserSecret(null);
        entryAndPools.getPools().forEach(pool -> pool.setPassword(null));
        return entryAndPools;
    }

    public List<SupercontestEntryPickStats> getEntryPickStats(String username) {
        List<SupercontestEntryPickStats> stats = supercontestEntryPickStatsRepository.findAllByUsername(username);
        if (stats.isEmpty()) {
            throw new NotFoundException();
        }
        return stats;
    }

    public List<SupercontestEntryFadeStats> getEntryFadeStats(String username) {
        List<SupercontestEntryFadeStats> stats = supercontestEntryFadeStatsRepository.findAllByUsername(username);
        if (stats.isEmpty()) {
            throw new NotFoundException();
        }
        return stats;
    }

    public List<SupercontestPublicPickStats> getPublicPicksByWeekNumber(Integer weekNumber) {
        return supercontestPublicPickStatsRepository.findAllByWeekNumber(weekNumber);
    }

    public List<SupercontestPublicPickStats> getMostPopularPicksOfSeason() {
        return supercontestPublicPickStatsRepository.findMostPopularPicksOfSeason();
    }

    public SupercontestEntryWeekAndPicks getEntryWeekAndPicks(String googleJwt, String username, Integer weekNumber) {
        Optional<SupercontestEntryWeekAndPicks> weekAndPicksOptional =
                supercontestEntryWeekAndPicksRepository.findByUsernameAndWeekNumber(username, weekNumber);
        if (weekAndPicksOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestEntryWeekAndPicks weekAndPicks = weekAndPicksOptional.get();
        User viewingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!weekAndPicks.getUserSecret().equals(viewingUser.getUserSecret())) {
            // hide picks that haven't started yet
            weekAndPicks.getPicks().forEach(pick -> {
                if (pick.getTimestamp() > Instant.now().toEpochMilli()) {
                    pick.setGameId(null);
                    pick.setTimestamp(null);
                    pick.setPickedTeam(null);
                    pick.setHomeTeam(null);
                    pick.setAwayTeam(null);
                    pick.setHomeSpread(null);
                }
            });

        }
        weekAndPicks.setUserSecret(null);
        return weekAndPicks;
    }

    public SupercontestEntryWeekAndPicks saveEntryWeekAndPicks(
            String googleJwt, String username, Integer weekNumber, Set<SupercontestPick> newPicks) {
        if (newPicks.size() > 5) {
            throw new IllegalArgumentException();
        }
        Optional<SupercontestEntryWeekAndPicks> weekAndPicksOptional =
                supercontestEntryWeekAndPicksRepository.findByUsernameAndWeekNumber(username, weekNumber);
        if (weekAndPicksOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestEntryWeekAndPicks weekAndPicks = weekAndPicksOptional.get();
        User requestingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!weekAndPicks.getUserSecret().equals(requestingUser.getUserSecret())) {
            throw new UnauthorizedException();
        }
        // any previously submitted pick for a game that has started must be in the new picks
        weekAndPicks.getPicks().forEach(existingPick -> {
            if (existingPick.getTimestamp() <= Instant.now().toEpochMilli() &&
                    (newPicks.stream().noneMatch(newPick ->
                        newPick.getPickedTeam() == existingPick.getPickedTeam()))) {
                    throw new IllegalArgumentException();
                }
        });
        List<GameLine> officialGameLines = gameLineRepository.findAllByWeekNumber(weekNumber);
        newPicks.forEach(newPick -> {
            Optional<GameLine> pickedGameOptional =
                    officialGameLines.stream().filter(gameLine ->
                            gameLine.getId().equals(newPick.getGameId())).findAny();
            // invalid gameId for pick
            if (pickedGameOptional.isEmpty()) {
                throw new IllegalArgumentException();
            }
            GameLine pickedGame = pickedGameOptional.get();
            // invalid pickedTeam for game
            if (!(newPick.getPickedTeam() == pickedGame.getHomeTeam()) &&
                    !(newPick.getPickedTeam() == pickedGame.getAwayTeam())) {
                throw new IllegalArgumentException();
            }
            // don't allow picks for both teams in a game
            if (newPicks.stream().filter(pick -> pick.getGameId().equals(newPick.getGameId())).count() > 1) {
                throw new IllegalArgumentException();
            }
            // don't allow any new picks for games that have started
            if (pickedGame.getTimestamp() <= Instant.now().toEpochMilli() &&
                    weekAndPicks.getPicks().stream().noneMatch(existingPick ->
                        existingPick.getPickedTeam() == newPick.getPickedTeam())) {
                    throw new IllegalArgumentException();
            }
            // set foreign key using retrieved EntryWeek
            newPick.setEntryWeekId(weekAndPicks.getId());
            // set other properties using GameLine with corresponding gameId (back end source of truth)
            newPick.setTimestamp(pickedGame.getTimestamp());
            newPick.setHomeTeam(pickedGame.getHomeTeam());
            newPick.setAwayTeam(pickedGame.getAwayTeam());
            newPick.setHomeSpread(pickedGame.getHomeSpread());
            // if game has been played, re-set scores and result since the pick is recreated as a new record
            if (pickedGame.getHomeScore() != null) {
                newPick.setHomeTeamScore(pickedGame.getHomeScore());
                newPick.setAwayTeamScore(pickedGame.getAwayScore());
                if (pickedGame.calculateCoveringTeam() == newPick.getPickedTeam()) {
                    newPick.setResult(Result.WIN);
                } else if (pickedGame.calculateCoveringTeam() == null) {
                    newPick.setResult(Result.PUSH);
                } else {
                    newPick.setResult(Result.LOSS);
                }
            }
        });
        weekAndPicks.updatePicks(newPicks);
        weekAndPicks.setUserSecret(null);
        return supercontestEntryWeekAndPicksRepository.save(weekAndPicks);
    }

    public List<SupercontestEntry> getSeasonLeaderboard() {
        return supercontestEntryRepository.findAllByUsernameIsNotNullOrderBySeasonScoreDesc();
    }

    public List<SupercontestEntryWeek> getWeeklyLeaderboard(Integer weekNumber) {
        return supercontestEntryWeekRepository.findAllByWeekNumberOrderByWeekScoreDesc(weekNumber);
    }

    public SupercontestPoolAndEntries getPoolAndEntries(String poolName) {
        Optional<SupercontestPoolAndEntries> poolAndEntriesOptional =
                supercontestPoolAndEntriesRepository.findById(poolName);
        if (poolAndEntriesOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestPoolAndEntries poolAndEntries = poolAndEntriesOptional.get();
        poolAndEntries.getEntries().forEach(entry -> entry.setUserSecret(null));
        return poolAndEntries;
    }

    public List<SupercontestPool> getAllPools() {
        List<SupercontestPool> pools = supercontestPoolRepository.findAll();
        pools.forEach(pool -> pool.setPassword(null));
        return pools;
    }

    public SupercontestPool createPool(String googleJwt, SupercontestPool pool) {
        Optional<User> creatingUserOptional =
                userRepository.findByUserSecret(JwtUtils.getUserFromJwt(googleJwt).getUserSecret());
        if (creatingUserOptional.isEmpty()) {
            throw new NotFoundException();
        }
        User creatingUser = creatingUserOptional.get();
        pool.setCreatorUsername(creatingUser.getUsername());
        if (pool.getJoinType() == PoolJoinType.PRIVATE &&
                (pool.getPassword() == null || pool.getPassword().isEmpty())) {
            throw new IllegalArgumentException();
        }
        if (supercontestPoolRepository.existsById(pool.getPoolName())) {
            throw new IllegalArgumentException();
        }
        return supercontestPoolRepository.save(pool);
    }

    public SupercontestEntryAndPools joinPool(String googleJwt, String poolName, String password) {
        Optional<SupercontestPool> poolToBeJoinedOptional =
                supercontestPoolRepository.findById(poolName);
        if (poolToBeJoinedOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestPool poolToBeJoined = poolToBeJoinedOptional.get();
        if (poolToBeJoined.getJoinType() == PoolJoinType.PRIVATE && !poolToBeJoined.getPassword().equals(password)) {
            throw new UnauthorizedException();
        }
        User enrollingUser = JwtUtils.getUserFromJwt(googleJwt);
        Optional<SupercontestEntryAndPools> joiningEntryOptional =
                supercontestEntryAndPoolsRepository.findByUserSecret(enrollingUser.getUserSecret());
        if (joiningEntryOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestEntryAndPools joiningEntry = joiningEntryOptional.get();
        joiningEntry.joinPool(poolToBeJoined);
        return supercontestEntryAndPoolsRepository.save(joiningEntry);
    }

    public List<SupercontestEntryWeekAndPicks> gradePicks(String googleJwt) {
        User gradingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!gradingUser.getUserSecret().equals("109251928136244659820")) { // TODO: put as env variable
            throw new UnauthorizedException();
        }
        // score current EntryWeeks
        List<GameLine> scoredGames = gameLineRepository.findAllInCurrentWeek();
        List<SupercontestEntryWeekAndPicks> entryWeeksInCurrentWeek =
                supercontestEntryWeekAndPicksRepository.findAllByWeekNumber(gameLineRepository.findCurrentGameWeek());
        entryWeeksInCurrentWeek.forEach(entryWeek -> {
            entryWeek.setWeekScore(0.0);
            entryWeek.setWeekWins(0);
            entryWeek.setWeekLosses(0);
            entryWeek.setWeekPushes(0);
            entryWeek.getPicks().forEach(pick -> {
                Optional<GameLine> pickedGameOptional = scoredGames.stream().filter(scoredGame ->
                        scoredGame.getId().equals(pick.getGameId())).findAny();
                if (pickedGameOptional.isEmpty()) {
                    throw new NotFoundException();
                }
                GameLine pickedGame = pickedGameOptional.get();
                // game has been played and pick can be graded
                if (pickedGame.getHomeScore() != null) {
                    pick.setHomeTeamScore(pickedGame.getHomeScore());
                    pick.setAwayTeamScore(pickedGame.getAwayScore());
                    Team coveringTeam = pickedGame.calculateCoveringTeam();
                    if (coveringTeam == pick.getPickedTeam()) {
                        pick.setResult(Result.WIN);
                        entryWeek.recordWin();
                    } else if (coveringTeam == null) {
                        pick.setResult(Result.PUSH);
                        entryWeek.recordPush();
                    } else {
                        pick.setResult(Result.LOSS);
                        entryWeek.recordLoss();
                    }
                }
            });
        });
        supercontestEntryWeekAndPicksRepository.saveAll(entryWeeksInCurrentWeek);
        // update season scores
        List<SupercontestEntryAndEntryWeeks> entries = supercontestEntryAndEntryWeeksRepository.findAll();
        entries.forEach(entry -> {
            entry.setSeasonScore(0.0);
            entry.setSeasonWins(0);
            entry.setSeasonLosses(0);
            entry.setSeasonPushes(0);
            entry.getSupercontestEntryWeeks().forEach(entryWeek -> {
                entry.recordWins(entryWeek.getWeekWins());
                entry.recordLosses(entryWeek.getWeekLosses());
                entry.recordPushes(entryWeek.getWeekPushes());
            });
        });
        supercontestEntryAndEntryWeeksRepository.saveAll(entries);
        return entryWeeksInCurrentWeek;
    }
}
