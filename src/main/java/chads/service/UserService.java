package chads.service;

import chads.exception.NotFoundException;
import chads.model.User;
import chads.model.UserPreferences;
import chads.repository.UserRepository;
import chads.util.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UserService {

    private final UserRepository userRepository;
    private final SupercontestService supercontestService;
    private final SportsbookService sportsbookService;
    private final SurvivorService survivorService;

    public User signUpOrSignIn(String googleJwt) {
        if (googleJwt.isEmpty()) {
            throw new IllegalArgumentException();
        }
        User userInfo = JwtUtils.getUserFromJwt(googleJwt);
        if (!userRepository.existsById(userInfo.getUsername())) {
            User user = userRepository.save(userInfo);
            supercontestService.createEntry(googleJwt);
            sportsbookService.createAccount(googleJwt);
            survivorService.createEntry(googleJwt);
            return user;
        }
        Optional<User> existingUser = userRepository.findByUserSecret(userInfo.getUserSecret());
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        throw new NotFoundException();
    }

    public List<String> getAllUsernames() {
        return userRepository.findAll().stream().map(User::getUsername).sorted().collect(Collectors.toList());
    }

    public User getUserPreferences(String googleJwt) {
        User userInfo = JwtUtils.getUserFromJwt(googleJwt);
        Optional<User> userPreferencesOptional = userRepository.findByUserSecret(userInfo.getUserSecret());
        if (userPreferencesOptional.isEmpty()) {
            throw new NotFoundException();
        }
        return userPreferencesOptional.get();
    }

    public User saveUserPreferences(String googleJwt, UserPreferences userPreferences) {
        User userInfo = JwtUtils.getUserFromJwt(googleJwt);
        Optional<User> userPreferencesOptional = userRepository.findByUserSecret(userInfo.getUserSecret());
        if (userPreferencesOptional.isEmpty()) {
            throw new NotFoundException();
        }
        User preferencesToUpdate = userPreferencesOptional.get();
        preferencesToUpdate.setPhoneNumber(userPreferences.getPhoneNumber());
        preferencesToUpdate.setCarrier(userPreferences.getCarrier());
        preferencesToUpdate.setOptInNewGamesNotification(
                userPreferences.getOptInNewGamesNotification());
        preferencesToUpdate.setOptInMissingPicksNotification(
                userPreferences.getOptInMissingPicksNotification());
        return userRepository.save(preferencesToUpdate);
    }
}
