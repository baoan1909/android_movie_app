package com.example.android_movie_app

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "moviesDB", null, 1) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Bảng Users
        db?.execSQL("""
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                passwordHash TEXT NOT NULL,
                displayName TEXT,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
                isActive INTEGER DEFAULT 1
            )
        """)

        // Bảng Movies
        db?.execSQL("""
            CREATE TABLE movies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                slug TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                originName TEXT,
                content TEXT,
                type TEXT CHECK(type IN ('single','series')),
                thumbUrl TEXT,
                posterUrl TEXT,
                year INTEGER,
                viewCount INTEGER DEFAULT 0,
                rating REAL DEFAULT 0.0,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """)

        // Bảng Episodes
        db?.execSQL("""
            CREATE TABLE episodes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                movieId INTEGER NOT NULL,
                name TEXT NOT NULL,
                episodeNumber INTEGER,
                videoUrl TEXT NOT NULL,
                duration INTEGER,
                viewCount INTEGER DEFAULT 0,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(movieId) REFERENCES movies(id) ON DELETE CASCADE
            )
        """)

        // Bảng User Favorites
        db?.execSQL("""
            CREATE TABLE user_favorites (
                userId INTEGER NOT NULL,
                movieId INTEGER NOT NULL,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(userId, movieId),
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(movieId) REFERENCES movies(id) ON DELETE CASCADE
            )
        """)

        // Bảng Watch Progress
        db?.execSQL("""
            CREATE TABLE watch_progress (
                userId INTEGER NOT NULL,
                movieId INTEGER NOT NULL,
                episodeId INTEGER,
                currentTime INTEGER DEFAULT 0,
                totalTime INTEGER DEFAULT 0,
                isCompleted INTEGER DEFAULT 0,
                lastWatchedAt TEXT DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(userId, movieId, episodeId),
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(movieId) REFERENCES movies(id) ON DELETE CASCADE,
                FOREIGN KEY(episodeId) REFERENCES episodes(id) ON DELETE SET NULL
            )
        """)

        // Bảng User Sessions
        db?.execSQL("""
            CREATE TABLE user_sessions (
                sessionToken TEXT PRIMARY KEY,
                userId INTEGER NOT NULL,
                expiresAt TEXT NOT NULL,
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS user_sessions")
        db?.execSQL("DROP TABLE IF EXISTS watch_progress")
        db?.execSQL("DROP TABLE IF EXISTS user_favorites")
        db?.execSQL("DROP TABLE IF EXISTS episodes")
        db?.execSQL("DROP TABLE IF EXISTS movies")
        db?.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // ---------------- Helper ----------------
    public fun cursorToMovie(cursor: Cursor): Movie {
        return Movie(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            slug = cursor.getString(cursor.getColumnIndexOrThrow("slug")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            originName = cursor.getString(cursor.getColumnIndexOrThrow("originName")),
            content = cursor.getString(cursor.getColumnIndexOrThrow("content")),
            type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
            thumbUrl = cursor.getString(cursor.getColumnIndexOrThrow("thumbUrl")),
            posterUrl = cursor.getString(cursor.getColumnIndexOrThrow("posterUrl")),
            year = if (!cursor.isNull(cursor.getColumnIndexOrThrow("year"))) cursor.getInt(cursor.getColumnIndexOrThrow("year")) else null,
            viewCount = cursor.getInt(cursor.getColumnIndexOrThrow("viewCount")),
            rating = cursor.getDouble(cursor.getColumnIndexOrThrow("rating")),
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))?.let {
                dateFormat.parse(it)
            }
        )
    }

    public fun cursorToUser(cursor: Cursor): User {
        return User(
            id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
            username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            passwordHash = cursor.getString(cursor.getColumnIndexOrThrow("passwordHash")),
            displayName = cursor.getString(cursor.getColumnIndexOrThrow("displayName")),
            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))?.let { d -> dateFormat.parse(d) },
            isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive")) == 1
        )
    }
}
