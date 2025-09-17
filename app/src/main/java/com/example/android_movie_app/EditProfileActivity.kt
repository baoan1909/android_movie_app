package com.example.android_movie_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.android_movie_app.dao.UserDAO
import java.security.MessageDigest

class EditProfileActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSave: Button

    private lateinit var userDAO: UserDAO
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Khởi tạo
        val dbHelper = DatabaseHelper(this)
        userDAO = UserDAO(dbHelper)
        session = SessionManager(this)

        edtUsername = findViewById(R.id.edtUsername_edit_profile)
        edtEmail = findViewById(R.id.edtEmail_edit_profile)
        edtPassword = findViewById(R.id.edtPassword_edit_profile)
        btnSave = findViewById(R.id.btnSave_edit)

        // Lấy userId từ session
        val userId = session.getUserId()
        val user = userDAO.getUserById(userId)

        if (user != null) {
            edtUsername.setText(user.username)
            edtEmail.setText(user.email)
        }

        // Sự kiện lưu
        btnSave.setOnClickListener {
            val newUsername = edtUsername.text.toString().trim()
            val newEmail = edtEmail.text.toString().trim()
            val newPassword = edtPassword.text.toString().trim()

            if (newUsername.isEmpty() || newEmail.isEmpty()) {
                CustomToast.show(this, "Vui lòng nhập đầy đủ thông tin", ToastType.WARNING)
                return@setOnClickListener
            }

            if (user != null) {
                user.username = newUsername
                user.email = newEmail

                // Nếu người dùng nhập mật khẩu mới thì hash lại
                if (newPassword.isNotEmpty()) {
                    user.passwordHash = hashPassword(newPassword)
                }

                val result = userDAO.updateUser(user)
                if (result > 0) {
                    CustomToast.show(this, "Cập nhật thành công", ToastType.SUCCESS)
                    finish() // quay lại màn trước
                } else {
                    CustomToast.show(this, "Cập nhật thất bại", ToastType.ERROR)
                }
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }  // chuyển thành chuỗi hex
    }
}