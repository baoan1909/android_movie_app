package com.example.android_movie_app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

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
                avatarPath TEXT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                passwordHash TEXT NOT NULL,
                createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
                isActive INTEGER DEFAULT 1
            )
        """)

        // Seed default user for convenience login during development
        // username: diyuyi, password: Diyuyi@123 (store SHA-256 like LoginActivity)
        val seedPasswordHash = MessageDigest
            .getInstance("SHA-256")
            .digest("Diyuyi@123".toByteArray())
            .joinToString("") { "%02x".format(it) }
        db?.execSQL(
            "INSERT INTO users ( avatarPath, username, email, passwordHash, isActive) VALUES ('ic_account_circle','diyuyi', 'diyuyi@example.com', '$seedPasswordHash', 1)"
        )

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
                rating DOUBLE DEFAULT 0.0,  -- 1-5
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

        // Adding new movies from học đường category
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (15, 'i-am-your-king-1', 'I Am Your King 1', 'I Am Your King 1', 'Một câu chuyện tình yêu đầy cảm xúc trong môi trường học đường với những bài học về tình bạn và tình yêu.', 'series', 'i-am-your-king-1-thumb.jpg', 'i-am-your-king-1-poster.jpg', 2023, 0, 7.5)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (16, 'star-struck-truy-tinh', 'Star Struck: Truy Tinh', 'Star Struck', 'Câu chuyện về tình yêu học đường giữa những ngôi sao trẻ trong ngành giải trí.', 'series', 'star-struck-truy-tinh-thumb.jpg', 'star-struck-truy-tinh-poster.jpg', 2023, 0, 6.4)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (17, 'chap-canh', 'Chắp Cánh', 'Fly, Again', 'Một câu chuyện cảm động về những ước mơ bay cao và tình yêu tuổi học trò.', 'series', 'chap-canh-thumb.jpg', 'chap-canh-poster.jpg', 2021, 0, 3.7)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (18, 'day-khong-phai-la-keo-co-sao', 'Đây Không Phải Là Kéo Co Sao?', 'Let''s Tug It!', 'Một bộ phim hài hước về cuộc sống học đường đầy màu sắc.', 'series', 'day-khong-phai-la-keo-co-sao--thumb.jpg', 'day-khong-phai-la-keo-co-sao--poster.jpg', 2023, 0, 5.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (19, 'chang-phan-dien-de-thuong', 'Chàng Phản Diện Dễ Thương', 'Cute Bad Guy', 'Câu chuyện về một chàng trai có vẻ ngoài phản diện nhưng thực chất rất dễ thương trong môi trường học đường.', 'series', 'chang-phan-dien-de-thuong-thumb.jpg', 'chang-phan-dien-de-thuong-poster.jpg', 2023, 0, 6.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (20, 'birdie-wing-golf-girls-story-season-2', 'BIRDIE WING -Golf Girls'' Story- Season 2', 'BIRDIE WING -Golf Girls'' Story- Season 2', 'Phần 2 của câu chuyện về những cô gái chơi golf với những ước mơ và tình bạn đẹp.', 'series', 'birdie-wing-golf-girls-story-season-2-thumb.jpg', 'birdie-wing-golf-girls-story-season-2-poster.jpg', 2023, 0, 6.6)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (21, 'vi-nho-reminders', 'Vì Nhớ (ReminderS)', 'ReminderS', 'Một câu chuyện ngắn về những kỷ niệm đẹp trong thời học sinh.', 'series', 'vi-nho-reminders-thumb.jpg', 'vi-nho-reminders-poster.jpg', 2023, 0, 7.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (22, 'alice-gear-aegis-expansion', 'Alice Gear Aegis Expansion', 'アリス・ギア・アイギス Expansion', 'Anime về những cô gái chiến đấu với thiết bị Alice Gear trong môi trường học đường.', 'series', 'alice-gear-aegis-expansion-thumb.jpg', 'alice-gear-aegis-expansion-poster.jpg', 2023, 0, 5.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (23, 'sau-khi-co-duoc-nang-luc-ba-dao-o-di-gioi-toi-cung-vo-doi-o-the-gioi-thuc-thang-cap-xong-thi-cuoc-doi-cung-thay-doi', 'Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~', '異世界でチート能力を手にした俺は、現実世界をも無双する ～レベルアップは人生を変えた～', 'Câu chuyện về một học sinh có được sức mạnh từ dị giới và áp dụng vào cuộc sống thực.', 'series', 'sau-khi-co-duoc-nang-luc-ba-dao-o-di-gioi-toi-cung-vo-doi-o-the-gioi-thuc-thang-cap-xong-thi-cuoc-doi-cung-thay-doi-thumb.jpg', 'sau-khi-co-duoc-nang-luc-ba-dao-o-di-gioi-toi-cung-vo-doi-o-the-gioi-thuc-thang-cap-xong-thi-cuoc-doi-cung-thay-doi-poster.jpg', 2023, 0, 8.245)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (24, 'the-dangers-in-my-heart', 'The Dangers in My Heart', '僕の心のヤバイやつ', 'Anime về tình yêu học đường với những cảm xúc phức tạp của tuổi mới lớn.', 'series', 'the-dangers-in-my-heart-thumb.jpg', 'the-dangers-in-my-heart-poster.jpg', 2023, 0, 7.8)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (25, 'yeu-yamada-o-lv999', 'Yêu Yamada ở Lv999!', 'My Love Story With Yamada-kun at Lv999', 'Câu chuyện tình yêu dễ thương giữa game thủ và cô gái trong môi trường đại học.', 'series', 'yeu-yamada-o-lv999d-thumb.jpg', 'yeu-yamada-o-lv999d-poster.jpg', 2023, 0, 8.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (26, 'khoa-hoc-ve-tinh-yeu', 'Khoa Học Về Tình Yêu', 'The Science of Falling in Love', 'Drama về việc nghiên cứu khoa học về tình yêu trong môi trường đại học.', 'series', 'khoa-hoc-ve-tinh-yeu-thumb.jpg', 'khoa-hoc-ve-tinh-yeu-poster.jpg', 2023, 0, 4.7)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (27, 'giai-dieu-tren-trien-doc', 'Giai Điệu Trên Triền Dốc', 'Kids on the Slope Sakamichi no Apollon', 'Anime về tình bạn và âm nhạc jazz trong thời kỳ học sinh.', 'series', 'giai-dieu-tren-trien-doc-thumb.jpg', 'giai-dieu-tren-trien-doc-poster.jpg', 2012, 0, 8.5)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (28, 'roboticsnotes', 'Robotics;Notes', 'ROBOTICS;NOTES', 'Anime về việc chế tạo robot trong câu lạc bộ robotics của trường học.', 'series', 'roboticsnotes-thumb.jpg', 'roboticsnotes-poster.jpg', 2012, 0, 6.8)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (29, 'than-tham-tron-hoc', 'Thần Thám Trốn Học', 'DETECTIVE CHEN', 'Phim về một thám tử trẻ giải quyết các vụ án trong môi trường học đường.', 'single', 'than-tham-tron-hoc-thumb.jpg', 'than-tham-tron-hoc-poster.jpg', 2022, 0, 3.3)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (30, 'nhat-quy-nhi-ma-thu-ba-takagi-movie', 'Nhất Quỷ Nhì Ma, Thứ Ba Takagi - Movie', '劇場版 からかい上手の高木さん', 'Phim điện ảnh về câu chuyện tình yêu học trò đầy hài hước.', 'single', 'nhat-quy-nhi-ma-thu-ba-takagi-movie-thumb.jpg', 'nhat-quy-nhi-ma-thu-ba-takagi-movie-poster.jpg', 2023, 0, 8.4)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (31, 'kubo-khong-de-toi-vo-hinh', 'Kubo Không Để Tôi Vô Hình', 'Kubo Won''t Let Me Be Invisible', 'Anime về một cậu học sinh nhút nhát và cô bạn luôn chú ý đến cậu.', 'series', 'kubo-khong-de-toi-vo-hinh-thumb.jpg', 'kubo-khong-de-toi-vo-hinh-poster.jpg', 2023, 0, 6.778)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (32, 'the-angel-next-door-spoils-me-rotten', 'Thiên Sứ nhà bên', 'The Angel Next Door Spoils Me Rotten', 'Anime về mối quan hệ giữa một học sinh lười biếng và cô hàng xóm hoàn hảo.', 'series', 'the-angel-next-door-spoils-me-rotten-thumb.jpg', 'the-angel-next-door-spoils-me-rotten-poster.jpg', 2023, 0, 7.7)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (33, 'the-misfit-of-demon-king-academy-ii', 'Kẻ Ngoại Lai Ở Học Viện Ma Vương (Phần 2)', 'The Misfit of Demon King Academy Ⅱ: Raja Iblis Terkuat dalam Sejarah Bereinkarnasi dan Bersekolah dengan Keturunannya', 'Phần 2 của câu chuyện về ma vương học tại học viện dành cho con cháu của mình.', 'series', 'the-misfit-of-demon-king-academy-ii-thumb.jpg', 'the-misfit-of-demon-king-academy-ii-poster.jpg', 2023, 0, 8.475)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (34, 'dont-toy-with-me-miss-nagatoro-2nd-attack', 'Đừng Chọc Anh Nữa Mà, Nagatoro! Phần 2', 'Don''t Toy with Me, Miss Nagatoro 2nd Attack', 'Phần 2 của câu chuyện hài hước về Nagatoro và senpai của cô.', 'series', 'dont-toy-with-me-miss-nagatoro-2nd-attack-thumb.jpg', 'dont-toy-with-me-miss-nagatoro-2nd-attack-poster.jpg', 2023, 0, 7.5)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (35, 'spy-classroom', 'Lớp Học Điệp Viên', 'Spy Classroom', 'Anime về một lớp học đào tạo điệp viên với những nhiệm vụ nguy hiểm.', 'series', 'spy-classroom-thumb.jpg', 'spy-classroom-poster.jpg', 2023, 0, 6.406)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (36, 'ky-luc-la-ve-ky-si-khong-dau', 'kỷ lục lạ về kỵ sĩ không đầu', 'The tale of the headless horseman', 'Anime về những câu chuyện kỳ bí trong môi trường học đường.', 'series', 'ky-luc-la-ve-ky-si-khong-dau-thumb.jpg', 'ky-luc-la-ve-ky-si-khong-dau-poster.jpg', 2010, 0, 7.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (37, 'xin-chao-ngay-hom-qua', 'Xin Chào, Ngày Hôm Qua', 'Never Grow Old', 'Drama về tình yêu học đường và những kỷ niệm tuổi trẻ.', 'series', 'xin-chao-ngay-hom-qua-thumb.jpg', 'xin-chao-ngay-hom-qua-poster.jpg', 2022, 0, 2.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (38, 'mat-ma-lech-lac', 'Mật Mã Lệch Lạc', 'The Warp Effect', 'Drama hài về cuộc sống học đường với những tình huống bất ngờ.', 'series', 'mat-ma-lech-lac-thumb.jpg', 'mat-ma-lech-lac-poster.jpg', 2022, 0, 8.4)")

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

        db?.execSQL("""
            INSERT INTO categories (name, slug) VALUES
            ('Hành Động', 'hanh-dong'),
            ('Tình Cảm', 'tinh-cam'),
            ('Hài Hước', 'hai-huoc'),
            ('Cổ Trang', 'co-trang'),
            ('Tâm Lý', 'tam-ly'),
            ('Hình Sự', 'hinh-su'),
            ('Chiến Tranh', 'chien-tranh'),
            ('Thể Thao', 'the-thao'),
            ('Võ Thuật', 'vo-thuat'),
            ('Viễn Tưởng', 'vien-tuong'),
            ('Phiêu Lưu', 'phieu-luu'),
            ('Khoa Học', 'khoa-hoc'),
            ('Kinh Dị', 'kinh-di'),
            ('Âm Nhạc', 'am-nhac'),
            ('Thần Thoại', 'than-thoai'),
            ('Tài Liệu', 'tai-lieu'),
            ('Gia Đình', 'gia-dinh'),
            ('Chính kịch', 'chinh-kich'),
            ('Bí ẩn', 'bi-an'),
            ('Học Đường', 'hoc-duong'),
            ('Kinh Điển', 'kinh-dien'),
            ('Phim 18+', 'phim-18')
        """.trimIndent())

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

        db?.execSQL("""
            INSERT INTO episodes (id, movieId, name, episodeNumber, videoUrl, duration) VALUES
            (1, 1, 'Full', 1, 'https://vip.opstream90.com/20250913/12223_004881eb/index.m3u8', 6300),
            (2, 2, 'Full', 1, 'https://vip.opstream90.com/20250913/12215_422e42d0/index.m3u8', 6480),
            (3, 3, 'Full', 1, 'https://vip.opstream90.com/20250912/12188_1730f69e/index.m3u8', 7800),
            (4, 4, 'Full', 1, 'https://vip.opstream12.com/20250911/28032_e4160531/index.m3u8', 5100),
            (5, 5, 'Full', 1, 'https://vip.opstream12.com/20250911/28033_59089496/index.m3u8', 5640),
            (6, 6, 'Full', 1, 'https://vip.opstream13.com/20250911/7082_3bf29f38/index.m3u8', 4980),
            (7, 7, 'Full', 1, 'https://vip.opstream90.com/20250911/12139_c5c548e4/index.m3u8', 6960),
            (8, 8, 'Full', 1, 'https://vip.opstream90.com/20250911/12133_37e01843/index.m3u8', 7740),
            (9, 9, 'Full', 1, 'https://vip.opstream10.com/20250910/30392_63e8e953/index.m3u8', 5700),
            (10, 10, 'Full', 1, 'https://vip.opstream90.com/20250910/12126_4bdb6179/index.m3u8', 5340),
            (11, 11, 'Full', 1, 'https://vip.opstream12.com/20250909/28028_604ee636/index.m3u8', 5460),
            (12, 12, 'Full', 1, 'https://vip.opstream90.com/20250909/12084_8caa4453/index.m3u8', 6900),
            (13, 13, 'Full', 1, 'https://vip.opstream12.com/20250907/28026_21353d3d/index.m3u8', 7200),
            (14, 14, 'Full', 1, 'https://vip.opstream12.com/20250907/28027_487cbe45/index.m3u8', 5400)
        """.trimIndent())

        // Episodes for new movies
        // I Am Your King 1 (movieId: 15) - 5 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (15, 'Tập 1', 1, 'https://vip.opstream11.com/20230610/44635_ef5299ab/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (15, 'Tập 2', 2, 'https://vip.opstream11.com/20230610/44636_04c05ef1/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (15, 'Tập 3', 3, 'https://vip.opstream11.com/20230610/44637_978aa2b1/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (15, 'Tập 4', 4, 'https://vip.opstream11.com/20230610/44638_79aed6fd/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (15, 'Tập 5', 5, 'https://vip.opstream11.com/20230610/44639_4f6dec83/index.m3u8', 1500)")

        // Star Struck: Truy Tinh (movieId: 16) - 8 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 1', 1, 'https://vip.opstream14.com/20230519/36598_2e32dec4/index.m3u8', 1200)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 2', 2, 'https://vip.opstream14.com/20230519/36599_1a8802df/index.m3u8', 1200)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 3', 3, 'https://vip.opstream14.com/20230525/36824_63878372/index.m3u8', 1200)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 4', 4, 'https://vip.opstream14.com/20230525/36825_ec86faf1/index.m3u8', 1200)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 5', 5, 'https://vip.opstream14.com/20230601/37102_89bfba98/index.m3u8', 1200)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 6', 6, 'https://vip.opstream14.com/20230601/37103_ab831917/index.m3u8', 1200)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 7', 7, 'https://vip.opstream14.com/20230608/37381_539e8955/index.m3u8', 1200)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (16, 'Tập 8', 8, 'https://vip.opstream14.com/20230608/37382_8e4aa760/index.m3u8', 1200)")

        // Chắp Cánh (movieId: 17) - 10 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 1', 1, 'https://vip.opstream15.com/20230501/38557_e25c5bf9/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 2', 2, 'https://vip.opstream15.com/20230501/38559_a9f5f0ec/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 3', 3, 'https://vip.opstream15.com/20230501/38560_c7fc2b30/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 4', 4, 'https://vip.opstream15.com/20230501/38561_48aefb85/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 5', 5, 'https://vip.opstream15.com/20230501/38562_47755168/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 6', 6, 'https://vip.opstream15.com/20230501/38563_8950451d/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 7', 7, 'https://vip.opstream15.com/20230501/38564_17cc506d/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 8', 8, 'https://vip.opstream15.com/20230501/38565_bbfca463/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 9', 9, 'https://vip.opstream15.com/20230501/38566_f714d43a/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (17, 'Tập 10', 10, 'https://vip.opstream15.com/20230501/38567_02402906/index.m3u8', 1800)")

        // Thần Thám Trốn Học (movieId: 29) - single movie
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (29, 'Full', 1, 'https://vip.opstream16.com/20230110/28952_754c3dff/index.m3u8', 5520)")

        // Nhất Quỷ Nhì Ma, Thứ Ba Takagi - Movie (movieId: 30) - single movie
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (30, 'Full', 1, 'https://vip.opstream11.com/20230110/41393_d75713cc/index.m3u8', 4320)")


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

        db?.execSQL("""
            INSERT INTO movie_categories (movieId, categoryId) VALUES
            (1, 3),
            (2, 3),
            (3, 18),
            (4, 18),
            (5, 7),
            (6, 1),
            (6, 3),
            (7, 6),
            (7, 18),
            (8, 13),
            (8, 19),
            (9, 13),
            (9, 19),
            (10, 3),
            (10, 6),
            (11, 13),
            (12, 10),
            (12, 11),
            (12, 12),
            (13, 7),
            (13, 18),
            (14, 16)
        """.trimIndent())

        // Movie categories for new movies
        // I Am Your King 1 (15) - Tâm Lý, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (15, 5), (15, 20)")
        
        // Star Struck: Truy Tinh (16) - Tâm Lý, Học Đường  
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (16, 5), (16, 20)")
        
        // Chắp Cánh (17) - Tình Cảm, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (17, 2), (17, 20)")
        
        // Đây Không Phải Là Kéo Co Sao? (18) - Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (18, 20)")
        
        // Chàng Phản Diện Dễ Thương (19) - Tâm Lý, Viễn Tưởng, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (19, 5), (19, 10), (19, 20)")
        
        // BIRDIE WING Season 2 (20) - Thể Thao, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (20, 8), (20, 20)")
        
        // Vì Nhớ (21) - Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (21, 20)")
        
        // Alice Gear Aegis Expansion (22) - Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (22, 20)")
        
        // Sau khi có được năng lực bá đạo (23) - Hành Động, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (23, 1), (23, 20)")
        
        // The Dangers in My Heart (24) - Tình Cảm, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (24, 2), (24, 20)")
        
        // Yêu Yamada ở Lv999 (25) - Tình Cảm, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (25, 2), (25, 20)")
        
        // Khoa Học Về Tình Yêu (26) - Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (26, 20)")
        
        // Giai Điệu Trên Triền Dốc (27) - Âm Nhạc, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (27, 14), (27, 20)")
        
        // Robotics;Notes (28) - Hành Động, Viễn Tưởng, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (28, 1), (28, 10), (28, 20)")
        
        // Thần Thám Trốn Học (29) - Hành Động, Bí ẩn, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (29, 1), (29, 19), (29, 20)")
        
        // Nhất Quỷ Nhì Ma, Thứ Ba Takagi Movie (30) - Tình Cảm, Hài Hước, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (30, 2), (30, 3), (30, 20)")
        
        // Kubo Không Để Tôi Vô Hình (31) - Tình Cảm, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (31, 2), (31, 20)")
        
        // Thiên Sứ nhà bên (32) - Hài Hước, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (32, 3), (32, 20)")
        
        // Kẻ Ngoại Lai Ở Học Viện Ma Vương II (33) - Tình Cảm, Viễn Tưởng, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (33, 2), (33, 10), (33, 20)")
        
        // Đừng Chọc Anh Nữa Mà, Nagatoro! Phần 2 (34) - Tình Cảm, Hài Hước, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (34, 2), (34, 3), (34, 20)")
        
        // Lớp Học Điệp Viên (35) - Hành Động, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (35, 1), (35, 20)")
        
        // kỷ lục lạ về kỵ sĩ không đầu (36) - Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (36, 20)")
        
        // Xin Chào, Ngày Hôm Qua (37) - Tình Cảm, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (37, 2), (37, 20)")
        
        // Mật Mã Lệch Lạc (38) - Tình Cảm, Hài Hước, Chính kịch, Học Đường
        db?.execSQL("INSERT INTO movie_categories (movieId, categoryId) VALUES (38, 2), (38, 3), (38, 18), (38, 20)")


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

        // ------------------- NOTIFICATIONS -------------------
        db?.execSQL("""
        CREATE TABLE notifications (
            notificationId INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            content TEXT NOT NULL,
            createdAt TEXT DEFAULT CURRENT_TIMESTAMP,
            type TEXT NOT NULL,       -- "ht" = hệ thống, "cn" = cá nhân
            userId INTEGER            -- null nếu hệ thống
        )
        """)

        // Thêm dữ liệu mẫu
        db?.execSQL("""
        INSERT INTO notifications (title, content, type) 
        VALUES ('One Piece tập 1090', 'Đã có phụ đề tiếng Việt', 'ht')
    """)

            db?.execSQL("""
        INSERT INTO notifications (title, content, type) 
        VALUES ('Naruto Shippuden', 'Tập 220 đã có', 'ht')
    """)

            db?.execSQL("""
        INSERT INTO notifications (title, content, type) 
        VALUES ('Jujutsu Kaisen 2', 'Tập mới nhất đã được thêm', 'ht')
    """)
        db?.execSQL("""
        INSERT INTO notifications (title, content, type) 
        VALUES ('Thanh gươm diệt quỷ', 'Trailer chính thức phát hành', 'ht')
    """)
        db?.execSQL("""
        INSERT INTO notifications (title, content, type) 
        VALUES ('Bá chủ Tây Tạng', 'Tập 15 đã được lên sóng', 'ht')
    """)
        db?.execSQL("""
        INSERT INTO notifications (title, content, type) 
        VALUES ('Hệ thống bảo trì', 'Từ 2h00 - 4h00 ngày 19/09/2025', 'ht')
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
        db?.execSQL("DROP TABLE IF EXISTS notifications")

        onCreate(db)
    }
}
