package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_movie_app.dao.NotificationDAO
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import java.security.MessageDigest
import java.util.Date

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtUsernameRegister: EditText
    private lateinit var edtEmailRegister: EditText
    private lateinit var edtPasswordRegister: EditText
    private lateinit var edtConfirmPasswordRegister: EditText
    private lateinit var btnRegister: Button
    private lateinit var txtLoginHere: TextView

    private lateinit var userDAO: UserDAO
    private lateinit var notificationDAO : NotificationDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DatabaseHelper(this)
        userDAO = UserDAO(dbHelper)
        notificationDAO = NotificationDAO(dbHelper)

        // Ánh xạ view
        edtUsernameRegister = findViewById(R.id.edtUsernameRegister)
        edtEmailRegister = findViewById(R.id.edtEmailRegister)
        edtPasswordRegister = findViewById(R.id.edtPasswordRegister)
        edtConfirmPasswordRegister = findViewById(R.id.edtConfirmPasswordRegister)
        btnRegister = findViewById(R.id.btnRegister)
        txtLoginHere = findViewById(R.id.txtLoginHere)

        val ivTogglePasswordRegister = findViewById<ImageView>(R.id.ivTogglePasswordRegister)
        val ivToggleConfirmPasswordRegister = findViewById<ImageView>(R.id.ivToggleConfirmPasswordRegister)

        // Dùng chung hàm toggleEye
        var isPasswordVisible = false
        var isConfirmPasswordVisible = false

        ivTogglePasswordRegister.setOnClickListener {
            isPasswordVisible = toggleEye(edtPasswordRegister, ivTogglePasswordRegister, isPasswordVisible)
        }

        ivToggleConfirmPasswordRegister.setOnClickListener {
            isConfirmPasswordVisible = toggleEye(edtConfirmPasswordRegister, ivToggleConfirmPasswordRegister, isConfirmPasswordVisible)
        }

        // Xử lý sự kiện đăng ký
        btnRegister.setOnClickListener {
            val username = edtUsernameRegister.text.toString().trim()
            val email = edtEmailRegister.text.toString().trim()
            val password = edtPasswordRegister.text.toString()
            val confirmPassword = edtConfirmPasswordRegister.text.toString()

            when {
                username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    CustomToast.show(this, "Vui lòng nhập đầy đủ thông tin", ToastType.WARNING)
                    return@setOnClickListener
                }
                !validateEmail(email) -> {
                    CustomToast.show(this, "Email không đúng định dạng \nVD: abc@gmail.com", ToastType.ERROR)
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    CustomToast.show(this, "Mật khẩu và nhập lại mật khẩu không khớp", ToastType.ERROR)
                    return@setOnClickListener
                }
                !validatePassword(password) -> {
                    CustomToast.show(
                        this,
                        "Mật khẩu phải bao gồm:\n• Ít nhất 8 ký tự\n• Chữ in hoa\n• Chữ in thường\n• Ký tự đặc biệt",
                        ToastType.WARNING
                    )
                    return@setOnClickListener
                }
                userDAO.getUserByUsername(username) != null -> {
                    CustomToast.show(this, "Tên đăng nhập đã tồn tại", ToastType.ERROR)
                    return@setOnClickListener
                }
                else -> {
                    val newUser = User(
                        avatarPath = "ic_account_circle",
                        username = username,
                        email = email,
                        passwordHash = hashPassword(password),
                        createdAt = Date(),
                        isActive = true
                    )

                    val result = userDAO.addUser(newUser)

                    if (result != -1L) {
                        CustomToast.show(this, "Đăng ký thành công", ToastType.SUCCESS)

                        // --- Lưu thông báo ký ---
                        val user = userDAO.getUserByUsername(username)
                        val notification = Notifications(
                            title = "Đăng ký thành công",
                            content = "Xin chào ${user?.username}, chúc bạn xem phim vui vẻ!",
                            createdAt = Date(),
                            type = "cn",
                            userId = user?.id
                        )
                        notificationDAO.insertNotification(notification)

                        val intent = Intent(this, LoginActivity::class.java)
                        intent.putExtra("username", username)

                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        CustomToast.show(this, "Đăng ký thất bại", ToastType.ERROR)
                    }
                }
            }
        }

        txtLoginHere.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Hàm dùng chung toggle hiển thị/ẩn mật khẩu
     */
    private fun toggleEye(editText: EditText, imageView: ImageView, isVisible: Boolean): Boolean {
        val newVisible = !isVisible
        if (newVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageView.setImageResource(R.drawable.ic_eye_off)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageView.setImageResource(R.drawable.ic_eye_open)
        }
        editText.setSelection(editText.text.length)
        return newVisible
    }

    private fun validatePassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$"
        return Regex(passwordPattern).matches(password)
    }

    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
