package com.example.android_movie_app.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android_movie_app.R
import com.example.android_movie_app.SettingOption
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MainSettingsBottomSheet(
// Nhận vào cả chất lượng và tốc độ hiện tại
private val currentQuality: String,
private val currentSpeed: Float,
// Sửa callback onQualitySelected để trả về String
private val onQualitySelected: (String) -> String,
private val onSpeedSelected: (Float) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var adapter: SettingsAdapter
    private lateinit var options: MutableList<SettingOption>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_main_settings, container, false)
    }

    @UnstableApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvSettings = view.findViewById<RecyclerView>(R.id.rv_settings)
        rvSettings.layoutManager = LinearLayoutManager(requireContext())

        val currentSpeedText = String.format("%.2fx", currentSpeed)

        options = mutableListOf(
            SettingOption(
                iconRes = R.drawable.ic_settings,
                title = "Chất lượng",
                value = currentQuality, // Hiển thị chất lượng ban đầu
                onClick = {
                    val qualityDialog = VideoQualityBottomSheetDialogFragment(
                        currentQuality = options.find { it.title == "Chất lượng" }?.value ?: "720p"
                    ) { selectedQuality ->
                        // 1. Gọi callback của Adapter và nhận lại kết quả
                        val qualityToShow = onQualitySelected(selectedQuality)

                        // 2. Cập nhật UI của dialog này với kết quả đó
                        val qualityIndex = options.indexOfFirst { it.title == "Chất lượng" }
                        if (qualityIndex != -1) {
                            options[qualityIndex] = options[qualityIndex].copy(value = qualityToShow)
                            adapter.notifyItemChanged(qualityIndex)
                        }
                    }
                    qualityDialog.show(parentFragmentManager, "VideoQualityDialog")
                }
            ),
            SettingOption(
                iconRes = R.drawable.ic_speed,
                title = "Tốc độ phát",
                value = currentSpeedText,
                onClick = {
                    // Lấy giá trị tốc độ hiện tại từ text view
                    val speedValue = options.find { it.title == "Tốc độ phát" }?.value
                        ?.replace("x", "")?.toFloatOrNull() ?: 1.0f

                    val dialog = PlaybackSpeedBottomSheetDialogFragment(
                        currentSpeed = speedValue
                    ) { selectedSpeed ->
                        onSpeedSelected(selectedSpeed)
                        // Cập nhật UI của dialog này
                        val speedText = String.format("%.2fx", selectedSpeed)
                        val speedIndex = options.indexOfFirst { it.title == "Tốc độ phát" }
                        if (speedIndex != -1) {
                            options[speedIndex] = options[speedIndex].copy(value = speedText)
                            adapter.notifyItemChanged(speedIndex)
                        }
                    }
                    dialog.show(parentFragmentManager, "PlaybackSpeedDialog")
                }
            )
        )

        adapter = SettingsAdapter(options)
        rvSettings.adapter = adapter
    }
}
