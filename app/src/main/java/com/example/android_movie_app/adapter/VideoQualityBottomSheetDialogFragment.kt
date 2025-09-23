package com.example.android_movie_app.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.android_movie_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VideoQualityBottomSheetDialogFragment(
    private var currentQuality: String = "720p",
    private val onQualitySelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var tvCurrentQuality: TextView
    private val qualityOptions = listOf("1080p", "720p", "480p", "360p", "240p", "144p")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_video_quality, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvCurrentQuality = view.findViewById(R.id.tv_current_quality)
        updateCurrentQuality()

        // container chứa list quality (LinearLayout)
        val containerOptions = view as ViewGroup
        for (i in 0 until containerOptions.childCount) {
            val child = containerOptions.getChildAt(i)
            if (child is LinearLayout) {
                // duyệt các TextView trong LinearLayout
                for (j in 0 until child.childCount) {
                    val optionView = child.getChildAt(j)
                    if (optionView is TextView) {
                        optionView.setOnClickListener {
                            val selectedQuality = optionView.text.toString()
                            currentQuality = selectedQuality
                            updateCurrentQuality()
                            onQualitySelected(selectedQuality)
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun updateCurrentQuality() {
        tvCurrentQuality.text = "Chất lượng video hiện tại • $currentQuality"
    }
}
