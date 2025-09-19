package com.example.android_movie_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.android_movie_app.adapter.UserInfoBottomSheet
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : BaseActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var userSessionDAO: UserSessionDAO
    private var userId: Int = -1
    private lateinit var imgAvatar: ImageView
    private val PICK_IMAGE_REQUEST = 1001
    private var currentUser: User? = null
    private var latestValidSession: UserSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val appMoMo = findViewById<LinearLayout>(R.id.appMoMo)
        val appTraveloka = findViewById<LinearLayout>(R.id.appTraveloka)
        val appGrab = findViewById<LinearLayout>(R.id.appGrab)
        val layoutSupport = findViewById<LinearLayout>(R.id.layoutSupport)
        val layoutContact = findViewById<LinearLayout>(R.id.layoutContact)
        imgAvatar = findViewById(R.id.avatarPath)

        val dbHelper = DatabaseHelper(this)
        userDAO = UserDAO(dbHelper)
        userSessionDAO = UserSessionDAO(dbHelper)

        // Lấy UserId
        latestValidSession = userSessionDAO.getLatestValidSession()
        if (latestValidSession != null) {
            userId = latestValidSession!!.userId
        } else {
            CustomToast.show(this, "Bạn chưa đăng nhập", ToastType.WARNING)
        }

        // Load user
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                userDAO.getUserById(userId)
            }
            user?.let {
                currentUser = it
                findViewById<TextView>(R.id.tvUsernameProfile).text = it.username


                if (!it.avatarPath.isNullOrEmpty()) {
                    Glide.with(this@ProfileActivity)
                        .load(File(it.avatarPath))
                        .placeholder(R.drawable.ic_account_circle)
                        .into(imgAvatar)
                }
            }
        }

        // Chọn avatar mới
        imgAvatar.setOnClickListener {
            openGallery()
        }

        // Drop down
        val ivDropdown = findViewById<ImageView>(R.id.ivDropdownProfile)
        ivDropdown.setOnClickListener {
            val popup = UserInfoBottomSheet(this)
            popup.show(ivDropdown)
        }

        // Đăng xuất
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
        appMoMo.setOnClickListener { openWebsite("https://momo.vn") }
        appTraveloka.setOnClickListener { openWebsite("https://www.traveloka.com") }
        appGrab.setOnClickListener { openWebsite("https://www.grab.com/vn/") }

        layoutSupport.setOnClickListener { showSupportPopup() }
        layoutContact.setOnClickListener {
            showPopup(
                "Thông tin liên hệ",
                "Vui lòng liên hệ qua email hoặc số điện thoại dưới đây:\nEmail: support@example.com\nHotline: 1900-123-456"
            )
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val savedPath = saveImageToInternalStorage(imageUri)
                if (savedPath != null && currentUser != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        userDAO.updateUserAvatar(currentUser!!.id, savedPath)
                    }
                    Glide.with(this)
                        .load(File(savedPath))
                        .placeholder(R.drawable.ic_account_circle)
                        .into(imgAvatar)
                }
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(filesDir, "avatar_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun openWebsite(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun showPopup(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Quay lại") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun showSupportPopup() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Trung tâm hỗ trợ")
        val textView = TextView(this).apply {
            text = "Bạn có thể xem thêm tại: https://support.google.com/chrome?p=help&ctx=menu#topic=7439538"
            setPadding(40, 30, 40, 30)
            isClickable = true
            isFocusable = true
            autoLinkMask = android.text.util.Linkify.WEB_URLS
            movementMethod = android.text.method.LinkMovementMethod.getInstance()
        }
        builder.setView(textView)
        builder.setPositiveButton("Quay lại") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
