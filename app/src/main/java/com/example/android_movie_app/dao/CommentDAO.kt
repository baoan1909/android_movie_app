package com.example.android_movie_app.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.android_movie_app.Comment
import com.example.android_movie_app.CommentWithUser
import com.example.android_movie_app.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class CommentDAO(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())


    fun insertComment(comment: Comment): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("userId", comment.userId)
            put("movieId", comment.movieId)
            put("episodeId", comment.episodeId)
            put("parentCommentId", comment.parentCommentId)
            put("content", comment.content)
            // createdAt sẽ để DB tự set CURRENT_TIMESTAMP
        }
        return db.insert("comments", null, values)
    }

    fun getCommentsByMovieId(movieId: Int): List<CommentWithUser> {
        val parentComments = mutableListOf<CommentWithUser>()
        val db = dbHelper.readableDatabase

        val sql = """
        SELECT c.id, c.userId, u.avatarPath, u.username, c.movieId, c.episodeId,
               c.parentCommentId, c.content, c.createdAt
        FROM comments c
        INNER JOIN users u ON c.userId = u.id
        WHERE c.movieId = ? AND c.parentCommentId IS NULL
        ORDER BY c.createdAt ASC
    """

        db.rawQuery(sql, arrayOf(movieId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val createdAtStr = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))
                    val createdAt = try { dateFormat.parse(createdAtStr) } catch (_: Exception) { null }

                    val parent = CommentWithUser(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                        avatarPath = cursor.getString(cursor.getColumnIndexOrThrow("avatarPath")),
                        username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        movieId = cursor.getInt(cursor.getColumnIndexOrThrow("movieId")),
                        episodeId = if (cursor.isNull(cursor.getColumnIndexOrThrow("episodeId"))) null
                        else cursor.getInt(cursor.getColumnIndexOrThrow("episodeId")),
                        parentCommentId = null,
                        content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        createdAt = createdAt,
                        replies = getRepliesByParentId(cursor.getInt(cursor.getColumnIndexOrThrow("id"))).toMutableList(),
                        isRepliesVisible = true
                    )

                    parentComments.add(parent)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return parentComments
    }


    // Lấy danh sách reply theo parentCommentId
    fun getRepliesByParentId(parentCommentId: Int): List<CommentWithUser> {
        val replies = mutableListOf<CommentWithUser>()
        val db: SQLiteDatabase = dbHelper.readableDatabase

        val sql = """
        SELECT c.id, c.userId, u.avatarPath, u.username, c.movieId, c.episodeId,
               c.parentCommentId, c.content, c.createdAt
        FROM comments c
        INNER JOIN users u ON c.userId = u.id
        WHERE c.parentCommentId = ?
        ORDER BY c.createdAt ASC
    """

        db.rawQuery(sql, arrayOf(parentCommentId.toString())).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val createdAtStr = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))
                    val createdAt = try {
                        dateFormat.parse(createdAtStr)
                    } catch (_: Exception) {
                        null
                    }

                    val reply = CommentWithUser(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        userId = cursor.getInt(cursor.getColumnIndexOrThrow("userId")),
                        avatarPath = cursor.getString(cursor.getColumnIndexOrThrow("avatarPath")),
                        username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        movieId = cursor.getInt(cursor.getColumnIndexOrThrow("movieId")),
                        episodeId = if (cursor.isNull(cursor.getColumnIndexOrThrow("episodeId"))) null
                        else cursor.getInt(cursor.getColumnIndexOrThrow("episodeId")),
                        parentCommentId = if (cursor.isNull(cursor.getColumnIndexOrThrow("parentCommentId"))) null
                        else cursor.getInt(cursor.getColumnIndexOrThrow("parentCommentId")),
                        content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
                        createdAt = createdAt
                    )
                    replies.add(reply)
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return replies
    }


}
