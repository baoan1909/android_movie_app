package com.example.android_movie_app

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android_movie_app.dao.UserDAO
import java.security.MessageDigest
import java.util.Date

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtUsernameRegister: EditText
    private lateinit var edtEmailRegister: EditText
    private lateinit var edtPasswordRegister: EditText
    private lateinit var edtConformPasswordRegister: EditText
    private lateinit var btnRegister: Button
    private lateinit var txtLoginHere: TextView

    private lateinit var userDAO: UserDAO
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DatabaseHelper(this) // Tạo database helper
        userDAO = UserDAO(dbHelper)         // Khởi tạo userDAO

        // Ánh xạ view
        edtUsernameRegister = findViewById(R.id.edtUsernameRegister)
        edtEmailRegister = findViewById(R.id.edtEmailRegister)
        edtPasswordRegister = findViewById(R.id.edtPasswordRegister)
        edtConformPasswordRegister = findViewById(R.id.edtConfirmPasswordRegister)
        btnRegister = findViewById(R.id.btnRegister)
        txtLoginHere = findViewById(R.id.txtLoginHere)

        // Xử lý sự kiện đăng ký
        btnRegister.setOnClickListener {
            val username = edtUsernameRegister.text.toString().trim()
            val email = edtEmailRegister.text.toString().trim()
            val password = edtPasswordRegister.text.toString()
            val confirmPassword = edtConformPasswordRegister.text.toString()

            // Kiểm tra dữ liệu đầu vào
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
                    // Tạo user mới
                    val newUser = User(
                        username = username,
                        email = email,
                        passwordHash = hashPassword(password),
                        createdAt = Date(),
                        isActive = true
                    )

                    val result = userDAO.addUser(newUser)

                    if (result != -1L) {
                        CustomToast.show(this, "Đăng ký thành công", ToastType.SUCCESS)
                        
                        // Chuyển thẳng vào app sau khi đăng ký thành công
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        CustomToast.show(this, "Đăng ký thất bại", ToastType.ERROR)
                    }
                }
            }
        }

        // Chuyển sang màn hình đăng nhập
        txtLoginHere.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Validate mật khẩu
    private fun validatePassword(password: String): Boolean {
        val passwordPattern =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&*(),.?\":{}|<>]).{8,}\$"
        return Regex(passwordPattern).matches(password)
    }

    // Validate email
    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Hash mật khẩu
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
