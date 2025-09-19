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
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (19, 'chang-phan-dien-de-thuong', 'Chàng Phản Diện Dễ Thương', 'Cute Bad Guy', 'Câu chuyện về một chàng trai có vẻ ngoài phản diện nhưng thực chất rất dễ thương trong môi trường học đường.', 'series', 'chang-phan-dien-de-thuong-thumb.jpg', 'chang-phan-dien-de-thuong-poster.jpg', 2023, 0, 6.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (20, 'birdie-wing-golf-girls-story-season-2', 'BIRDIE WING -Golf Girls'' Story- Season 2', 'BIRDIE WING -Golf Girls'' Story- Season 2', 'Phần 2 của câu chuyện về những cô gái chơi golf với những ước mơ và tình bạn đẹp.', 'series', 'birdie-wing-golf-girls-story-season-2-thumb.jpg', 'birdie-wing-golf-girls-story-season-2-poster.jpg', 2023, 0, 6.6)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (21, 'vi-nho-reminders', 'Vì Nhớ (ReminderS)', 'ReminderS', 'Một câu chuyện ngắn về những kỷ niệm đẹp trong thời học sinh.', 'series', 'vi-nho-reminders-thumb.jpg', 'vi-nho-reminders-poster.jpg', 2023, 0, 7.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (22, 'alice-gear-aegis-expansion', 'Alice Gear Aegis Expansion', 'アリス・ギア・アイギス Expansion', 'Anime về những cô gái chiến đấu với thiết bị Alice Gear trong môi trường học đường.', 'series', 'alice-gear-aegis-expansion-thumb.jpg', 'alice-gear-aegis-expansion-poster.jpg', 2023, 0, 5.0)")
        db?.execSQL("INSERT INTO movies (id, slug, name, originName, content, type, thumbUrl, posterUrl, year, viewCount, rating) VALUES (23, 'sau-khi-co-duoc-nang-luc-ba-dao-o-di-gioi-toi-cung-vo-doi-o-the-gioi-thuc-thang-cap-xong-thi-cuoc-doi-cung-thay-doi', 'Sau khi có được năng lực bá đạo ở dị giới, tôi cũng vô đối ở thế giới thực ~Thăng cấp xong thì cuộc đời cũng thay đổi~', '異世界でチート能力を手にした俺は、現実世界をも無双する ～レベルアップは人生を変えた～', 'Câu chuyện về một học sinh có được sức mạnh từ dị giới và áp dụng vào cuộc sống thực.', 'series', 'sau-khi-co-duoc-nang-luc-ba-dao-o-di-gioi-toi-cung-vo-doi-o-the-gioi-thuc-thang-cap-xong-thi-cuoc-doi-cung-thay-doi-thumb.jpg', 'sau-khi-co-duoc-nang-luc-ba-dao-o-di-gioi-toi-cung-vo-doi-o-the-gioi-thuc-thang-cap-xong-thi-cuoc-doi-cung-thay-doi-poster.jpg', 2023, 0, 8.245)")
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

        // Chàng Phản Diện Dễ Thương (movieId: 19) - 23 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 1', 1, 'https://vip.opstream14.com/20230412/35256_d9142062/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 2', 2, 'https://vip.opstream14.com/20230412/35257_eb11ddc0/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 3', 3, 'https://vip.opstream14.com/20230412/35258_19cb0177/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 4', 4, 'https://vip.opstream14.com/20230412/35259_1cafddd2/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 5', 5, 'https://vip.opstream14.com/20230412/35260_6623ba92/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 6', 6, 'https://vip.opstream14.com/20230412/35261_a7595ac9/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 7', 7, 'https://vip.opstream14.com/20230412/35262_b0cf255a/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 8', 8, 'https://vip.opstream14.com/20230412/35263_f70899c2/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 9', 9, 'https://vip.opstream14.com/20230412/35264_31df9958/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 10', 10, 'https://vip.opstream14.com/20230412/35265_260a0c87/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 11', 11, 'https://vip.opstream14.com/20230412/35266_6f8d5165/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 12', 12, 'https://vip.opstream14.com/20230412/35267_70c22295/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 13', 13, 'https://vip.opstream14.com/20230412/35268_a8e18b8a/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 14', 14, 'https://vip.opstream14.com/20230412/35269_e2fd5250/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 15', 15, 'https://vip.opstream14.com/20230412/35270_205c88cd/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 16', 16, 'https://vip.opstream14.com/20230412/35271_62b19558/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 17', 17, 'https://vip.opstream14.com/20230412/35272_f648d191/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 18', 18, 'https://vip.opstream14.com/20230412/35273_d2be26d1/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 19', 19, 'https://vip.opstream14.com/20230412/35274_b54fd211/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 20', 20, 'https://vip.opstream14.com/20230412/35275_b77b7989/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 21', 21, 'https://vip.opstream14.com/20230412/35276_4713b297/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 22', 22, 'https://vip.opstream14.com/20230412/35277_5b6e03ee/index.m3u8', 1500)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (19, 'Tập 23', 23, 'https://vip.opstream14.com/20230412/35278_5743d945/index.m3u8', 1500)")

        // BIRDIE WING Season 2 (movieId: 20) - 12 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 1', 1, 'https://vip.opstream11.com/20230408/43194_c01e8209/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 2', 2, 'https://vip.opstream11.com/20230415/43224_13f9cc96/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 3', 3, 'https://vip.opstream11.com/20230422/43258_e7222e2f/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 4', 4, 'https://vip.opstream16.com/20230429/34501_c2899a76/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 5', 5, 'https://vip.opstream16.com/20230506/34541_dda10423/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 6', 6, 'https://vip.opstream16.com/20230513/34571_392dd427/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 7', 7, 'https://vip.opstream16.com/20230520/34612_470ec5e5/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 8', 8, 'https://vip.opstream16.com/20230527/34649_f2ecb9fd/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 9', 9, 'https://vip.opstream16.com/20230603/34687_eccfad81/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 10', 10, 'https://vip.opstream16.com/20230610/34710_57d6f475/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 11', 11, 'https://vip.opstream16.com/20230617/34753_bb7c6966/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (20, 'Tập 12', 12, 'https://vip.opstream15.com/20230826/41721_2b81a899/index.m3u8', 1440)")

        // Vì Nhớ (ReminderS) (movieId: 21) - 3 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (21, 'Tập 1', 1, 'https://vip.opstream14.com/20230405/34651_424cb387/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (21, 'Tập 2', 2, 'https://vip.opstream14.com/20230405/34652_5963aaac/index.m3u8', 1800)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (21, 'Tập 3', 3, 'https://vip.opstream14.com/20230405/34653_1a9d514b/index.m3u8', 1800)")

        // Alice Gear Aegis Expansion (movieId: 22) - 15 episodes (12 + 3 OVA)
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 1', 1, 'https://vip.opstream11.com/20230404/43180_05d17640/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 2', 2, 'https://vip.opstream11.com/20230411/43203_85fc677e/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 3', 3, 'https://vip.opstream11.com/20230417/43237_c42f2c7e/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 4', 4, 'https://vip.opstream16.com/20230425/34268_180afb08/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 5', 5, 'https://vip.opstream16.com/20230509/34562_248a16db/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 6', 6, 'https://vip.opstream16.com/20230509/34561_db8f9fb7/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 7', 7, 'https://vip.opstream16.com/20230516/34584_7de749a6/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 8', 8, 'https://vip.opstream16.com/20230523/34622_33bed27e/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 9', 9, 'https://vip.opstream16.com/20230530/34659_1e725683/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 10', 10, 'https://vip.opstream16.com/20230606/34696_aa59bc73/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 11', 11, 'https://vip.opstream16.com/20230613/34721_56b697dc/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'Tập 12', 12, 'https://vip.opstream16.com/20230620/34763_60807042/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'OVA 1', 13, 'https://vip.opstream16.com/20230704/34791_fa0abd6a/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'OVA 2', 14, 'https://vip.opstream16.com/20230704/34790_15f71e76/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (22, 'OVA 3', 15, 'https://vip.opstream16.com/20230704/34789_92750470/index.m3u8', 1440)")

        // Sau khi có được năng lực bá đạo (movieId: 23) - 13 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 1', 1, 'https://vip.opstream11.com/20230404/43182_3ab83604/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 2', 2, 'https://vip.opstream16.com/20230425/34274_0c5fdf9c/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 3', 3, 'https://vip.opstream16.com/20230425/34275_3b95ff82/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 4', 4, 'https://vip.opstream16.com/20230502/34528_343b0c65/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 5', 5, 'https://vip.opstream16.com/20230502/34529_c44a8526/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 6', 6, 'https://vip.opstream16.com/20230509/34564_7541d7e5/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 7', 7, 'https://vip.opstream16.com/20230516/34586_4144bd1b/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 8', 8, 'https://vip.opstream16.com/20230523/34624_b15c825d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 9', 9, 'https://vip.opstream16.com/20230530/34661_7bfa09f6/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 10', 10, 'https://vip.opstream16.com/20230606/34698_f71adc76/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 11', 11, 'https://vip.opstream16.com/20230613/34723_a7772aa2/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 12', 12, 'https://vip.opstream16.com/20230620/34765_df65c888/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (23, 'Tập 13', 13, 'https://vip.opstream16.com/20230629/34783_3612d110/index.m3u8', 1440)")

        // Khoa Học Về Tình Yêu (movieId: 26) - 24 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 1', 1, 'https://vip.opstream16.com/20230315/33041_49b73d17/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 2', 2, 'https://vip.opstream16.com/20230315/33042_d36c9f57/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 3', 3, 'https://vip.opstream16.com/20230315/33043_ff11d410/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 4', 4, 'https://vip.opstream16.com/20230315/33044_2d279b34/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 5', 5, 'https://vip.opstream16.com/20230315/33045_8c30d334/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 6', 6, 'https://vip.opstream16.com/20230315/33046_0898fc39/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 7', 7, 'https://vip.opstream16.com/20230316/33097_cf140488/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 8', 8, 'https://vip.opstream16.com/20230316/33098_250e459e/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 9', 9, 'https://vip.opstream16.com/20230317/33157_f49b902c/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 10', 10, 'https://vip.opstream16.com/20230317/33158_dd7f1368/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 11', 11, 'https://vip.opstream16.com/20230318/33197_453b486c/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 12', 12, 'https://vip.opstream16.com/20230318/33198_4c74959f/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 13', 13, 'https://vip.opstream16.com/20230329/33559_2d0d384b/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 14', 14, 'https://vip.opstream16.com/20230329/33560_df722835/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 15', 15, 'https://vip.opstream14.com/20230331/34306_383f7dc8/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 16', 16, 'https://vip.opstream14.com/20230331/34307_1f0c8f14/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 17', 17, 'https://vip.opstream16.com/20230331/33599_0f84f2d7/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 18', 18, 'https://vip.opstream16.com/20230331/33600_9f63b945/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 19', 19, 'https://vip.opstream14.com/20230402/34469_0461bf50/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 20', 20, 'https://vip.opstream14.com/20230402/34470_3750f738/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 21', 21, 'https://vip.opstream14.com/20230403/34580_23af6821/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 22', 22, 'https://vip.opstream14.com/20230403/34581_d29b6099/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 23', 23, 'https://vip.opstream14.com/20230403/34590_371005e7/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (26, 'Tập 24', 24, 'https://vip.opstream14.com/20230403/34591_e79d8b4d/index.m3u8', 2700)")

        // Giai Điệu Trên Triền Dốc (movieId: 27) - 12 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 1', 1, 'https://vip.opstream11.com/20230113/41515_39752fa0/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 2', 2, 'https://vip.opstream11.com/20230113/41516_172ab76d/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 3', 3, 'https://vip.opstream11.com/20230113/41517_6256fa1e/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 4', 4, 'https://vip.opstream11.com/20230113/41518_61adf2dd/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 5', 5, 'https://vip.opstream11.com/20230113/41519_6e2314c4/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 6', 6, 'https://vip.opstream11.com/20230113/41520_6be12eca/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 7', 7, 'https://vip.opstream11.com/20230113/41521_3977cf0e/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 8', 8, 'https://vip.opstream11.com/20230113/41522_c07ab28d/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 9', 9, 'https://vip.opstream11.com/20230113/41523_ab8b51ae/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 10', 10, 'https://vip.opstream11.com/20230113/41524_66038378/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 11', 11, 'https://vip.opstream11.com/20230113/41525_9cf75961/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (27, 'Tập 12', 12, 'https://vip.opstream11.com/20230113/41526_5bf5ba18/index.m3u8', 1380)")

        // Robotics;Notes (movieId: 28) - 22 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 1', 1, 'https://vip.opstream11.com/20230111/41419_8b381439/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 2', 2, 'https://vip.opstream11.com/20230111/41420_02af6f21/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 3', 3, 'https://vip.opstream11.com/20230111/41421_a40b732d/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 4', 4, 'https://vip.opstream11.com/20230111/41422_267afc4b/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 5', 5, 'https://vip.opstream11.com/20230111/41423_562ed925/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 6', 6, 'https://vip.opstream11.com/20230111/41424_de4f09c4/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 7', 7, 'https://vip.opstream11.com/20230111/41425_11f8d4c5/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 8', 8, 'https://vip.opstream11.com/20230111/41426_cb0f80f1/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 9', 9, 'https://vip.opstream11.com/20230111/41427_977f7c63/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 10', 10, 'https://vip.opstream11.com/20230111/41428_750862a6/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 11', 11, 'https://vip.opstream11.com/20230111/41429_6986f8b7/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 12', 12, 'https://vip.opstream11.com/20230111/41430_ba4bd82c/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 13', 13, 'https://vip.opstream11.com/20230111/41431_3ffd24f5/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 14', 14, 'https://vip.opstream11.com/20230111/41432_dad8c0fe/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 15', 15, 'https://vip.opstream11.com/20230111/41433_4b461fa8/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 16', 16, 'https://vip.opstream11.com/20230111/41434_fac65f81/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 17', 17, 'https://vip.opstream11.com/20230111/41435_d354a9ba/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 18', 18, 'https://vip.opstream11.com/20230111/41436_59e7b453/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 19', 19, 'https://vip.opstream11.com/20230111/41437_e48e080f/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 20', 20, 'https://vip.opstream11.com/20230111/41438_650fff4a/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 21', 21, 'https://vip.opstream11.com/20230111/41439_b712df88/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (28, 'Tập 22', 22, 'https://vip.opstream11.com/20230111/41440_0d37db90/index.m3u8', 1380)")

        // Kubo Không Để Tôi Vô Hình (movieId: 31) - 12 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 1', 1, 'https://vip.opstream11.com/20230110/41390_53ef1d00/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 2', 2, 'https://vip.opstream11.com/20230118/41662_ab9404d4/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 3', 3, 'https://vip.opstream11.com/20230125/41967_05c2c36d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 4', 4, 'https://vip.opstream11.com/20230201/42128_4e0f4fb6/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 5', 5, 'https://vip.opstream11.com/20230208/42477_be7e2e49/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 6', 6, 'https://vip.opstream11.com/20230215/42815_155fd00d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 7', 7, 'https://vip.opstream16.com/20230517/34601_bca016e5/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 8', 8, 'https://vip.opstream16.com/20230524/34626_ffe17d0f/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 9', 9, 'https://vip.opstream16.com/20230531/34663_5c005651/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 10', 10, 'https://vip.opstream16.com/20230607/34700_aea9c651/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 11', 11, 'https://vip.opstream16.com/20230615/34743_99cf0440/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (31, 'Tập 12', 12, 'https://vip.opstream16.com/20230621/34767_b4146aec/index.m3u8', 1440)")

        // Thiên Sứ nhà bên (movieId: 32) - 12 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 1', 1, 'https://vip.opstream11.com/20230108/41204_d3262a14/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 2', 2, 'https://vip.opstream11.com/20230114/41570_24066fdc/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 3', 3, 'https://vip.opstream11.com/20230122/41877_a6941b88/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 4', 4, 'https://vip.opstream11.com/20230129/42033_f3f32623/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 5', 5, 'https://vip.opstream11.com/20230205/42392_8926be7a/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 6', 6, 'https://vip.opstream11.com/20230211/42782_34c7c9c7/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 7', 7, 'https://vip.opstream11.com/20230219/42870_ecb2497e/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 8', 8, 'https://vip.opstream11.com/20230226/42913_6e2d6d77/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 9', 9, 'https://vip.opstream11.com/20230312/43052_2e08cc22/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 10', 10, 'https://vip.opstream11.com/20230312/43050_56bb0f7b/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 11', 11, 'https://vip.opstream11.com/20230319/43098_ac2a732a/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (32, 'Tập 12', 12, 'https://vip.opstream11.com/20230326/43137_4edc47ed/index.m3u8', 1440)")

        // Kẻ Ngoại Lai Ở Học Viện Ma Vương II (movieId: 33) - 24 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 1', 1, 'https://vip.opstream11.com/20230108/41206_ef46952e/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 2', 2, 'https://vip.opstream11.com/20230115/41572_eb4b8340/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 3', 3, 'https://vip.opstream11.com/20230122/41876_a974136d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 4', 4, 'https://vip.opstream15.com/20230129/32195_8f21011d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 5', 5, 'https://vip.opstream11.com/20230205/42446_dde4e148/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 6', 6, 'https://vip.opstream11.com/20230212/42795_521ef503/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 7', 7, 'https://vip.opstream11.com/20230820/46678_037829fd/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 8', 8, 'https://vip.opstream16.com/20230827/35000_ae40c4a1/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 9', 9, 'https://vip.opstream11.com/20230903/46884_edd55859/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 10', 10, 'https://vip.opstream16.com/20230911/35083_fe1e0029/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 11', 11, 'https://vip.opstream16.com/20230917/35118_38b43b46/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 12', 12, 'https://vip.opstream16.com/20230924/35144_ae714b5f/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 13', 13, 'https://vip.opstream17.com/20240412/4943_1d28bca2/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 14', 14, 'https://vip.opstream11.com/20240420/51697_c38f03c8/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 15', 15, 'https://vip.opstream17.com/20240426/6059_6c47a1e7/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 16', 16, 'https://vip.opstream17.com/20240504/6652_83194603/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 17', 17, 'https://vip.opstream11.com/20240511/51828_63063925/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 18', 18, 'https://vip.opstream17.com/20240518/8003_0b3e917d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 19', 19, 'https://vip.opstream11.com/20240601/51897_4451001e/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 20', 20, 'https://vip.opstream17.com/20240608/10853_0770a947/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 21', 21, 'https://vip.opstream11.com/20240615/51913_ac9e8af5/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 22', 22, 'https://vip.opstream11.com/20240629/51935_a2b891cd/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 23', 23, 'https://vip.opstream11.com/20240705/51984_36734fcf/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (33, 'Tập 24', 24, 'https://vip.opstream11.com/20240725/52022_148f5053/index.m3u8', 1440)")

        // Đừng Chọc Anh Nữa Mà, Nagatoro! Phần 2 (movieId: 34) - 12 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 1', 1, 'https://vip.opstream11.com/20230108/41197_3d13cc1d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 2', 2, 'https://vip.opstream11.com/20230115/41577_1680662b/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 3', 3, 'https://vip.opstream11.com/20230115/41578_f51f79d4/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 4', 4, 'https://vip.opstream11.com/20230122/41882_fc2ed298/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 5', 5, 'https://vip.opstream11.com/20230129/42040_aaec7c50/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 6', 6, 'https://vip.opstream11.com/20230205/42451_c2319cd8/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 7', 7, 'https://vip.opstream11.com/20230212/42800_8d641fa6/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 8', 8, 'https://vip.opstream11.com/20230219/42878_d8239ed8/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 9', 9, 'https://vip.opstream11.com/20230226/42921_7d617888/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 10', 10, 'https://vip.opstream11.com/20230306/42984_de70a7bf/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 11', 11, 'https://vip.opstream11.com/20230312/43058_1e78c722/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (34, 'Tập 12', 12, 'https://vip.opstream11.com/20230319/43103_2769c416/index.m3u8', 1440)")

        // Lớp Học Điệp Viên (movieId: 35) - 12 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 1', 1, 'https://vip.opstream11.com/20230106/41025_80b61a11/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 2', 2, 'https://vip.opstream11.com/20230112/41488_dae9cde0/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 3', 3, 'https://vip.opstream11.com/20230120/41776_1179f3c2/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 4', 4, 'https://vip.opstream11.com/20230127/41992_b26d94b7/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 5', 5, 'https://vip.opstream11.com/20230203/42194_ac91e746/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 6', 6, 'https://vip.opstream11.com/20230210/42654_fd4cab1a/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 7', 7, 'https://vip.opstream11.com/20230217/42857_3084471d/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 8', 8, 'https://vip.opstream11.com/20230302/42941_98d8ea25/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 9', 9, 'https://vip.opstream11.com/20230310/43013_3df3c22f/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 10', 10, 'https://vip.opstream11.com/20230317/43085_905647dc/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 11', 11, 'https://vip.opstream11.com/20230323/43125_72499e70/index.m3u8', 1440)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (35, 'Tập 12', 12, 'https://vip.opstream11.com/20230331/43170_b86ce25e/index.m3u8', 1440)")

        // kỷ lục lạ về kỵ sĩ không đầu (movieId: 36) - 25 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 1', 1, 'https://vip.opstream11.com/20221227/40140_d41b72c9/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 2', 2, 'https://vip.opstream11.com/20221227/40141_2019be9b/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 3', 3, 'https://vip.opstream11.com/20221227/40142_ef7ec2f2/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 4', 4, 'https://vip.opstream11.com/20221227/40143_4c66dd4f/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 5', 5, 'https://vip.opstream11.com/20221227/40144_0fd05866/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 6', 6, 'https://vip.opstream11.com/20221227/40145_1059c6f3/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 7', 7, 'https://vip.opstream11.com/20221227/40146_2398529d/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 8', 8, 'https://vip.opstream11.com/20221227/40147_35f70541/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 9', 9, 'https://vip.opstream11.com/20221227/40148_c3017530/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 10', 10, 'https://vip.opstream11.com/20221227/40149_a9b884f5/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 11', 11, 'https://vip.opstream11.com/20221227/40150_a977c413/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 12', 12, 'https://vip.opstream11.com/20221227/40151_d665d974/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 13', 13, 'https://vip.opstream11.com/20221227/40152_cee5321f/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 14', 14, 'https://vip.opstream11.com/20221227/40153_f19fb6a4/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 15', 15, 'https://vip.opstream11.com/20221227/40154_f2157f9a/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 16', 16, 'https://vip.opstream11.com/20221227/40155_96ffef32/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 17', 17, 'https://vip.opstream11.com/20221227/40156_6cb410bd/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 18', 18, 'https://vip.opstream11.com/20221227/40157_b1a25b88/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 19', 19, 'https://vip.opstream11.com/20221227/40158_5d353481/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 20', 20, 'https://vip.opstream11.com/20221227/40159_f17ef75f/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 21', 21, 'https://vip.opstream11.com/20221227/40160_548386a3/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 22', 22, 'https://vip.opstream11.com/20221227/40161_ad217400/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 23', 23, 'https://vip.opstream11.com/20221227/40162_fdd3ca5b/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 24', 24, 'https://vip.opstream11.com/20221227/40163_fcdaa53f/index.m3u8', 1380)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (36, 'Tập 25', 25, 'https://vip.opstream11.com/20221227/40164_bc27b44c/index.m3u8', 1380)")

        // Xin Chào, Ngày Hôm Qua (movieId: 37) - 24 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 1', 1, 'https://vip.opstream11.com/20221226/40106_76ab0722/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 2', 2, 'https://vip.opstream11.com/20221226/40107_c24db55e/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 3', 3, 'https://vip.opstream11.com/20221226/40108_def51d65/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 4', 4, 'https://vip.opstream11.com/20221226/40109_452061bd/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 5', 5, 'https://vip.opstream15.com/20221227/30360_5044377e/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 6', 6, 'https://vip.opstream15.com/20221227/30361_dbce9bbd/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 7', 7, 'https://vip.opstream14.com/20221229/28862_f55d076b/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 8', 8, 'https://vip.opstream14.com/20221229/28863_659a01ed/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 9', 9, 'https://vip.opstream14.com/20221230/28873_67536fc4/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 10', 10, 'https://vip.opstream14.com/20221230/28874_9a4e00f2/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 11', 11, 'https://vip.opstream11.com/20221230/40354_37003a7a/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 12', 12, 'https://vip.opstream14.com/20230104/29081_8938af25/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 13', 13, 'https://vip.opstream14.com/20230104/29083_4efc06f7/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 14', 14, 'https://vip.opstream14.com/20230105/29093_97a87539/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 15', 15, 'https://vip.opstream14.com/20230105/29094_8356f790/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 16', 16, 'https://vip.opstream16.com/20230110/28902_6941ae6f/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 17', 17, 'https://vip.opstream16.com/20230111/29042_8c652fd3/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 18', 18, 'https://vip.opstream16.com/20230111/29043_23198990/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 19', 19, 'https://vip.opstream16.com/20230113/29165_12cefd9d/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 20', 20, 'https://vip.opstream16.com/20230113/29166_1242e1b0/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 21', 21, 'https://vip.opstream16.com/20230113/29169_520e7835/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 22', 22, 'https://vip.opstream15.com/20230119/31901_ffb81c6c/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 23', 23, 'https://vip.opstream15.com/20230119/31899_52810949/index.m3u8', 2700)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (37, 'Tập 24', 24, 'https://vip.opstream16.com/20230119/29515_eb078de3/index.m3u8', 2700)")

        // Mật Mã Lệch Lạc (movieId: 38) - 12 episodes
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 1', 1, 'https://vip.opstream16.com/20230119/29502_8f4bc73e/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 2', 2, 'https://vip.opstream16.com/20230119/29504_25dc1049/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 3', 3, 'https://vip.opstream16.com/20230119/29506_f4f31787/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 4', 4, 'https://vip.opstream16.com/20230119/29501_c85b47d3/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 5', 5, 'https://vip.opstream16.com/20230119/29503_85dd2ff9/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 6', 6, 'https://vip.opstream16.com/20230119/29505_0431f539/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 7', 7, 'https://vip.opstream16.com/20230123/29766_a7efe6cb/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 8', 8, 'https://vip.opstream16.com/20230130/30345_33cd4085/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 9', 9, 'https://vip.opstream16.com/20230206/30821_43bc4153/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 10', 10, 'https://vip.opstream14.com/20230215/31533_1b39fba7/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 11', 11, 'https://vip.opstream16.com/20230226/32250_a963cffc/index.m3u8', 3600)")
        db?.execSQL("INSERT INTO episodes (movieId, name, episodeNumber, videoUrl, duration) VALUES (38, 'Tập 12', 12, 'https://vip.opstream16.com/20230301/32375_9532a6e4/index.m3u8', 3600)")


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
