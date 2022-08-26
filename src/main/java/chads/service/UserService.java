package chads.service;

import chads.exception.NotFoundException;
import chads.model.User;
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

    public User signUpOrSignIn(String googleJwt) {
        if (googleJwt.isEmpty()) {
            throw new IllegalArgumentException();
        }
        User userInfo = JwtUtils.getUserFromJwt(googleJwt);
        if (!userRepository.existsById(userInfo.getUsername())) {
            User user = userRepository.save(userInfo);
            supercontestService.createEntry(googleJwt);
            sportsbookService.createAccount(googleJwt);
            // also create eliminator?
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
}
