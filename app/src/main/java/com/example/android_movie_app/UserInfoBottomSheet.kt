package com.example.android_movie_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class UserInfoBottomSheet(private val context: Context) {

    private var userId : Int = -1
    private lateinit var userSessionDAO : UserSessionDAO

    fun show(anchor: View) {
        val popupView = LayoutInflater.from(context).inflate(R.layout.popup_user_info, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f

        val dbHelper = DatabaseHelper(context)
        userSessionDAO = UserSessionDAO(dbHelper)
        val latestValidSession = userSessionDAO.getLatestValidSession()
        if (latestValidSession != null){
            userId = latestValidSession.userId
        }else{
            CustomToast.show(context, "Bạn chưa đăng nhập", ToastType.WARNING)
        }

        if (userId != -1) {
            val dbHelper = DatabaseHelper(context)
            val userDAO = UserDAO(dbHelper)
            val btnEditProfile: LinearLayout = popupView.findViewById(R.id.btnEditProfile)

            CoroutineScope(Dispatchers.IO).launch {
                val user = userDAO.getUserById(userId)

                withContext(Dispatchers.Main) {
                    user?.let {
                        popupView.findViewById<TextView>(R.id.tvName_popup_info).text = it.username
                        popupView.findViewById<TextView>(R.id.tvEmail_popup_info).text = it.email
                        popupView.findViewById<TextView>(R.id.tvCreatedAt_popup_info).text =
                            "Ngày tạo: ${it.createdAt}"
                        popupView.findViewById<TextView>(R.id.tvStatus_popup_info).text =
                            "Trạng thái: ${if (it.isActive) "Hoạt động" else "Không hoạt động"}"
                    }

                    // Xử lý nút Edit Profile
                    btnEditProfile.setOnClickListener {
                        popupWindow.dismiss()
                        showEditDialog(anchor, userDAO, user) // truyền anchor
                    }
                }
            }
        }

        // Show popup
        popupWindow.showAsDropDown(anchor, -100, 10)
    }

    private fun showEditDialog(anchor: View, userDAO: UserDAO, user: User?) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_edit_profile, null)

        val edtUsername = dialogView.findViewById<EditText>(R.id.edtUsername_edit_profile)
        val edtEmail = dialogView.findViewById<EditText>(R.id.edtEmail_edit_profile)
        val edtPassword = dialogView.findViewById<EditText>(R.id.edtPassword_edit_profile)
        val edtRePassword = dialogView.findViewById<EditText>(R.id.edtRePassword_edit_profile)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave_edit)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel_edit)

        user?.let {
            edtUsername.setText(it.username)
            edtEmail.setText(it.email)
        }

        val dialog = android.app.AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        // Save
        btnSave.setOnClickListener {
            val newUsername = edtUsername.text.toString().trim()
            val newEmail = edtEmail.text.toString().trim()
            val newPassword = edtPassword.text.toString().trim()
            val rePassword = edtRePassword.text.toString().trim()

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                CustomToast.show(context, "Vui lòng nhập đầy đủ thông tin", ToastType.WARNING)
                return@setOnClickListener
            }

            if (newPassword.isNotEmpty() && newPassword != rePassword) {
                CustomToast.show(context, "Mật khẩu nhập lại không khớp", ToastType.ERROR)
                return@setOnClickListener
            }

            user?.apply {
                username = newUsername
                email = newEmail
                if (newPassword.isNotEmpty()) {
                    passwordHash = hashPassword(newPassword)
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                val result = userDAO.updateUser(user!!)
                withContext(Dispatchers.Main) {
                    if (result > 0) {
                        CustomToast.show(context, "Cập nhật thành công", ToastType.SUCCESS)
                        dialog.dismiss()
                        show(anchor) // reload lại popup info
                    } else {
                        CustomToast.show(context, "Cập nhật thất bại", ToastType.ERROR)
                    }
                }
            }
        }


        // Cancel
        btnCancel.setOnClickListener {
            dialog.dismiss()
            // Mở lại popup_user_info
            show(anchor)
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
