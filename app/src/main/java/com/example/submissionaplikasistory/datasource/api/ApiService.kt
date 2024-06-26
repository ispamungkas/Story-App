package com.example.submissionaplikasistory.datasource.api

import com.example.submissionaplikasistory.datasource.model.DetailStoryResponse
import com.example.submissionaplikasistory.datasource.model.LoginResponse
import com.example.submissionaplikasistory.datasource.model.PostResponse
import com.example.submissionaplikasistory.datasource.model.RegisterResponse
import com.example.submissionaplikasistory.datasource.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @HeaderMap headerMap: Map<String,String>,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("location") location: Int = 1
    ): Response<StoryResponse>

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @HeaderMap headerMap: Map<String,String>,
        @Path("id") id: String
    ): Response<DetailStoryResponse>

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @HeaderMap header: Map<String, String>,
        @Part("description") description: RequestBody,
        @Part file : MultipartBody.Part,
        @Part("lat") lat: Float?,
        @Part("lon") lon: Float?
    ): Response<PostResponse>
}