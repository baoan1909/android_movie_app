package com.example.android_movie_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.R
import androidx.appcompat.widget.AppCompatButton
import com.example.android_movie_app.Episode

class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val lastWatchedEpisodeNumber: Int?,
    private val onItemClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        // Nếu đã xem tập nào → set selectedPosition theo nó
        // Nếu chưa → mặc định chọn tập 1
        selectedPosition = if (!episodes.isNullOrEmpty()) {
            val index = lastWatchedEpisodeNumber?.let { number ->
                episodes.indexOfFirst { it.episodeNumber == number }
            } ?: 0 // chưa có thì tập 1
            if (index != -1) index else 0
        } else {
            RecyclerView.NO_POSITION
        }
    }

    inner class EpisodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val episodeButton: AppCompatButton = itemView.findViewById(R.id.btnEpisodeItem)

        fun bind(episode: Episode, position: Int) {
            episodeButton.text = "Tập ${episode.episodeNumber}"

            // Đổi background nếu là tập đang chọn
            if (position == selectedPosition) {
                episodeButton.setBackgroundResource(R.drawable.button_background_selected)
            } else {
                episodeButton.setBackgroundResource(R.drawable.button_background)
            }

            episodeButton.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                onItemClick(episode)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun getItemCount(): Int = episodes.size

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        holder.bind(episodes[position], position)
    }
}
