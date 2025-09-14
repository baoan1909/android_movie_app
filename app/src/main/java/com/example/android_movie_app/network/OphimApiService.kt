package com.example.android_movie_app.network

import retrofit2.http.GET
import com.example.android_movie_app.model.MovieResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface OphimApiService {

    // Lấy danh sách phim mới
    @GET("danh-sach/phim-moi")
    suspend fun getNewMovies(): MovieResponse

    // Nếu muốn thêm các endpoint khác, ví dụ: phim hot, phim sắp chiếu
    @GET("danh-sach/phim-hot")
    suspend fun getHotMovies(): MovieResponse
}

object RetrofitClient {

    private const val BASE_URL = "https://ophim17.cc/"

    val instance: OphimApiService by lazy {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OphimApiService::class.java)
    }
}

