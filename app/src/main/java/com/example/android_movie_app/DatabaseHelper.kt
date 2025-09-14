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
                rating DOUBLE DEFAULT 0,  -- 1-5
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP
            )
        """)

        // Chèn toàn bộ 14 movie
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (1, 'mot-paris-rat-sai', 'Một Paris rất... sai', 'The Wrong Paris', 'Dawn nghĩ rằng mình sẽ tham gia một chương trình hẹn hò ở Paris, Pháp, nhưng thực ra lại hạ cánh xuống Paris, Texas. Cô đã có một kế hoạch thoát thân - cho đến khi nảy sinh tình cảm với anh chàng cao bồi độc thân.', 'single', 'mot-paris-rat-sai-thumb.jpg', 'mot-paris-rat-sai-poster.jpg', 2025, 0, 0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (2, 'hoc-vien-ac-nhan', 'Học viện ác nhân', 'Kontrabida Academy', 'Khi chiếc TV bí ẩn đưa cô đến ngôi trường dành cho ác nhân trên màn ảnh, nhân viên nhà hàng nọ tìm thấy mục đích mới – và một cách để trả thù những kẻ thù của mình.', 'single', 'hoc-vien-ac-nhan-thumb.jpg', 'hoc-vien-ac-nhan-poster.jpg', 2025, 0, 8)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (3, 'khoang-cach-giua-doi-ta', 'Khoảng Cách Giữa Đôi Ta', 'Flat Girls', 'Trong khu chung cư của cảnh sát – nơi có hàng trăm gia đình cảnh sát cùng sinh sống – có Jane, một cô gái đến từ gia đình của một sĩ quan cấp cao. Mẹ cô là \"bà trùm\" cho vay nặng lãi trong khu chung cư, khiến ai cũng phải nể sợ. Trong khi đó, Ann là cô gái có cha đã hy sinh khi đang làm nhiệm vụ, khiến cô phải giúp mẹ làm việc kiếm sống và chăm sóc ba em nhỏ. Jane và Ann luôn chia sẻ mọi khoảnh khắc với nhau, lúc nào cũng kề vai sát cánh. Sau giờ tan học, họ thường cùng nhau chơi cầu lông ở sân giữa khu nhà, cùng với người bạn thân thiết là Nice. Jane luôn mong muốn được sống mãi trong khu chung cư này, với Ann luôn ở bên cạnh. Thế nhưng, mọi chuyện bắt đầu thay đổi khi Tong – một chàng cảnh sát trẻ mới chuyển đến – xuất hiện. Sự có mặt của anh không chỉ làm lung lay tình bạn giữa Jane và Ann, mà còn buộc cả hai phải đối mặt với sự thật cay đắng: Gia đình của Ann – một gia đình cảnh sát không còn người cha – có thể không còn nhiều thời gian để tiếp tục sống trong khu chung cư này.', 'single', 'khoang-cach-giua-doi-ta-thumb.jpg', 'khoang-cach-giua-doi-ta-poster.jpg', 2025, 0, 7)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (4, 'dam-me-duc-vong', 'Đam Mê Dục Vọng', 'Even Though I Don''t Like It', 'Phim kể về một phụ nữ trẻ đẹp, cô vốn là tiếp viên rượu ở quán Bar, trong một lần tiếp rượu cho khách cô bị ép rượu và may cho cô là được anh nhân viên cứu thoát thế nhưng anh ta vốn thích cô từ trước nên hôm đó anh đã dẫn cô về và làm tình với cô. Trong một lần kia 2 người cãi nhau thì có một thanh niên trẻ khác an ủi cô và cô cũng trao thân cho anh ta. Một người là anh tiếp viên ở quán rượu, luôn nóng nảy còn một người luôn bên cạnh cô, nhẹ nhàng và chu đáo với cô.', 'single', 'dam-me-duc-vong-thumb.jpg', 'dam-me-duc-vong-poster.jpg', 2016, 0, 4.9)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (5, 'war-blade', 'War Blade', 'War Blade', 'British operative Banks must rescue a French resistance fighter from a secret Nazi bunker during World War II. Outnumbered and outgunned, Banks and his allies must outwit the enemy and survive the deadly mission.', 'single', 'war-blade-thumb.jpg', 'war-blade-poster.jpg', 2024, 0, 4.5)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (6, 'hiep-khach-hanh-1982', 'Hiệp Khách Hành', 'Ode to Gallantry', 'Một võ sĩ đơn độc có biệt danh là \"Mongrel\" liên tục bị cuốn vào cuộc đấu tranh dữ dội giữa một số gia tộc võ thuật đối địch sau khi anh ta tình cờ tìm thấy Black Iron Token, thứ cho phép chủ sở hữu thực hiện bất kỳ điều ước nào được Xie Yanke, một bậc thầy kung fu tàn bạo, ban cho.', 'single', 'hiep-khach-hanh-1982-thumb.jpg', 'hiep-khach-hanh-1982-poster.jpg', 1982, 0, 5.4)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (7, 'vinh-barron', 'Vịnh Barron', 'Barron''s Cove', 'Khi cậu con trai nhỏ bị một bạn học sát hại dã man, người cha đau khổ với quá khứ bạo lực đã bắt cóc chính đứa trẻ gây tội. Hành động này châm ngòi cho một cuộc truy lùng điên cuồng, dẫn đầu bởi một chính trị gia đầy quyền lực — cũng chính là cha của cậu bé bị bắt cóc.', 'single', 'vinh-barron-thumb.jpg', 'vinh-barron-poster.jpg', 2025, 0, 6.5)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (8, 'vu-khi', 'Giờ Mất Tích', 'Weapons', 'Khi tất cả học sinh trong cùng một lớp bất ngờ biến mất trong cùng một đêm, vào đúng một thời điểm — chỉ trừ lại một em nhỏ duy nhất — cả cộng đồng rơi vào hoang mang tột độ, tự hỏi: ai… hoặc điều gì đứng sau sự biến mất bí ẩn ấy?', 'single', 'vu-khi-thumb.jpg', 'vu-khi-poster.jpg', 2025, 0, 7.48)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (9, 'bay-hoi-sinh', 'Bẫy Hồi Sinh', 'The Ghost Game', 'YouTuber đầy tham vọng Gi-ho dàn dựng một buổi cầu hồn giả để lan truyền nội dung, nhưng Ja-young đã tham gia, tìm kiếm người chị gái mất tích Seo-woo. Trong một bể chứa ngầm, Seo-woo tình nguyện trở thành \"người bị quỷ ám\", nhưng khi cô ngã xuống, nỗi kinh hoàng ập đến. Bị mắc kẹt bởi những thế lực đen tối, các học sinh phải đối mặt với số phận chết chóc, dẫn đến một bước ngoặt gây sốc.', 'single', 'bay-hoi-sinh-thumb.jpg', 'bay-hoi-sinh-poster.jpg', 2025, 0, 4)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (10, 'honey-dung', 'Honey Đừng!', 'Honey Don''t!', 'Honey O''Donahue, một nữ thám tử tư ở thị trấn nhỏ, bắt đầu điều tra hàng loạt cái chết kỳ lạ có liên quan đến một nhà thờ bí ẩn.', 'single', 'honey-dung-thumb.jpg', 'honey-dung-poster.jpg', 2025, 0, 5.8)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (11, 'plank-face', 'Plank Face', 'Plank Face', 'A man is taken captive by a feral family in the deep woods. They are determined to transform him, body and mind, into one of their own -- whether he likes it or not.', 'single', 'plank-face-thumb.jpg', 'plank-face-poster.jpg', 2016, 0, 5)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (12, 'bo-tu-sieu-dang-buoc-di-dau-tien', 'Bộ Tứ Siêu Đẳng: Bước Đi Đầu Tiên', 'The Fantastic 4: First Steps', 'Sau một chuyến bay thám hiểm vũ trụ, bốn phi hành gia bất ngờ sở hữu năng lực siêu nhiên và trở thành gia đình siêu anh hùng đầu tiên của Marvel. The Fantastic Four: First Steps là bộ phim mở đầu Kỷ nguyên anh hùng thứ sáu (Phase Six), đặt nền móng cho siêu bom tấn Avengers: Doomsday trong năm sau.', 'single', 'bo-tu-sieu-dang-buoc-di-dau-tien-thumb.jpg', 'bo-tu-sieu-dang-buoc-di-dau-tien-poster.jpg', 2025, 0, 7.195)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (13, 'chien-dich-blitz', 'Chiến Dịch Blitz', 'Blitz', 'Ở London trong Thế Chiến II, cậu bé chín tuổi George được mẹ, Rita, đưa đi sơ tán về vùng nông thôn để tránh các vụ đánh bom. Bất khuất và quyết tâm về với gia đình, George bước vào một hành trình gian truân để trở về nhà trong lúc Rita đi tìm cậu.', 'single', 'chien-dich-blitz-thumb.jpg', 'chien-dich-blitz-poster.jpg', 2024, 0, 5.991)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (14, 'banh-mi-and-hoa-hong', 'Bánh Mì & Hoa Hồng', 'Bread & Roses', 'Taliban dần lấy lại quyền lực, một nhóm phụ nữ Kabul dẫn đầu cuộc đấu tranh cách mạng vì quyền lợi và mạng sống của họ.', 'single', 'banh-mi-and-hoa-hong-thumb.jpg', 'banh-mi-and-hoa-hong-poster.jpg', 2024, 0, 6.393)")

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
