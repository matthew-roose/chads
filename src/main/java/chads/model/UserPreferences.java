package chads.model;

import lombok.Data;

@Data
public class UserPreferences {
    String phoneNumber;
    String carrier;
    Boolean optInNewGamesNotification;
    Boolean optInMissingPicksNotification;
}
