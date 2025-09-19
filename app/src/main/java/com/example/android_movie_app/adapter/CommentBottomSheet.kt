package com.example.android_movie_app.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android_movie_app.Comment
import com.example.android_movie_app.CommentWithUser
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.R
import com.example.android_movie_app.dao.UserDAO
import com.example.android_movie_app.dao.UserSessionDAO
import com.example.android_movie_app.data.CommentDAO
import com.example.android_movie_app.databinding.LayoutCommentBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class CommentBottomSheet(private val movieId: Int) : BottomSheetDialogFragment() {

    private var _binding: LayoutCommentBinding? = null
    private val binding get() = _binding!!

    private lateinit var commentDAO: CommentDAO
    private lateinit var adapter: CommentAdapter
    private var comments: MutableList<CommentWithUser> = mutableListOf()

    // Comment đang reply
    private var replyingToComment: CommentWithUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentDAO = CommentDAO(requireContext())

        setupRecyclerView()
        loadComments()

        // Đóng popup
        binding.buttonClose.setOnClickListener { dismiss() }

        // Gửi comment / reply
        binding.buttonSend.setOnClickListener { insertComment() }

        // Hủy reply
        binding.buttonCancelReply.setOnClickListener { cancelReply() }
    }

    private fun setupRecyclerView() {
        adapter = CommentAdapter(requireContext(), comments)

        // Callback khi nhấn reply
        adapter.setOnReplyClickListener { comment ->
            replyingToComment = comment
            showReplyKeyboard(comment)
        }

        binding.recyclerViewComments.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewComments.adapter = adapter
    }

    private fun loadComments() {
        comments.clear()
        comments.addAll(commentDAO.getCommentsByMovieId(movieId))
        adapter.updateData(comments)
        binding.textViewTitle.text = "Bình luận (${comments.size})"
    }

    private fun insertComment() {
        val content = binding.editTextComment.text.toString().trim()
        if (content.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show()
            return
        }

        val sessionDAO = UserSessionDAO(DatabaseHelper(requireContext()))
        val session = sessionDAO.getLatestValidSession()
        val userId = session?.userId
        if (userId == null) {
            Toast.makeText(requireContext(), "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show()
            return
        }

        val userDAO = UserDAO(DatabaseHelper(requireContext()))
        val user = userDAO.getUserById(userId)
        if (user == null) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            return
        }

        val newComment = Comment(
            id = 0,
            movieId = movieId,
            userId = userId,
            content = content,
            parentCommentId = replyingToComment?.id,
            createdAt = Date()
        )

        val insertedId = commentDAO.insertComment(newComment)
        if (insertedId == -1L) {
            Toast.makeText(requireContext(), "Không thể gửi bình luận", Toast.LENGTH_SHORT).show()
            return
        }

        val newCommentWithUser = CommentWithUser(
            id = insertedId.toInt(),
            userId = userId,
            avatarUrl = user.avatarUrl ?: "",
            username = user.username,
            movieId = movieId,
            episodeId = null,
            parentCommentId = replyingToComment?.id,
            content = content,
            createdAt = newComment.createdAt
        )

        if (replyingToComment != null) {
            // Thêm reply vào comment cha
            replyingToComment!!.replies = (replyingToComment!!.replies ?: mutableListOf()).apply {
                add(newCommentWithUser)
            }
            replyingToComment!!.isRepliesVisible = true

            // Cập nhật adapter cho comment cha
            val parentIndex = comments.indexOfFirst { it.id == replyingToComment!!.id }
            if (parentIndex != -1) {
                adapter.notifyItemChanged(parentIndex)
                // Scroll tới comment cha
                binding.recyclerViewComments.post {
                    val layoutManager = binding.recyclerViewComments.layoutManager as LinearLayoutManager
                    layoutManager.scrollToPositionWithOffset(parentIndex, 0)
                }
            }
        } else {
            // Comment mới
            comments.add(0, newCommentWithUser)
            adapter.notifyItemInserted(0)
            binding.recyclerViewComments.scrollToPosition(0)
        }

        // Clear input
        binding.editTextComment.text?.clear()
        cancelReply()
    }

    private fun showReplyKeyboard(comment: CommentWithUser) {
        binding.replyInfoContainer.visibility = View.VISIBLE
        binding.textViewReplyInfo.text = "Đang trả lời @${comment.username ?: "User"}"
        binding.editTextComment.hint = "Nhập trả lời của bạn..."

        binding.editTextComment.post {
            binding.editTextComment.requestFocus()

            // Scroll tới comment cha
            val index = comments.indexOfFirst { it.id == comment.id }
            if (index != -1) {
                (binding.recyclerViewComments.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(index, 0)
            }

            // Mở keyboard
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.editTextComment, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun cancelReply() {
        replyingToComment = null
        binding.replyInfoContainer.visibility = View.GONE
        binding.editTextComment.hint = "Gửi bình luận..."
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            val playerBottom = (activity?.findViewById<FrameLayout>(R.id.playerContainer))?.bottom ?: 0
            val height = resources.displayMetrics.heightPixels - playerBottom
            it.layoutParams.height = height
            it.requestLayout()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
