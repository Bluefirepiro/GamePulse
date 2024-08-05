package com.scottparrillo.gamepulse;
import java.sql.Time;
import java.util.ArrayList;
//Making some enums for different notifications
enum NotificationType {
    achievementEarned,
    playGameReminder,
    friendShared,
    friendRequest,
}
public class Notification {
    //If we have a global list of messages we can pull from or inherit that might make more sense
    ArrayList<String> notificationMessages;
    NotificationType notificationType;
    //If we need to keep track of time then I'm just gonna pull from the time class
    Time time;



}
