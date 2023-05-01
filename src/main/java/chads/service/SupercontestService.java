package chads.service;

import chads.enums.PoolJoinType;
import chads.enums.Result;
import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.User;
import chads.model.supercontest.*;
import chads.repository.GameLineRepository;
import chads.repository.UserRepository;
import chads.repository.supercontest.*;
import chads.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
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
    private final SupercontestPublicEntryWeekRepository supercontestPublicEntryWeekRepository;
    private final SupercontestPublicPickStatsRepository supercontestPublicPickStatsRepository;
    private final SupercontestHeadToHeadStatsRepository supercontestHeadToHeadStatsRepository;

    @Autowired
    public SupercontestService(UserRepository userRepository,
                               GameLineRepository gameLineRepository,
                               SupercontestEntryRepository supercontestEntryRepository,
                               SupercontestEntryAndEntryWeeksRepository supercontestEntryAndEntryWeeksRepository,
                               SupercontestEntryAndPoolsRepository supercontestEntryAndPoolsRepository,
                               SupercontestEntryWeekRepository supercontestEntryWeekRepository,
                               SupercontestEntryWeekAndPicksRepository supercontestEntryWeekAndPicksRepository,
                               SupercontestPoolRepository supercontestPoolRepository,
                               SupercontestPoolAndEntriesRepository supercontestPoolAndEntriesRepository,
                               SupercontestEntryPickStatsRepository supercontestEntryPickStatsRepository,
                               SupercontestEntryFadeStatsRepository supercontestEntryFadeStatsRepository,
                               SupercontestPublicEntryWeekRepository supercontestPublicEntryWeekRepository,
                               SupercontestPublicPickStatsRepository supercontestPublicPickStatsRepository,
                               SupercontestHeadToHeadStatsRepository supercontestHeadToHeadStatsRepository) {
        this.userRepository = userRepository;
        this.gameLineRepository = gameLineRepository;
        this.supercontestEntryRepository = supercontestEntryRepository;
        this.supercontestEntryAndEntryWeeksRepository = supercontestEntryAndEntryWeeksRepository;
        this.supercontestEntryAndPoolsRepository = supercontestEntryAndPoolsRepository;
        this.supercontestEntryWeekRepository = supercontestEntryWeekRepository;
        this.supercontestEntryWeekAndPicksRepository = supercontestEntryWeekAndPicksRepository;
        this.supercontestPoolRepository = supercontestPoolRepository;
        this.supercontestPoolAndEntriesRepository = supercontestPoolAndEntriesRepository;
        this.supercontestEntryPickStatsRepository = supercontestEntryPickStatsRepository;
        this.supercontestEntryFadeStatsRepository = supercontestEntryFadeStatsRepository;
        this.supercontestPublicEntryWeekRepository = supercontestPublicEntryWeekRepository;
        this.supercontestPublicPickStatsRepository = supercontestPublicPickStatsRepository;
        this.supercontestHeadToHeadStatsRepository = supercontestHeadToHeadStatsRepository;
    }

    @Value("${adminId}")
    private String adminId;

    @Value("${seasonStartTime}")
    private Long seasonStartTime;

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
            SupercontestEntryAndPools noData = new SupercontestEntryAndPools();
            noData.setPools(new HashSet<>());
            return noData;
        }
        SupercontestEntryAndPools entryAndPools = entryAndPoolsOptional.get();
        entryAndPools.setUserSecret(null);
        entryAndPools.getPools().forEach(pool -> pool.setPassword(null));
        return entryAndPools;
    }

    public SupercontestEntryAndEntryWeeks getAllEntryWeeksForUser(String username) {
        SupercontestEntryAndEntryWeeks entryWeekAndPicks =
                supercontestEntryAndEntryWeeksRepository.findByUsername(username);
        if (entryWeekAndPicks == null) {
            SupercontestEntryAndEntryWeeks noData = new SupercontestEntryAndEntryWeeks();
            noData.setSupercontestEntryWeeks(new ArrayList<>());
            return noData;
        }
        return entryWeekAndPicks;
    }

    public List<SupercontestEntryPickStats> getEntryPickStats(String username) {
        List<SupercontestEntryPickStats> stats = supercontestEntryPickStatsRepository.findAllByUsername(username);
        stats.forEach(this::setNullsToZero);
        return stats;
    }

    public List<SupercontestEntryFadeStats> getEntryFadeStats(String username) {
        List<SupercontestEntryFadeStats> stats = supercontestEntryFadeStatsRepository.findAllByUsername(username);
        stats.forEach(this::setNullsToZero);
        return stats;
    }

    public List<SupercontestPublicEntryWeek> getPublicEntryWeeks() {
        return supercontestPublicEntryWeekRepository.getPublicEntryWeeks();
    }

    public List<SupercontestPublicPickStats> getPublicPicksByWeekNumber(Integer weekNumber) {
        return supercontestPublicPickStatsRepository.findAllByWeekNumber(weekNumber);
    }

    public List<SupercontestPublicPickStats> getMostPopularPicksOfSeason() {
        return supercontestPublicPickStatsRepository.findMostPopularPicksOfSeason();
    }

    public List<SupercontestHeadToHeadStats> getHeadToHeadStats(String username1, String username2) {
        List<SupercontestHeadToHeadStats> headToHeadStats =
                supercontestHeadToHeadStatsRepository.getHeadToHeadStats(username1, username2);
        return headToHeadStats.stream().filter(pick ->
                pick.getTimestamp() <= Instant.now().toEpochMilli()).collect(Collectors.toList());
    }

    public SupercontestEntryWeekAndPicks getEntryWeekAndPicks(String googleJwt, String username, Integer weekNumber) {
        Optional<SupercontestEntryWeekAndPicks> weekAndPicksOptional =
                supercontestEntryWeekAndPicksRepository.findByUsernameAndWeekNumber(username, weekNumber);
        if (weekAndPicksOptional.isEmpty()) {
            SupercontestEntryWeekAndPicks noData = new SupercontestEntryWeekAndPicks();
            noData.setPicks(new ArrayList<>());
            return noData;
        }
        SupercontestEntryWeekAndPicks weekAndPicks = weekAndPicksOptional.get();
        weekAndPicks.getPicks().sort(Comparator.comparingInt(SupercontestPick::getGameId));
        User viewingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!weekAndPicks.getUserSecret().equals(viewingUser.getUserSecret())) {
            // hide picks that haven't started yet
            weekAndPicks.getPicks().forEach(pick -> {
                if (pick.getTimestamp() > Instant.now().toEpochMilli()) {
                    pick.setGameId(null);
                    pick.setTimestamp(null);
                    pick.setPickedTeam(null);
                    pick.setOpposingTeam(null);
                    pick.setHomeTeam(null);
                    pick.setAwayTeam(null);
                    pick.setHomeSpread(null);
                }
            });
        }
        weekAndPicks.setUserSecret(null);
        return weekAndPicks;
    }

    // picks only need gameId and pickedTeam
    public SupercontestEntryWeekAndPicks submitPicks(
            String googleJwt, List<SupercontestPick> newPicks) {
        if (newPicks.size() > 5) {
            throw new IllegalArgumentException();
        }
        User requestingUser = JwtUtils.getUserFromJwt(googleJwt);
        int currentWeekNumber = gameLineRepository.findCurrentWeekNumber();
        Optional<SupercontestEntryWeekAndPicks> weekAndPicksOptional =
                supercontestEntryWeekAndPicksRepository.findByUserSecretAndWeekNumber(
                        requestingUser.getUserSecret(), currentWeekNumber);
        if (weekAndPicksOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestEntryWeekAndPicks weekAndPicks = weekAndPicksOptional.get();
        // any previously submitted pick for a game that has started must be in the new picks
        weekAndPicks.getPicks().forEach(existingPick -> {
            if (existingPick.getTimestamp() <= Instant.now().toEpochMilli() &&
                    (newPicks.stream().noneMatch(newPick ->
                        newPick.getPickedTeam() == existingPick.getPickedTeam()))) {
                    throw new IllegalArgumentException();
                }
        });
        List<GameLine> officialGameLines = gameLineRepository.findAllInCurrentWeek();
        newPicks.forEach(newPick -> {
            Optional<GameLine> pickedGameOptional =
                    officialGameLines.stream().filter(gameLine ->
                            gameLine.getGameId().equals(newPick.getGameId())).findAny();
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
            if (newPick.getPickedTeam() == pickedGame.getHomeTeam()) {
                newPick.setOpposingTeam(pickedGame.getAwayTeam());
            } else {
                newPick.setOpposingTeam(pickedGame.getHomeTeam());
            }
            newPick.setHomeTeam(pickedGame.getHomeTeam());
            newPick.setAwayTeam(pickedGame.getAwayTeam());
            newPick.setHomeSpread(pickedGame.getHomeSpread());
            // if game has been played, re-set scores and result since the pick is recreated as a new record
            if (pickedGame.getHomeScore() != null) {
                newPick.setHomeScore(pickedGame.getHomeScore());
                newPick.setAwayScore(pickedGame.getAwayScore());
                newPick.setResult(newPick.calculateResult());
            }
        });
        weekAndPicks.updatePicks(newPicks);
        weekAndPicks.setHasMadePicks(true);
        // TODO: remove user secret from this and below methods
        return supercontestEntryWeekAndPicksRepository.save(weekAndPicks);
    }

    public List<SupercontestEntry> getSeasonLeaderboard() {
        return supercontestEntryRepository.findAllByUsernameIsNotNullOrderBySeasonScoreDesc();
    }

    public List<SupercontestEntryWeek> getWeeklyLeaderboard(Integer weekNumber) {
        return supercontestEntryWeekRepository.findAllByWeekNumberOrderByWeekScoreDesc(weekNumber);
    }

    public List<SupercontestEntryWeekAndPicks> getBestPicksOfTheWeek(Integer weekNumber) {
        if (weekNumber < 1) {
            throw new IllegalArgumentException();
        }
        return supercontestEntryWeekAndPicksRepository.getBestPicksOfTheWeek(weekNumber);
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
        // max buy in is 100
        if (pool.getBuyIn() > 100) {
            throw new IllegalArgumentException();
        }
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
        supercontestPoolRepository.save(pool);
        // now join pool
        Optional<SupercontestEntryAndPools> creatingEntryOptional =
                supercontestEntryAndPoolsRepository.findByUserSecret(creatingUser.getUserSecret());
        if (creatingEntryOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SupercontestEntryAndPools creatingEntry = creatingEntryOptional.get();
        creatingEntry.joinPool(pool);
        supercontestEntryAndPoolsRepository.save(creatingEntry);
        return pool;
    }

    public SupercontestEntryAndPools joinPool(String googleJwt, String poolName, String password) {
        if (Instant.now().toEpochMilli() > seasonStartTime) {
            throw new UnauthorizedException();
        }
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
        if (!gradingUser.getUserSecret().equals(adminId)) {
            throw new UnauthorizedException();
        }
        // score current EntryWeeks
        List<GameLine> scoredGames = gameLineRepository.findAllInCurrentWeek();
        List<SupercontestEntryWeekAndPicks> entryWeeksInCurrentWeek =
                supercontestEntryWeekAndPicksRepository.findAllByWeekNumber(gameLineRepository.findCurrentWeekNumber());
        entryWeeksInCurrentWeek.forEach(entryWeek -> {
            entryWeek.setWeekScore(0.0);
            entryWeek.setWeekWins(0);
            entryWeek.setWeekLosses(0);
            entryWeek.setWeekPushes(0);
            entryWeek.getPicks().forEach(pick -> {
                Optional<GameLine> pickedGameOptional = scoredGames.stream().filter(scoredGame ->
                        scoredGame.getGameId().equals(pick.getGameId())).findAny();
                if (pickedGameOptional.isEmpty()) {
                    throw new NotFoundException();
                }
                GameLine pickedGame = pickedGameOptional.get();
                // game has been played and pick can be graded
                if (pickedGame.getHomeScore() != null) {
                    pick.setHomeScore(pickedGame.getHomeScore());
                    pick.setAwayScore(pickedGame.getAwayScore());
                    pick.setResult(pick.calculateResult());
                    if (pick.getResult() == Result.WIN) {
                        entryWeek.recordWin();
                    } else if (pick.getResult() == Result.LOSS) {
                        entryWeek.recordLoss();
                    } else if (pick.getResult() == Result.PUSH) {
                        entryWeek.recordPush();
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

    private void setNullsToZero(SupercontestEntryPickStats team) {
        if (team.getWins() == null) {
            team.setWins(0);
        }
        if (team.getLosses() == null) {
            team.setLosses(0);
        }
        if (team.getPushes() == null) {
            team.setPushes(0);
        }
    }

    private void setNullsToZero(SupercontestEntryFadeStats team) {
        if (team.getWins() == null) {
            team.setWins(0);
        }
        if (team.getLosses() == null) {
            team.setLosses(0);
        }
        if (team.getPushes() == null) {
            team.setPushes(0);
        }
    }
}
