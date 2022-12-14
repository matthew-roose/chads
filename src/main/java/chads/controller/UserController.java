package chads.controller;

import chads.exception.NotFoundException;
import chads.model.User;
import chads.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class UserController {

    private final UserService userService;

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
}
