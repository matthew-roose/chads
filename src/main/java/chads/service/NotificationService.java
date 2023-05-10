package chads.service;

import chads.repository.GameLineRepository;
import chads.repository.UserRepository;
import chads.repository.supercontest.SupercontestEntryWeekAndPicksRepository;
import chads.repository.survivor.SurvivorPickRepository;
import com.twilio.Twilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final static String TWILIO_PHONE_NUMBER = "+1-Twilio-Number";
    private final static String NEW_GAMES_MESSAGE = "This week's games are now available at https://chads.be.";
    private final static String MISSING_SUPERCONTEST_PICKS_MESSAGE =
            "Friendly reminder to make your 5 Supercontest picks at https://chads.be";
    private final static String MISSING_SURVIVOR_PICKS_MESSAGE =
            "Friendly reminder to make your Survivor pick at https://chads.be";

    private final UserRepository userRepository;
    private final GameLineRepository gameLineRepository;
    private final SupercontestEntryWeekAndPicksRepository supercontestEntryWeekAndPicksRepository;
    private final SurvivorPickRepository survivorPickRepository;

    @Value("${adminId}")
    private String adminId;

    @Autowired
    public NotificationService(UserRepository userRepository,
                               GameLineRepository gameLineRepository,
                               SupercontestEntryWeekAndPicksRepository supercontestEntryWeekAndPicksRepository,
                               SurvivorPickRepository survivorPickRepository) {
        this.userRepository = userRepository;
        this.gameLineRepository = gameLineRepository;
        this.supercontestEntryWeekAndPicksRepository = supercontestEntryWeekAndPicksRepository;
        this.survivorPickRepository = survivorPickRepository;
        Twilio.init("ACCOUNT_SID", "AUTH_TOKEN");
    }

    public void sendNewGamesNotification(String googleJwt) {
//        User notifyingUser = JwtUtils.getUserFromJwt(googleJwt);
//        if (!notifyingUser.getUserSecret().equals(adminId)) {
//            throw new UnauthorizedException();
//        }
//        List<User> usersToNotify = userRepository.findAllByOptInNewGamesNotificationIsTrue();
//        usersToNotify.forEach(user -> {
//            String userPhoneNumber = "+1".concat(user.getPhoneNumber().replaceAll("-", ""));
//            Message.creator(
//                    new com.twilio.type.PhoneNumber(userPhoneNumber),
//                    new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
//                            NEW_GAMES_MESSAGE)
//                .create();
//        });
    }

    public void sendMissingPicksNotification(String googleJwt) {
//        User notifyingUser = JwtUtils.getUserFromJwt(googleJwt);
//        if (!notifyingUser.getUserSecret().equals(adminId)) {
//            throw new UnauthorizedException();
//        }
//        List<User> usersToNotify = userRepository.findAllByOptInMissingPicksNotificationIsTrue();
//        Integer currentWeekNumber = gameLineRepository.findCurrentWeekNumber();
//        usersToNotify.forEach(user -> {
//            String userPhoneNumber = "+1".concat(user.getPhoneNumber().replaceAll("-", ""));
//            // check for missing Supercontest picks
//            Optional<SupercontestEntryWeekAndPicks> currentWeekAndPicksOptional =
//                    supercontestEntryWeekAndPicksRepository.findByUsernameAndWeekNumber(
//                            user.getUsername(), currentWeekNumber);
//            if (currentWeekAndPicksOptional.isPresent()) {
//                SupercontestEntryWeekAndPicks currentWeekAndPicks = currentWeekAndPicksOptional.get();
//                if (currentWeekAndPicks.getPicks().isEmpty()) {
//                    Message.creator(
//                            new com.twilio.type.PhoneNumber(userPhoneNumber),
//                            new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
//                                    MISSING_SUPERCONTEST_PICKS_MESSAGE)
//                        .create();
//                }
//            }
//            // check for missing Survivor pick
//            SurvivorPick currentWeekPick =
//                    survivorPickRepository.findByUserSecretAndWeekNumber(user.getUserSecret(), currentWeekNumber);
//            if (currentWeekPick == null) {
//                Message.creator(
//                        new com.twilio.type.PhoneNumber(userPhoneNumber),
//                        new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
//                                MISSING_SURVIVOR_PICKS_MESSAGE)
//                    .create();
//            }
//        });
    }
}
