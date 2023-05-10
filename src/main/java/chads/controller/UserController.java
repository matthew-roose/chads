package chads.controller;

import chads.exception.NotFoundException;
import chads.exception.UnauthorizedException;
import chads.model.User;
import chads.model.UserPreferences;
import chads.service.NotificationService;
import chads.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;

    @PutMapping("/login")
    public ResponseEntity<User> login(@RequestHeader("Authorization") String googleJwt) {
        try {
            User user = userService.signUpOrSignIn(googleJwt);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/all-usernames")
    public ResponseEntity<List<String>> getAllUsernames() {
        try {
            List<String> allUsernames = userService.getAllUsernames();
            return new ResponseEntity<>(allUsernames, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user-preferences")
    public ResponseEntity<User> getUserPreferences(@RequestHeader("Authorization") String googleJwt) {
        try {
            User userPreferences = userService.getUserPreferences(googleJwt);
            return new ResponseEntity<>(userPreferences, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/user-preferences")
    public ResponseEntity<User> saveUserPreferences(
            @RequestHeader("Authorization") String googleJwt,
            @RequestBody UserPreferences userPreferences) {
        try {
            User updatedUserPreferences = userService.saveUserPreferences(googleJwt, userPreferences);
            return new ResponseEntity<>(updatedUserPreferences, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/notify-users-of-missing-picks")
    public HttpStatus notifyUsersOfMissingPicks(@RequestHeader("Authorization") String googleJwt) {
        try {
            notificationService.sendMissingPicksNotification(googleJwt);
            return HttpStatus.OK;
        } catch (UnauthorizedException e) {
            return HttpStatus.FORBIDDEN;
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
