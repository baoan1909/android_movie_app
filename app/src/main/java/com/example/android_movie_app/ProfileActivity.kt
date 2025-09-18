package com.example.android_movie_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileActivity : BaseActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var userSessionDAO : UserSessionDAO
    private var userId : Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val appMoMo = findViewById<LinearLayout>(R.id.appMoMo)
        val appTraveloka = findViewById<LinearLayout>(R.id.appTraveloka)
        val appGrab = findViewById<LinearLayout>(R.id.appGrab)
        val layoutSupport = findViewById<LinearLayout>(R.id.layoutSupport)
        val layoutContact = findViewById<LinearLayout>(R.id.layoutContact)

        val dbHelper = DatabaseHelper(this)
        userDAO = UserDAO(dbHelper)
        userSessionDAO = UserSessionDAO(dbHelper)

        // Lấy UserId
        val latestValidSession = userSessionDAO.getLatestValidSession()
        if (latestValidSession != null){
            userId = latestValidSession.userId
        }else{
            CustomToast.show(this, "Bạn chưa đăng nhập", ToastType.WARNING)
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
            val popup = UserInfoBottomSheet(this)
            popup.show(ivDropdown)
        }

        // Sự kiện click Đăng xuất
        val btnLogout = findViewById<LinearLayout>(R.id.btnLogoutProfile)
        btnLogout.setOnClickListener {
            latestValidSession?.let {
                userSessionDAO.deleteSession(it.sessionToken)
            }

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // App liên kết
        appMoMo.setOnClickListener {
            openWebsite("https://momo.vn")
        }

        appTraveloka.setOnClickListener {
            openWebsite("https://www.traveloka.com")
        }

        appGrab.setOnClickListener {
            openWebsite("https://www.grab.com/vn/")
        }


        // Trung tâm hỗ trợ
        layoutSupport.setOnClickListener {
            showSupportPopup()
        }

        // Thông tin liên hệ
        layoutContact.setOnClickListener {
            showPopup("Thông tin liên hệ", "Vui lòng liên hệ qua email hoặc số điện thoại dưới đây, xin cảm ơn! \nEmail: support@example.com\nHotline: 1900-123-456")
        }
    }

    // Hàm mở website
    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    // Hàm popup
    private fun showPopup(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Quay lại") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    //Popup support
    private fun showSupportPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Trung tâm hỗ trợ")

        // TextView chứa link
        val textView = TextView(this)
        textView.text = "Bạn có thể xem thêm tại: https://support.google.com/chrome?p=help&ctx=menu#topic=7439538"
        textView.setPadding(40, 30, 40, 30)
        textView.isClickable = true
        textView.isFocusable = true
        textView.autoLinkMask = android.text.util.Linkify.WEB_URLS
        textView.movementMethod = android.text.method.LinkMovementMethod.getInstance()

        builder.setView(textView)

        builder.setPositiveButton("Quay lại") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}
