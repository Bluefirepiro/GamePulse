package com.scottparrillo.gamepulse

import java.sql.Time
import kotlin.reflect.typeOf

//Making some enums for different notifications
enum class NotificationType {
    achievementEarned,
    playGameReminder,
    friendShared,
    friendRequest,
}

class Notification {
    //If we have a global list of messages we can pull from or inherit that might make more sense
    var notificationMessages: ArrayList<String>? = null
    var notificationType: NotificationType? = null

    //If we need to keep track of time then I'm just gonna pull from the time class
    var time: Time? = null
    val textNotifications = listOf<String>("Hey don't forget to spend some time gaming",
        "Time to stop what you're doing and game", "Don't forget to logon and have some fun",
        "")
}
