package com.example.android_movie_app.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import com.example.android_movie_app.CustomToast
import com.example.android_movie_app.R
import com.example.android_movie_app.ToastType
import com.google.android.material.bottomsheet.BottomSheetDialog

class ShareDialog(
    private val context: Context,
    private val movieName: String,
    private val movieId: Int
) {
    fun show() {
        val dialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme).apply {
            setContentView(
                LayoutInflater.from(context).inflate(R.layout.layout_share_dialog, null)
            )
        }

        val edtLink = dialog.findViewById<EditText>(R.id.edtLink)
        val imgCopy = dialog.findViewById<ImageView>(R.id.imgCopy)
        val imgShare = dialog.findViewById<ImageView>(R.id.imgShape) // đúng id bạn đặt trong XML
        val btnClose = dialog.findViewById<ImageView>(R.id.close_button)

        val shareLink = "https://myapp.com/movies/$movieId"

        // Hiển thị link trong EditText
        edtLink?.setText(shareLink)

        // Nút sao chép link
        imgCopy?.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Movie Link", shareLink)
            clipboard.setPrimaryClip(clip)
            CustomToast.show(context, "Đã sao chép liên kết", ToastType.SUCCESS)
        }

        // Nút chia sẻ link
        imgShare?.setOnClickListener {
            val shareText = "Mình vừa xem phim \"$movieName\" trên MovieApp.\n" +
                    "Xem ngay tại: $shareLink"

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ phim hay 🎬")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Chia sẻ qua..."))
        }

        // Đóng dialog
        btnClose?.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
}