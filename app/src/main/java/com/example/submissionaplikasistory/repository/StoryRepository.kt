package com.example.submissionaplikasistory.repository

import android.annotation.SuppressLint
import com.example.submissionaplikasistory.datasource.api.ApiConfiguration
import com.example.submissionaplikasistory.datasource.local.DaoStoryConfig
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.datasource.model.DetailStoryResponse
import com.example.submissionaplikasistory.datasource.model.PostResponse
import com.example.submissionaplikasistory.datasource.model.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class StoryRepository(
    private val db : DaoStoryConfig
) {

    suspend fun getAllStories(header: Map<String, String>): Response<StoryResponse> {
        return ApiConfiguration.getApiService().getStories(header)
    }

    suspend fun getDetailStory(header: Map<String, String>, id: String): Response<DetailStoryResponse> {
        return ApiConfiguration.getApiService().getDetailStory(header, id)
    }

    suspend fun postStory(header: Map<String, String>, description: RequestBody, file: MultipartBody.Part): Response<PostResponse> {
        return ApiConfiguration.getApiService().postStory(header, description, file)
    }

    suspend fun postStory(story: EntityDaoStory) {
        return db.getService().addStory(story)
    }

    fun getStoryFromDatabaseDao() = db.getService().getStory()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: StoryRepository? = null
        fun getInstance(daoStoryConfig: DaoStoryConfig) = instance ?: synchronized(this) {
            val ins = StoryRepository(daoStoryConfig)
            instance = ins
            instance
        }
    }
}