package com.example.android_movie_app

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RatingBar
import com.example.android_movie_app.dao.ReviewDAO
import com.google.android.material.bottomsheet.BottomSheetDialog

class RatingDialog(
    private val context: Context,
    private val movieId: Int,
    private val episodeId: Int? = null,
    private val onRatingSaved: (Int) -> Unit
) {
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

            val session = SessionManager(context)
            val userId = session.getUserId()

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
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
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
