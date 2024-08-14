package com.scottparrillo.gamepulse

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class AchievementAdapter(
    private var achievements: MutableList<Achievement>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.achievementIcon)
        val title: TextView = view.findViewById(R.id.achievementTitle)
        val description: TextView = view.findViewById(R.id.achievementDescription)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        val progressText: TextView = view.findViewById(R.id.progressText)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.icon.setImageResource(achievement.iconResId)
        holder.title.text = achievement.title
        holder.description.text = achievement.description
        holder.progressBar.max = achievement.total
        holder.progressBar.progress = achievement.progress
        holder.progressText.text = holder.itemView.context.getString(
            R.string.progress_text,
            achievement.progress,
            achievement.total
        )

        holder.itemView.setOnClickListener {
            achievement.soundResId?.let { soundId ->
                val mediaPlayer = MediaPlayer.create(holder.itemView.context, soundId)
                mediaPlayer.start()
                mediaPlayer.setOnCompletionListener {
                    it.release()
                }
            }
        }

        holder.deleteButton.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount(): Int = achievements.size

    fun removeItem(position: Int) {
        if (position >= 0 && position < achievements.size) {
            achievements.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount)
        }
    }

    fun updateList(newList: List<Achievement>) {
        val diffCallback = AchievementDiffCallback(achievements, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        achievements.clear()
        achievements.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

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
}

