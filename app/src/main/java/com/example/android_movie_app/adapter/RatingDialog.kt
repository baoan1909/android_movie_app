package com.example.android_movie_app.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RatingBar
import com.example.android_movie_app.CustomToast
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.R
import com.example.android_movie_app.SessionManager
import com.example.android_movie_app.ToastType
import com.example.android_movie_app.dao.ReviewDAO
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import com.google.android.material.bottomsheet.BottomSheetDialog

class RatingDialog(
    private val context: Context,
    private val movieId: Int,
    private val episodeId: Int? = null,
    private val onRatingSaved: (Int) -> Unit
) {

    private fun getCurrentUserIdOrNull(): Int? {
        val sessionDAO = UserSessionDAO(DatabaseHelper(context))
        val session = sessionDAO.getLatestValidSession() ?: return null

        val userDAO = UserDAO(DatabaseHelper(context))
        val user = userDAO.getUserById(session.userId) ?: return null

        return user.id
    }

    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme).apply {
            setContentView(
                LayoutInflater.from(context).inflate(R.layout.layout_rating_dialog, null)
            )
        }

        val ratingBar = dialog.findViewById<RatingBar>(R.id.rating_bar)
        val closeButton = dialog.findViewById<ImageView>(R.id.close_button)

        closeButton?.setOnClickListener { dialog.dismiss() }

        ratingBar?.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (!fromUser) return@setOnRatingBarChangeListener

            val userId = getCurrentUserIdOrNull()

            if (userId == null) {
                CustomToast.show(context, "Bạn chưa đăng nhập", ToastType.WARNING)
            } else {
                val reviewDAO = ReviewDAO(DatabaseHelper(context))
                val success = reviewDAO.insertOrUpdateReview(
                    userId = userId,
                    movieId = movieId,
                    episodeId = episodeId,
                    rating = rating.toInt()
                )

                if (success) {
                    CustomToast.show(context, "Đánh giá ${rating.toInt()} ★ thành công", ToastType.SUCCESS)
                    onRatingSaved(rating.toInt())

                    // Đợi Toast hiển thị xong rồi đóng dialog
                    Handler(Looper.getMainLooper()).postDelayed({
                        dialog.dismiss()
                    }, 1500) // 1.5s = thời gian Toast.LENGTH_SHORT
                } else {
                    CustomToast.show(context, "Không thể lưu đánh giá", ToastType.ERROR)
                }
            }
        }

        dialog.show()
    }
}