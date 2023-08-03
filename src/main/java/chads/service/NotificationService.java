package chads.service;

import chads.exception.UnauthorizedException;
import chads.model.User;
import chads.model.supercontest.SupercontestEntryWeekAndPicks;
import chads.model.survivor.SurvivorPick;
import chads.repository.GameLineRepository;
import chads.repository.UserRepository;
import chads.repository.supercontest.SupercontestEntryWeekAndPicksRepository;
import chads.repository.survivor.SurvivorPickRepository;
import chads.util.JwtUtils;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final static String NEW_GAMES_MESSAGE =
            "This week's games are now available at https://chads.be!";
    private final static String MISSING_SUPERCONTEST_PICKS_MESSAGE =
            "Friendly reminder to make your 5 Supercontest picks at https://chads.be/supercontest/make-picks!";
    private final static String MISSING_SURVIVOR_PICKS_MESSAGE =
            "Friendly reminder to make your Survivor pick at https://chads.be/survivor/make-picks!";

    private final UserRepository userRepository;
    private final GameLineRepository gameLineRepository;
    private final SupercontestEntryWeekAndPicksRepository supercontestEntryWeekAndPicksRepository;
    private final SurvivorPickRepository survivorPickRepository;

    @Value("${adminId}")
    private String adminId;

    private final SendGrid sendGrid;

    @Autowired
    public NotificationService(UserRepository userRepository,
                               GameLineRepository gameLineRepository,
                               SupercontestEntryWeekAndPicksRepository supercontestEntryWeekAndPicksRepository,
                               SurvivorPickRepository survivorPickRepository) {
        this.userRepository = userRepository;
        this.gameLineRepository = gameLineRepository;
        this.supercontestEntryWeekAndPicksRepository = supercontestEntryWeekAndPicksRepository;
        this.survivorPickRepository = survivorPickRepository;
        sendGrid = new SendGrid("sendGridKey");
    }

    public void sendNewGamesNotification(String googleJwt) {
        User notifyingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!notifyingUser.getUserSecret().equals(adminId)) {
            throw new UnauthorizedException();
        }
        List<User> usersToNotify = userRepository.findAllByOptInNewGamesNotificationIsTrue();
        Email from = new Email("admin@em7991.chads.be");
        String subject = "Chad's";
        Content content = new Content("text/plain", NEW_GAMES_MESSAGE);
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.addContent(content);
        Personalization recipients = new Personalization();
        Request request = new Request();
        usersToNotify.forEach(user -> {
            Email to = new Email(user.getPhoneNumber().replaceAll("\\D", "")
                    + "@" + getCarrierDomain(user.getCarrier()));
            recipients.addTo(to);
        });

        if (!recipients.getTos().isEmpty()) {
            mail.addPersonalization(recipients);
            try {
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());
                sendGrid.api(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMissingPicksNotification(String googleJwt) {
        User notifyingUser = JwtUtils.getUserFromJwt(googleJwt);
        if (!notifyingUser.getUserSecret().equals(adminId)) {
            throw new UnauthorizedException();
        }
        List<User> usersToNotify = userRepository.findAllByOptInMissingPicksNotificationIsTrue();
        Integer currentWeekNumber = gameLineRepository.findCurrentWeekNumber();
        Email from = new Email("admin@em7991.chads.be");
        String subject = "Chad's";

        Content supercontestContent = new Content("text/plain", MISSING_SUPERCONTEST_PICKS_MESSAGE);
        Mail supercontestMail = new Mail();
        supercontestMail.setFrom(from);
        supercontestMail.setSubject(subject);
        supercontestMail.addContent(supercontestContent);
        Personalization supercontestRecipients = new Personalization();

        Content survivorContent = new Content("text/plain", MISSING_SURVIVOR_PICKS_MESSAGE);
        Mail survivorMail = new Mail();
        survivorMail.setFrom(from);
        survivorMail.setSubject(subject);
        survivorMail.addContent(survivorContent);
        Personalization survivorRecipients = new Personalization();

        usersToNotify.forEach(user -> {
            // check for missing Supercontest picks
            Optional<SupercontestEntryWeekAndPicks> currentWeekAndPicksOptional =
                    supercontestEntryWeekAndPicksRepository.findByUsernameAndWeekNumber(
                            user.getUsername(), currentWeekNumber);
            if (currentWeekAndPicksOptional.isPresent()) {
                SupercontestEntryWeekAndPicks currentWeekAndPicks = currentWeekAndPicksOptional.get();
                if (currentWeekAndPicks.getPicks().isEmpty()) {
                    Email to = new Email(user.getPhoneNumber().replaceAll("\\D", "")
                            + "@" + getCarrierDomain(user.getCarrier()));
                    supercontestRecipients.addTo(to);
                }
            }
            // check for missing Survivor pick
            SurvivorPick currentWeekPick =
                    survivorPickRepository.findByUserSecretAndWeekNumber(user.getUserSecret(), currentWeekNumber);
            if (currentWeekPick == null) {
                Email to = new Email(user.getPhoneNumber().replaceAll("\\D", "")
                        + "@" + getCarrierDomain(user.getCarrier()));
                survivorRecipients.addTo(to);
            }
        });

        try {
            if (!supercontestRecipients.getTos().isEmpty()) {
                supercontestMail.addPersonalization(supercontestRecipients);
                Request supercontestRequest = new Request();
                supercontestRequest.setMethod(Method.POST);
                supercontestRequest.setEndpoint("mail/send");
                supercontestRequest.setBody(supercontestMail.build());
                sendGrid.api(supercontestRequest);
            }
            if (!survivorRecipients.getTos().isEmpty()) {
                survivorMail.addPersonalization(survivorRecipients);
                Request survivorRequest = new Request();
                survivorRequest.setMethod(Method.POST);
                survivorRequest.setEndpoint("mail/send");
                survivorRequest.setBody(survivorMail.build());
                sendGrid.api(survivorRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCarrierDomain(String carrier) {
        if (carrier.equals("Verizon")) {
            return "vtext.com";
        } if (carrier.equals("AT&T")) {
            return "txt.att.net";
        } if (carrier.equals("T-Mobile")) {
            return "tmomail.net";
        } if (carrier.equals("Sprint")) {
            return "messaging.sprintpcs.com";
        }
        throw new IllegalArgumentException();
    }
}
