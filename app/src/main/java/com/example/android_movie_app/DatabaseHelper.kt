package com.example.android_movie_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "moviesDB", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // ------------------- USERS -------------------
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

        // ------------------- MOVIES -------------------
        db?.execSQL("""
            CREATE TABLE movies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                slug TEXT UNIQUE NOT NULL,
                name TEXT NOT NULL,
                originName TEXT,
                content TEXT,
                type TEXT CHECK(type IN ('single','series')) DEFAULT 'single',
                thumbUrl TEXT,
                posterUrl TEXT,
                year INTEGER,
                viewCount INTEGER DEFAULT 0,
                rating INTEGER DEFAULT 0,  -- 1-5
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """)

        // ------------------- CATEGORIES -------------------
        db?.execSQL("""
            CREATE TABLE categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE NOT NULL,
                slug TEXT UNIQUE NOT NULL,
                description TEXT,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """)

        // ------------------- MOVIE_CATEGORIES (junction) -------------------
        db?.execSQL("""
            CREATE TABLE movie_categories (
                movieId INTEGER NOT NULL,
                categoryId INTEGER NOT NULL,
                PRIMARY KEY(movieId, categoryId),
                FOREIGN KEY(movieId) REFERENCES movies(id) ON DELETE CASCADE,
                FOREIGN KEY(categoryId) REFERENCES categories(id) ON DELETE CASCADE
            )
        """)

        // ------------------- EPISODES -------------------
        db?.execSQL("""
            CREATE TABLE episodes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                movieId INTEGER NOT NULL,
                name TEXT NOT NULL,
                episodeNumber INTEGER DEFAULT 1,
                videoUrl TEXT NOT NULL,
                duration INTEGER,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(movieId) REFERENCES movies(id) ON DELETE CASCADE
            )
        """)

        // ------------------- USER FAVORITES -------------------
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

        // ------------------- WATCH PROGRESS -------------------
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

        // ------------------- USER SESSIONS -------------------
        db?.execSQL("""
            CREATE TABLE user_sessions (
                sessionToken TEXT PRIMARY KEY,
                userId INTEGER NOT NULL,
                expiresAt TEXT NOT NULL,
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
            )
        """)

        // ------------------- REVIEWS -------------------
        db?.execSQL("""
            CREATE TABLE reviews (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                movieId INTEGER NOT NULL,
                episodeId INTEGER,
                rating INTEGER CHECK(rating >= 1 AND rating <= 5),
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
                updatedAt TEXT,
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(movieId) REFERENCES movies(id) ON DELETE CASCADE,
                FOREIGN KEY(episodeId) REFERENCES episodes(id) ON DELETE CASCADE,
                UNIQUE(userId, movieId, episodeId)
            )
        """)

        // ------------------- COMMENTS -------------------
        db?.execSQL("""
            CREATE TABLE comments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                movieId INTEGER NOT NULL,
                episodeId INTEGER,
                parentCommentId INTEGER,
                content TEXT NOT NULL,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(movieId) REFERENCES movies(id) ON DELETE CASCADE,
                FOREIGN KEY(episodeId) REFERENCES episodes(id) ON DELETE CASCADE,
                FOREIGN KEY(parentCommentId) REFERENCES comments(id) ON DELETE CASCADE
            )
        """)

        // ------------------- VIEW: movies_with_categories -------------------
        db?.execSQL("""
            CREATE VIEW movies_with_categories AS
                SELECT 
                    m.id AS movieId,
                    m.slug,
                    m.name,
                    m.originName,
                    m.type,
                    m.thumbUrl,
                    m.posterUrl,
                    m.year,
                    m.rating,
                    m.createdAt,
                    GROUP_CONCAT(c.name, ', ') AS categories
                FROM movies m
                LEFT JOIN movie_categories mc ON m.id = mc.movieId
                LEFT JOIN categories c ON mc.categoryId = c.id
                GROUP BY m.id;
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop all tables and views
        db?.execSQL("DROP VIEW IF EXISTS movies_with_categories")
        db?.execSQL("DROP TABLE IF EXISTS comments")
        db?.execSQL("DROP TABLE IF EXISTS reviews")
        db?.execSQL("DROP TABLE IF EXISTS user_sessions")
        db?.execSQL("DROP TABLE IF EXISTS watch_progress")
        db?.execSQL("DROP TABLE IF EXISTS user_favorites")
        db?.execSQL("DROP TABLE IF EXISTS episodes")
        db?.execSQL("DROP TABLE IF EXISTS movie_categories")
        db?.execSQL("DROP TABLE IF EXISTS categories")
        db?.execSQL("DROP TABLE IF EXISTS movies")
        db?.execSQL("DROP TABLE IF EXISTS users")

        onCreate(db)
    }
}
