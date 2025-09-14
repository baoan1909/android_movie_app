package com.example.android_movie_app.dao

import android.content.ContentValues
import android.database.Cursor
import com.example.android_movie_app.Category
import com.example.android_movie_app.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Locale

class CategoryDAO(private val dbHelper: DatabaseHelper) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // ---------- ADD CATEGORY ----------
    fun addCategory(category: Category): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
            put("slug", category.slug)
            put("description", category.description)
            put("createdAt", category.createdAt?.let { dateFormat.format(it) })
        }
        return db.insert("categories", null, values)
    }

    // ---------- GET CATEGORY BY ID ----------
    fun getCategoryById(id: Int): Category? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM categories WHERE id=?", arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToCategory(it)
            }
        }
        return null
    }

    // ---------- GET CATEGORY BY SLUG ----------
    fun getCategoryBySlug(slug: String): Category? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM categories WHERE slug=?", arrayOf(slug))
        cursor.use {
            if (it.moveToFirst()) {
                return cursorToCategory(it)
            }
        }
        return null
    }

    // ---------- GET ALL CATEGORIES ----------
    fun getAllCategories(): List<Category> {
        val list = mutableListOf<Category>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM categories ORDER BY name ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(cursorToCategory(it))
            }
        }
        return list
    }

    // ---------- UPDATE CATEGORY ----------
    fun updateCategory(category: Category): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("name", category.name)
            put("slug", category.slug)
            put("description", category.description)
            put("createdAt", category.createdAt?.let { dateFormat.format(it) })
        }
        return db.update("categories", values, "id=?", arrayOf(category.id.toString()))
    }

    // ---------- DELETE CATEGORY ----------
    fun deleteCategory(id: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete("categories", "id=?", arrayOf(id.toString()))
    }

    // ---------- HELPER: Cursor -> Category ----------
    private fun cursorToCategory(cursor: Cursor): Category {
        return Category(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))
                ?.let { dateFormat.parse(it) }
        )
    }
}