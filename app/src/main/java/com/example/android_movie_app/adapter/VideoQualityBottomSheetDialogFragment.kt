package com.example.android_movie_app.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.android_movie_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VideoQualityBottomSheetDialogFragment(
    private var currentQuality: String = "720p",
    private val onQualitySelected: (String) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var tvCurrentQuality: TextView
    private val qualityViewIds = listOf(
        R.id.quality_1080p, R.id.quality_720p, R.id.quality_480p,
        R.id.quality_360p, R.id.quality_240p, R.id.quality_144p
    )

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

        // Duyệt qua danh sách ID đã định nghĩa
        qualityViewIds.forEach { id ->
            val optionView = view.findViewById<TextView>(id)
            val optionQuality = optionView.text.toString()

            // 1. Kiểm tra và đổi màu cho chất lượng hiện tại
            if (optionQuality == currentQuality) {
                optionView.setTextColor(Color.parseColor("#FFA500")) // Màu cam
            } else {
                optionView.setTextColor(Color.WHITE) // Hoặc màu chữ mặc định của bạn
            }

            // 2. Gán sự kiện click
            optionView.setOnClickListener {
                if (optionQuality == currentQuality) {
                    // Nếu bấm vào chất lượng đang được chọn -> hiển thị thông báo
                    Toast.makeText(requireContext(), "Bạn đang chọn chất lượng này", Toast.LENGTH_SHORT).show()
                } else {
                    // Nếu chọn chất lượng mới -> thực hiện callback và đóng dialog
                    onQualitySelected(optionQuality)
                    dismiss()
                }
            }
        }
    }

    private fun updateCurrentQuality() {
        tvCurrentQuality.text = "Chất lượng video hiện tại • $currentQuality"
    }
}
