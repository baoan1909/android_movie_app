package com.example.android_movie_app

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android_movie_app.dao.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.android_movie_app.UserInfoBottomSheet

class ProfileActivity : AppCompatActivity() {

    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val dbHelper = DatabaseHelper(this)
        userDAO = UserDAO(dbHelper)

        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val userId = sharedPref.getInt("USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            return
        }

        // Load username ra màn Profile
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                userDAO.getUserById(userId)
            }
            user?.let {
                findViewById<TextView>(R.id.tvUsernameProfile).text = it.username
            }
        }

        // Sự kiện click icon drop down
        val ivDropdown = findViewById<ImageView>(R.id.ivDropdownProfile)
        ivDropdown.setOnClickListener {
            val popup = UserInfoBottomSheet(this)   // class mình viết ở trên
            popup.show(ivDropdown)
        }

        // Sự kiện click Đăng xuất
        val btnLogout = findViewById<LinearLayout>(R.id.btnLogoutProfile)
        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
