package com.example.android_movie_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import java.security.MessageDigest

class EditProfileActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSave: Button

    private lateinit var userDAO: UserDAO
    private lateinit var session: SessionManager
    private lateinit var userSessionDAO: UserSessionDAO

    private var userId: Int = -1
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val dbHelper = DatabaseHelper(this)
        userDAO = UserDAO(dbHelper)
        session = SessionManager(this)
        userSessionDAO = UserSessionDAO(dbHelper)

        edtUsername = findViewById(R.id.edtUsername_edit_profile)
        edtEmail = findViewById(R.id.edtEmail_edit_profile)
        edtPassword = findViewById(R.id.edtPassword_edit_profile)
        btnSave = findViewById(R.id.btnSave_edit)

        // --- Lấy userId ---
        val latestValidSession = userSessionDAO.getLatestValidSession()
        userId = latestValidSession?.userId ?: session.getUserId()

        if (userId == -1) {
            CustomToast.show(this, "Bạn chưa đăng nhập", ToastType.WARNING)
            finish()
            return
        }

        // --- Lấy user ---
        user = userDAO.getUserById(userId)
        if (user != null) {
            edtUsername.setText(user!!.username)
            edtEmail.setText(user!!.email)
        }

        // --- Lưu cập nhật ---
        btnSave.setOnClickListener {
            val newUsername = edtUsername.text.toString().trim()
            val newEmail = edtEmail.text.toString().trim()
            val newPassword = edtPassword.text.toString().trim()

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                CustomToast.show(this, "Vui lòng nhập đầy đủ thông tin", ToastType.WARNING)
                return@setOnClickListener
            }

            user?.let {
                it.username = newUsername
                it.email = newEmail
                if (newPassword.isNotEmpty()) {
                    it.passwordHash = hashPassword(newPassword)
                }

                val result = userDAO.updateUser(it)
                if (result > 0) {
                    CustomToast.show(this, "Cập nhật thành công", ToastType.SUCCESS)
                    finish()
                } else {
                    CustomToast.show(this, "Cập nhật thất bại", ToastType.ERROR)
                }
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}