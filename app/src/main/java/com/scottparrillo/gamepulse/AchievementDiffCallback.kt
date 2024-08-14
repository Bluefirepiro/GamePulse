package com.scottparrillo.gamepulse

import androidx.recyclerview.widget.DiffUtil

class AchievementDiffCallback(
    private val oldList: List<Achievement>,
    private val newList: List<Achievement>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].title == newList[newItemPosition].title

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}
