package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.android_movie_app.DatabaseHelper
import com.example.android_movie_app.User
import java.text.SimpleDateFormat
import java.util.*

class UserDAO(private val dbHelper: DatabaseHelper) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ---------- CREATE ----------
    fun addUser(user: User): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", user.username)
            put("email", user.email)
            put("passwordHash", user.passwordHash)
            put("createdAt", user.createdAt?.let { dateFormat.format(it) })
            put("isActive", if (user.isActive) 1 else 0)
            put("avatarPath", user.avatarPath)
        }
        return db.insert("users", null, values)
    }

    // ---------- READ ----------
    fun getUserByUsername(username: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username=?", arrayOf(username))
        cursor.use {
            if (it.moveToFirst()) return cursorToUser(it)
        }
        return null
    }

    fun getUserById(id: Int): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE id=?", arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) return cursorToUser(it)
        }
        return null
    }

    fun getAllUsers(): List<User> {
        val list = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users ORDER BY id DESC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToUser(it))
            }
        }
        return list
    }

    // ---------- UPDATE ----------
    fun updateUser(user: User): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", user.username)
            put("email", user.email)
            put("passwordHash", user.passwordHash)
            put("avatarPath", user.avatarPath)
            put("isActive", if (user.isActive) 1 else 0)
            put("createdAt", user.createdAt?.let { dateFormat.format(it) })
        }
        return db.update("users", values, "id=?", arrayOf(user.id.toString()))
    }

    fun updateUserStatus(id: Int, isActive: Boolean): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("isActive", if (isActive) 1 else 0)
        }
        return db.update("users", values, "id=?", arrayOf(id.toString()))
    }

    // ---------- DELETE ----------
    fun deleteUser(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("users", "id=?", arrayOf(id.toString()))
    }


    //Update Avatar
    fun updateUserAvatar(id: Int, avatarPath: String): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("avatarPath", avatarPath)
        }
        return db.update("users", values, "id=?", arrayOf(id.toString()))
    }

    // ---------- UTILITY ----------
    fun cursorToUser(cursor: Cursor): User {
        // Cột bắt buộc
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
        val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
        val passwordHash = cursor.getString(cursor.getColumnIndexOrThrow("passwordHash"))
        val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive")) == 1

        // Cột tùy chọn
        val avatarPath = cursor.getColumnIndex("avatarPath").let { if (it != -1) cursor.getString(it) ?: "" else "" }
        val createdAt = cursor.getColumnIndex("createdAt").let {
            if (it != -1) cursor.getString(it)?.let { d -> dateFormat.parse(d) } else null
        }

        return User(
            id = id,
            avatarPath = avatarPath,
            username = username,
            email = email,
            passwordHash = passwordHash,
            createdAt = createdAt,
            isActive = isActive
        )
    }
}
