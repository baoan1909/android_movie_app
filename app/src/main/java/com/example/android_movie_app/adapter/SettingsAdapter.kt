package com.example.android_movie_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.R
import com.example.android_movie_app.SettingOption

class SettingsAdapter(
    private val items: List<SettingOption>
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    inner class SettingsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.iv_setting_icon)
        val title: TextView = view.findViewById(R.id.tv_setting_title)
        val value: TextView = view.findViewById(R.id.tv_setting_value)
        val arrow: ImageView = view.findViewById(R.id.iv_setting_arrow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_setting_option, parent, false)
        return SettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconRes)
        holder.title.text = item.title
        holder.value.text = item.value ?: ""

        holder.itemView.setOnClickListener {
            item.onClick?.invoke()
        }
    }

    override fun getItemCount(): Int = items.size
}
