package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.android_movie_app.Comment
import com.example.android_movie_app.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.get

class CommentDAO(private val dbHelper: DatabaseHelper) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ---------- ADD COMMENT ----------
    fun addComment(comment: Comment): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", comment.userId)
            put("movieId", comment.movieId)
            put("episodeId", comment.episodeId)
            put("parentCommentId", comment.parentCommentId)
            put("content", comment.content)
            put("createdAt", comment.createdAt?.let { dateFormat.format(it) })
        }
        return db.insert("comments", null, values)
    }

    // ---------- COMMENT NODE WITH USER ----------
    data class CommentNodeUI(
        val comment: Comment,
        val username: String,
        val displayName: String?,
        val replies: MutableList<CommentNodeUI> = mutableListOf()
    )

    // ---------- GET COMMENT TREE WITH USER INFO BY MOVIE ----------
    fun getCommentTreeByMovieWithUser(movieId: Int): List<CommentNodeUI> {
        val allComments = getAllCommentsForMovieWithUser(movieId)
        return buildCommentTreeWithUser(allComments)
    }

    // ---------- GET COMMENT TREE WITH USER INFO BY EPISODE ----------
    fun getCommentTreeByEpisodeWithUser(episodeId: Int): List<CommentNodeUI> {
        val allComments = getAllCommentsForEpisodeWithUser(episodeId)
        return buildCommentTreeWithUser(allComments)
    }

    // ---------- HELPER: Lấy tất cả comment + user ----------
    private data class CommentWithUser(
        val comment: Comment,
        val username: String,
        val displayName: String?
    )

    private fun getAllCommentsForMovieWithUser(movieId: Int): List<CommentWithUser> {
        val list = mutableListOf<CommentWithUser>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT c.*, u.username, u.displayName
            FROM comments c
            JOIN users u ON c.userId = u.id
            WHERE c.movieId=?
            ORDER BY c.createdAt ASC
            """.trimIndent(),
            arrayOf(movieId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToCommentWithUser(it))
            }
        }
        return list
    }

    private fun getAllCommentsForEpisodeWithUser(episodeId: Int): List<CommentWithUser> {
        val list = mutableListOf<CommentWithUser>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            """
            SELECT c.*, u.username, u.displayName
            FROM comments c
            JOIN users u ON c.userId = u.id
            WHERE c.episodeId=?
            ORDER BY c.createdAt ASC
            """.trimIndent(),
            arrayOf(episodeId.toString())
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToCommentWithUser(it))
            }
        }
        return list
    }

    // ---------- HELPER: Build tree với user ----------
    private fun buildCommentTreeWithUser(comments: List<CommentWithUser>): List<CommentNodeUI> {
        val map = mutableMapOf<Int, CommentNodeUI>()
        val roots = mutableListOf<CommentNodeUI>()

        // Tạo map commentId -> CommentNodeUI
        for (c in comments) {
            val node = CommentNodeUI(c.comment, c.username, c.displayName)
            map[c.comment.id] = node
        }

        // Gắn replies vào parent
        for (c in comments) {
            val node = map[c.comment.id]!!
            if (c.comment.parentCommentId != null) {
                val parentNode = map[c.comment.parentCommentId]
                parentNode?.replies?.add(node)
            } else {
                roots.add(node)
            }
        }

        return roots
    }

    // ---------- HELPER: Cursor -> CommentWithUser ----------
    private fun cursorToCommentWithUser(cursor: Cursor): CommentWithUser {
        val comment = Comment(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
            movieId = cursor.getInt(cursor.getColumnIndexOrThrow("movieId")),
            episodeId = if (!cursor.isNull(cursor.getColumnIndexOrThrow("episodeId"))) cursor.getInt(
                cursor.getColumnIndexOrThrow("episodeId")
            ) else null,
            parentCommentId = if (!cursor.isNull(cursor.getColumnIndexOrThrow("parentCommentId"))) cursor.getInt(
                cursor.getColumnIndexOrThrow("parentCommentId")
            ) else null,
            content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))
                ?.let { dateFormat.parse(it) }
        )
        val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
        val displayName = cursor.getString(cursor.getColumnIndexOrThrow("displayName"))
        return CommentWithUser(comment, username, displayName)
    }

    // ---------- DELETE COMMENT ----------
    fun deleteComment(commentId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("comments", "id=?", arrayOf(commentId.toString()))
    }
}
