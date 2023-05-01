package chads.service;

import chads.enums.PoolJoinType;
import chads.enums.Result;
import chads.enums.Team;
import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.GameLine;
import chads.model.User;
import chads.model.survivor.SurvivorEntry;
import chads.model.survivor.SurvivorEntryAndPicks;
import chads.model.survivor.SurvivorEntryAndPools;
import chads.model.survivor.SurvivorPick;
import chads.model.survivor.SurvivorPool;
import chads.model.survivor.SurvivorPoolAndEntries;
import chads.model.survivor.SurvivorPublicPickStats;
import chads.repository.GameLineRepository;
import chads.repository.UserRepository;
import chads.repository.survivor.SurvivorEntryAndPicksRepository;
import chads.repository.survivor.SurvivorEntryAndPoolsRepository;
import chads.repository.survivor.SurvivorEntryRepository;
import chads.repository.survivor.SurvivorPickRepository;
import chads.repository.survivor.SurvivorPoolAndEntriesRepository;
import chads.repository.survivor.SurvivorPoolRepository;
import chads.repository.survivor.SurvivorPublicPickStatsRepository;
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
public class SurvivorService {

    private final UserRepository userRepository;
    private final GameLineRepository gameLineRepository;
    private final SurvivorEntryRepository survivorEntryRepository;
    private final SurvivorEntryAndPicksRepository survivorEntryAndPicksRepository;
    private final SurvivorEntryAndPoolsRepository survivorEntryAndPoolsRepository;
    private final SurvivorPickRepository survivorPickRepository;
    private final SurvivorPoolRepository survivorPoolRepository;
    private final SurvivorPoolAndEntriesRepository survivorPoolAndEntriesRepository;
    private final SurvivorPublicPickStatsRepository survivorPublicPickStatsRepository;

    @Autowired
    public SurvivorService(UserRepository userRepository,
                           GameLineRepository gameLineRepository,
                           SurvivorEntryRepository survivorEntryRepository,
                           SurvivorEntryAndPicksRepository survivorEntryAndPicksRepository,
                           SurvivorEntryAndPoolsRepository survivorEntryAndPoolsRepository,
                           SurvivorPickRepository survivorPickRepository,
                           SurvivorPoolRepository survivorPoolRepository,
                           SurvivorPoolAndEntriesRepository survivorPoolAndEntriesRepository,
                           SurvivorPublicPickStatsRepository survivorPublicPickStatsRepository) {
        this.userRepository = userRepository;
        this.gameLineRepository = gameLineRepository;
        this.survivorEntryRepository = survivorEntryRepository;
        this.survivorEntryAndPicksRepository = survivorEntryAndPicksRepository;
        this.survivorEntryAndPoolsRepository = survivorEntryAndPoolsRepository;
        this.survivorPickRepository = survivorPickRepository;
        this.survivorPoolRepository = survivorPoolRepository;
        this.survivorPoolAndEntriesRepository = survivorPoolAndEntriesRepository;
        this.survivorPublicPickStatsRepository = survivorPublicPickStatsRepository;
    }

    @Value("${adminId}")
    private String adminId;

    @Value("${seasonStartTime}")
    private Long seasonStartTime;

    public void createEntry(String googleJwt) {
        User creatingUser = JwtUtils.getUserFromJwt(googleJwt);
        SurvivorEntry newEntry = new SurvivorEntry(creatingUser.getUsername(), creatingUser.getUserSecret(),
                0.0, 0, 0, 0, 0);
        survivorEntryRepository.save(newEntry);
    }

    public SurvivorEntryAndPools getEntryAndPools(String username) {
        Optional<SurvivorEntryAndPools> entryAndPoolsOptional =
                survivorEntryAndPoolsRepository.findById(username);
        if (entryAndPoolsOptional.isEmpty()) {
            SurvivorEntryAndPools noData = new SurvivorEntryAndPools();
            noData.setPools(new HashSet<>());
            return noData;        }
        SurvivorEntryAndPools entryAndPools = entryAndPoolsOptional.get();
        entryAndPools.setUserSecret(null);
        entryAndPools.getPools().forEach(pool -> pool.setPassword(null));
        return entryAndPools;
    }

    public SurvivorEntryAndPicks getEntryAndPicks(String googleJwt, String username) {
        Optional<SurvivorEntryAndPicks> entryAndPicksOptional =
                survivorEntryAndPicksRepository.findById(username);
        if (entryAndPicksOptional.isEmpty()) {
            SurvivorEntryAndPicks noData = new SurvivorEntryAndPicks();
            noData.setPicks(new ArrayList<>());
            return noData;
        }
        SurvivorEntryAndPicks entry = entryAndPicksOptional.get();
        entry.getPicks().sort(Comparator.comparingInt(SurvivorPick::getWeekNumber));
        entry.obscurePicksForOtherViewers(googleJwt);
        return entry;
    }

    public List<SurvivorEntryAndPicks> getLeaderboard(String googleJwt) {
        List<SurvivorEntryAndPicks> entries = survivorEntryAndPicksRepository.findAll();
        entries.forEach(entry -> entry.obscurePicksForOtherViewers(googleJwt));
        return entries;
    }

    public List<SurvivorPublicPickStats> getPublicPicksByWeekNumber(Integer weekNumber) {
        return survivorPublicPickStatsRepository.findAllByWeekNumber(weekNumber);
    }

    public List<SurvivorPublicPickStats> getMostPopularPicksOfSeason() {
        return survivorPublicPickStatsRepository.findMostPopularPicksOfSeason();
    }

