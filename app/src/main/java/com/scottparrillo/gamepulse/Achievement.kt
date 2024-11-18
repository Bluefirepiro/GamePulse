package com.scottparrillo.gamepulse

import java.io.Serializable

class Achievement : Serializable{
    //val iconResId: Int = 0
    var title: String = ""
    var description: String = ""
    var percentageEarned: Double = 0.0
    var isEarned: Boolean = false
    var progress: Int = 0
    val total: Int = 0
    //val soundResId: Int = 0
    var isFavorite: Boolean = false
    var achImageUrl: String = ""
    var achImageUrlGray: String = ""
    val imageURL: String? = null
}
