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

// Sửa constructor để nhận các hàm callback
class MainSettingsBottomSheet(
    private val currentSpeed: Float,
    private val onQualitySelected: (String) -> Unit,
    private val onSpeedSelected: (Float) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var adapter: SettingsAdapter
    private lateinit var options: MutableList<SettingOption>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                value = "Tự động (720p)", // Giá trị này có thể cần được truyền từ ngoài vào
                onClick = {
                    val dialog = VideoQualityBottomSheetDialogFragment(
                        currentQuality = options[0].value ?: "720p"
                    ) { selectedQuality ->
                        // Cập nhật UI
                        options[0] = options[0].copy(value = selectedQuality)
                        adapter.notifyItemChanged(0)

                        // **GỌI CALLBACK VỀ ADAPTER**
                        onQualitySelected(selectedQuality)
                        dismiss() // Đóng dialog cài đặt sau khi chọn
                    }
                    dialog.show(parentFragmentManager, "VideoQualityDialog")
                }
            ),
            SettingOption(
                iconRes = R.drawable.ic_speed,
                title = "Tốc độ phát",
                value = currentSpeedText,
                onClick = {
                    val dialog = PlaybackSpeedBottomSheetDialogFragment(
                        currentSpeed = this.currentSpeed // <-- TRUYỀN TỐC ĐỘ HIỆN TẠI
                    ) { selectedSpeed ->
                        // Cập nhật UI
                        val speedText = String.format("%.2fx", selectedSpeed)
                        options[1] = options[1].copy(value = speedText)
                        adapter.notifyItemChanged(1)

                        // GỌI CALLBACK VỀ ADAPTER
                        onSpeedSelected(selectedSpeed)
                    }
                    dialog.show(parentFragmentManager, "PlaybackSpeedDialog")
                }
            )
        )

        adapter = SettingsAdapter(options)
        rvSettings.adapter = adapter
    }
}
