
package com.scottparrillo.gamepulse

data class User(
    val name: String,
    val email: String,
    val gamesOwned: List<Game>,
    val achievements: List<Achievement>,
    val friends: List<User>,
    val profilePictureUrl: String? = null // Optional profile picture
) {
    fun totalGamesOwned(): Int {
        return gamesOwned.size
    }

    fun totalAchievements(): Int {
        return achievements.size
    }

    fun totalFriends(): Int {
        return friends.size
    }
}



