package com.example.android_movie_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.example.android_movie_app.dao.UserDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserInfoBottomSheet(private val context: Context) {
    fun show(anchor: View) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_user_info, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f

        // Lấy userId từ SharedPreferences
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)

        if (userId != -1) {
            val dbHelper = DatabaseHelper(context)
            val userDAO = UserDAO(dbHelper)

            CoroutineScope(Dispatchers.IO).launch {
                val user = userDAO.getUserById(userId)
                user?.let {
                    withContext(Dispatchers.Main) {
                        popupView.findViewById<TextView>(R.id.tvName_popup_info).text = it.username
                        popupView.findViewById<TextView>(R.id.tvEmail_popup_info).text = it.email
                        popupView.findViewById<TextView>(R.id.tvCreatedAt_popup_info).text =
                            "Ngày tạo: ${it.createdAt}"
                        popupView.findViewById<TextView>(R.id.tvStatus_popup_info).text =
                            "Trạng thái: ${if (it.isActive) "Hoạt động" else "Không hoạt động"}"
                    }
                }
            }
        }

        // Show popup ngay dưới và lệch phải icon
        popupWindow.showAsDropDown(anchor, -100, 10) // chỉnh x, y offset cho đẹp
    }
}