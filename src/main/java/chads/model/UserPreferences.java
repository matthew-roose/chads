package chads.model;

import lombok.Data;

@Data
public class UserPreferences {
    String phoneNumber;
    Boolean optInNewGamesNotification;
    Boolean optInMissingPicksNotification;
}