    public SurvivorPick submitPick(String googleJwt, SurvivorPick newPick) {
        User requestingUser = JwtUtils.getUserFromJwt(googleJwt);
        List<Team> previouslyPickedTeams =
                survivorPickRepository.findAllByUserSecret(requestingUser.getUserSecret())
                        .stream().map(SurvivorPick::getPickedTeam).collect(Collectors.toList());
        if (previouslyPickedTeams.contains(newPick.getPickedTeam())) {
            throw new IllegalArgumentException();
        }
        List<GameLine> officialGameLines = gameLineRepository.findAllInCurrentWeek();
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
        Integer currentWeekNumber = officialGameLines.get(0).getWeekNumber();
        SurvivorPick existingPick = survivorPickRepository.findByUserSecretAndWeekNumber(
                requestingUser.getUserSecret(), currentWeekNumber);
        SurvivorPick pickToSave;
        if (existingPick != null) {
            if (existingPick.getTimestamp() <= Instant.now().toEpochMilli()) {
                // previously picked game has started and can't be changed
                throw new IllegalArgumentException();
            }
            pickToSave = existingPick;
        } else {
            pickToSave = new SurvivorPick();
        }
            pickToSave.setUsername(requestingUser.getUsername());
            pickToSave.setUserSecret(requestingUser.getUserSecret());
            pickToSave.setGameId(pickedGame.getGameId());
            pickToSave.setWeekNumber(pickedGame.getWeekNumber());
            pickToSave.setTimestamp(pickedGame.getTimestamp());
            pickToSave.setPickedTeam(newPick.getPickedTeam());
            if (newPick.getPickedTeam() == pickedGame.getHomeTeam()) {
                pickToSave.setOpposingTeam(pickedGame.getAwayTeam());
            } else {
                pickToSave.setOpposingTeam(pickedGame.getHomeTeam());
            }
            pickToSave.setHomeTeam(pickedGame.getHomeTeam());
            pickToSave.setAwayTeam(pickedGame.getAwayTeam());
        return survivorPickRepository.save(pickToSave);
    }

    public List<SurvivorPick> getLosersByWeek(Integer weekNumber) {
        List<SurvivorPick> losingPicks = survivorPickRepository.findAllByWeekNumberAndResult(weekNumber, Result.LOSS);
        losingPicks.forEach(pick -> pick.setUserSecret(null));
        return losingPicks;
    }

    public SurvivorPoolAndEntries getPoolAndEntries(String googleJwt, String poolName) {
        Optional<SurvivorPoolAndEntries> poolAndEntriesOptional =
                survivorPoolAndEntriesRepository.findById(poolName);
        if (poolAndEntriesOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SurvivorPoolAndEntries poolAndEntries = poolAndEntriesOptional.get();
        poolAndEntries.getEntries().forEach(entry -> {
            entry.obscurePicksForOtherViewers(googleJwt);
            entry.setUserSecret(null);
        });
        return poolAndEntries;
    }

    public List<SurvivorPool> getAllPools() {
        List<SurvivorPool> pools = survivorPoolRepository.findAll();
        pools.forEach(pool -> pool.setPassword(null));
        return pools;
    }

    public SurvivorPool createPool(String googleJwt, SurvivorPool pool) {
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
        if (survivorPoolRepository.existsById(pool.getPoolName())) {
            throw new IllegalArgumentException();
        }
        survivorPoolRepository.save(pool);
        // now join pool
        Optional<SurvivorEntryAndPools> creatingEntryOptional =
                survivorEntryAndPoolsRepository.findByUserSecret(creatingUser.getUserSecret());
        if (creatingEntryOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SurvivorEntryAndPools creatingEntry = creatingEntryOptional.get();
        creatingEntry.joinPool(pool);
        survivorEntryAndPoolsRepository.save(creatingEntry);
        return pool;
    }

    public SurvivorEntryAndPools joinPool(String googleJwt, String poolName, String password) {
        if (Instant.now().toEpochMilli() > seasonStartTime) {
            throw new UnauthorizedException();
        }
        Optional<SurvivorPool> poolToBeJoinedOptional =
                survivorPoolRepository.findById(poolName);
        if (poolToBeJoinedOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SurvivorPool poolToBeJoined = poolToBeJoinedOptional.get();
        if (poolToBeJoined.getJoinType() == PoolJoinType.PRIVATE && !poolToBeJoined.getPassword().equals(password)) {
            throw new UnauthorizedException();
        }
        User enrollingUser = JwtUtils.getUserFromJwt(googleJwt);
        Optional<SurvivorEntryAndPools> joiningEntryOptional =
                survivorEntryAndPoolsRepository.findByUserSecret(enrollingUser.getUserSecret());
        if (joiningEntryOptional.isEmpty()) {
            throw new NotFoundException();
        }
        SurvivorEntryAndPools joiningEntry = joiningEntryOptional.get();
        joiningEntry.joinPool(poolToBeJoined);
        return survivorEntryAndPoolsRepository.save(joiningEntry);
    }

    public List<SurvivorEntryAndPicks> gradePicks(String googleJwt) {
        User gradingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!gradingUser.getUserSecret().equals(adminId)) {
            throw new UnauthorizedException();
        }
        List<GameLine> scoredGames = gameLineRepository.findAll();
        List<SurvivorEntryAndPicks> entries =
                survivorEntryAndPicksRepository.findAll();
        entries.forEach(entry -> {
            entry.setScore(0.0);
            entry.setWins(0);
            entry.setLosses(0);
            entry.setPushes(0);
            entry.setCurrentStreak(0);
            entry.getPicks().forEach(pick -> {
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
                        entry.recordWin();
                    } else if (pick.getResult() == Result.LOSS) {
                        entry.recordLoss();
                    } else if (pick.getResult() == Result.PUSH ){
                        entry.recordPush();
                    }
                }
            });
        });
        return survivorEntryAndPicksRepository.saveAll(entries);
    }
}
