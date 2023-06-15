package chads.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @Column
    private String username;

    @Column(name = "user_secret")
    private String userSecret;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "opt_in_new_games_notification")
    private Boolean optInNewGamesNotification;

    @Column(name = "opt_in_missing_picks_notification")
    private Boolean optInMissingPicksNotification;
}
