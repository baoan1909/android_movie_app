package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_movie_app.dao.NotificationDAO
import com.example.android_movie_app.dao.UserDAO
import java.security.MessageDigest
import java.util.Date

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var txtCreateAccount: TextView
    private lateinit var btnLogin: Button
    private lateinit var userDAO: UserDAO
    private lateinit var notificationDAO: NotificationDAO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DatabaseHelper(this) // Tạo database helper
        userDAO = UserDAO(dbHelper)         // Khởi tạo userDAO
        notificationDAO = NotificationDAO(dbHelper)

        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        txtCreateAccount = findViewById(R.id.txtCreateAccount)
        btnLogin = findViewById(R.id.btnLogin)
        var isPasswordVisible = false
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val ivTogglePassword = findViewById<ImageView>(R.id.ivTogglePassword)

        // Auto-fill username nếu được truyền từ RegisterActivity
        val prefillUsername = intent.getStringExtra("username")
        if (!prefillUsername.isNullOrEmpty()) {
            edtUsername.setText(prefillUsername)

            // Focus vào ô password + mở bàn phím
            edtPassword.requestFocus()
            val imm = getSystemService<InputMethodManager>()
            imm?.showSoftInput(edtPassword, InputMethodManager.SHOW_IMPLICIT)
        }

        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                // Hiện mật khẩu
                edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off)
            } else {
                // Ẩn mật khẩu
                edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePassword.setImageResource(R.drawable.ic_eye_open)
            }
            // Đưa con trỏ về cuối text
            edtPassword.setSelection(edtPassword.text.length)
        }

        // Xử lý đăng nhập
        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString()
            val password = edtPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                CustomToast.show(this, "Vui lòng nhập đầy đủ thông tin", ToastType.WARNING)
                return@setOnClickListener
            }

            val user = userDAO.getUserByUsername(username)
            if (user == null) {
                CustomToast.show(this, "Tài khoản không tồn tại", ToastType.ERROR)
                return@setOnClickListener
            }

            val passwordHash = hashPassword(password)
            if (user.passwordHash != passwordHash) {
                CustomToast.show(this, "Sai mật khẩu", ToastType.ERROR)
                return@setOnClickListener
            }

            if (!user.isActive) {
                CustomToast.show(this, "Tài khoản chưa được kích hoạt", ToastType.WARNING)
                return@setOnClickListener
            }

//            val noti = Notifications(
//                notificationId = 0,
//                title = "Đăng nhập thành công",
//                content = "Xin chào ${user.username}, chúc bạn xem phim vui vẻ!",
//                createdAt = Date()
//            )
//            notificationDAO.insertNotification(noti)
            // Đăng nhập thành công
            CustomToast.show(this, "Đăng nhập thành công", ToastType.SUCCESS)

            val session = SessionManager(this)
            session.saveUserId(user.id)

            // Chuyển sang màn hình Home/Main
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Chuyển sang đăng ký
        txtCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }  // chuyển thành chuỗi hex
    }
}
