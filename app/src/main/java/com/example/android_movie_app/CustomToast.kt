package com.example.android_movie_app

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.android_movie_app.R

enum class ToastType {
    SUCCESS, ERROR, WARNING, INFO
}

object CustomToast {

    fun show(context: Context, message: String, type: ToastType) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val toastLayout = layout.findViewById<LinearLayout>(R.id.toastLayoutRoot)
        val toastIcon = layout.findViewById<ImageView>(R.id.toastIcon)
        val toastMessage = layout.findViewById<TextView>(R.id.toastMessage)

        when (type) {
            ToastType.SUCCESS -> {
                (toastLayout.background as GradientDrawable).setColor(Color.parseColor("#4CAF50"))
                toastIcon.setImageResource(R.drawable.ic_check)
            }
            ToastType.ERROR -> {
                (toastLayout.background as GradientDrawable).setColor(Color.parseColor("#F44336"))
                toastIcon.setImageResource(R.drawable.ic_error)
            }
            ToastType.WARNING -> {
                (toastLayout.background as GradientDrawable).setColor(Color.parseColor("#FF9800"))
                toastIcon.setImageResource(R.drawable.ic_warning)
            }
            ToastType.INFO -> {
                (toastLayout.background as GradientDrawable).setColor(Color.parseColor("#333333"))
                toastIcon.setImageResource(R.drawable.ic_infor)
            }
        }

        toastMessage.text = message

        Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            view = layout
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 120)
            show()
        }
    }
}
