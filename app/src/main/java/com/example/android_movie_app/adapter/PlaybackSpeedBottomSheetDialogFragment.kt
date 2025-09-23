package com.example.android_movie_app.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.children
import com.example.android_movie_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import java.util.Locale
import kotlin.math.absoluteValue

class PlaybackSpeedBottomSheetDialogFragment(
    private var currentSpeed: Float = 1.0f,
    private val onSpeedSelected: (Float) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var tvSpeedValue: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var btnDecrease: ImageView
    private lateinit var btnIncrease: ImageView
    private lateinit var toggleGroup: LinearLayout   // giữ nguyên id: toggle_group_speed

    private val presetSpeeds = mapOf(
        R.id.btn_speed_1 to 1.0f,
        R.id.btn_speed_1_25 to 1.25f,
        R.id.btn_speed_1_5 to 1.5f,
        R.id.btn_speed_2 to 2.0f,
        R.id.btn_speed_3 to 3.0f
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_playback_speed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvSpeedValue = view.findViewById(R.id.tv_speed_value)
        seekBar = view.findViewById(R.id.seekbar_speed)
        btnDecrease = view.findViewById(R.id.btn_decrease_speed)
        btnIncrease = view.findViewById(R.id.btn_increase_speed)
        toggleGroup = view.findViewById(R.id.toggle_group_speed)

        updateSpeedText()

        // Setup SeekBar: min 0.25x, max 3.0x
        seekBar.max = 275
        seekBar.progress = ((currentSpeed - 0.25f) / 0.01f).toInt()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentSpeed = 0.25f + progress * 0.01f
                    updateSpeedText()
                    syncToggleWithSpeed(currentSpeed)
                }
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {
                onSpeedSelected(currentSpeed)
            }
        })

        btnDecrease.setOnClickListener { changeSpeed(-0.25f) }
        btnIncrease.setOnClickListener { changeSpeed(+0.25f) }

        // Setup click cho từng button trong toggleGroup
        toggleGroup.children.forEach { child ->
            if (child is android.widget.Button) {
                child.setOnClickListener {
                    val speed = presetSpeeds[child.id] ?: return@setOnClickListener
                    currentSpeed = speed
                    updateSeekBar()
                    updateSpeedText()
                    onSpeedSelected(currentSpeed)
                    highlightSelectedButton(child.id)
                }
            }
        }
        // Khởi tạo đúng button được chọn
        syncToggleWithSpeed(currentSpeed)
    }

    private fun updateSpeedText() {
        tvSpeedValue.text = String.format(Locale.US, "%.2fx", currentSpeed)
    }

    private fun updateSeekBar() {
        seekBar.progress = ((currentSpeed - 0.25f) / 0.01f).toInt()
    }

    private fun changeSpeed(delta: Float) {
        val newSpeed = (currentSpeed + delta).coerceIn(0.25f, 3.0f)
        currentSpeed = when {
            (newSpeed - 1.0f).absoluteValue < 0.01f -> 1.0f
            (newSpeed - 1.25f).absoluteValue < 0.01f -> 1.25f
            (newSpeed - 1.5f).absoluteValue < 0.01f -> 1.5f
            (newSpeed - 2.0f).absoluteValue < 0.01f -> 2.0f
            (newSpeed - 3.0f).absoluteValue < 0.01f -> 3.0f
            else -> newSpeed
        }
        updateSeekBar()
        updateSpeedText()
        syncToggleWithSpeed(currentSpeed)
        onSpeedSelected(currentSpeed)
    }

    private fun syncToggleWithSpeed(speed: Float) {
        val buttonId = presetSpeeds.entries.find { it.value == speed }?.key
        highlightSelectedButton(buttonId)
    }

    private fun highlightSelectedButton(selectedId: Int?) {
        toggleGroup.children.forEach { child ->
            if (child is android.widget.Button) {
                child.isSelected = (child.id == selectedId)
            }
        }
    }
}
