package com.example.submissionaplikasistory.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.submissionaplikasistory.datasource.StoryRemoteMediator
import com.example.submissionaplikasistory.datasource.api.ApiConfiguration
import com.example.submissionaplikasistory.datasource.api.ApiService
import com.example.submissionaplikasistory.datasource.local.DaoStoryConfig
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.datasource.model.DetailStoryResponse
import com.example.submissionaplikasistory.datasource.model.PostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class StoryRepository(
    private val db : DaoStoryConfig,
    private val apiService: ApiService
) {

    suspend fun getDetailStory(header: Map<String, String>, id: String): Response<DetailStoryResponse> {
        return ApiConfiguration.getApiService().getDetailStory(header, id)
    }

    suspend fun postStory(header: Map<String, String>, description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?): Response<PostResponse> {
        return ApiConfiguration.getApiService().postStory(header, description, file, lat, lon)
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStoryFromDatabaseDao(token: Map<String, String>): LiveData<PagingData<EntityDaoStory>>{
        return Pager(
            config =  PagingConfig(
                pageSize = 5,
                maxSize = 15
            ),
            remoteMediator = StoryRemoteMediator(db, apiService, token),
            pagingSourceFactory = {
                db.getService().getStory()
            }
        ).liveData
    }

    suspend fun getOnlyStory(): List<EntityDaoStory> {
        return db.getService().getStoryListEntityDaoStory()
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: StoryRepository? = null
        fun getInstance(daoStoryConfig: DaoStoryConfig, apiService: ApiService) = instance ?: synchronized(this) {
            val ins = StoryRepository(daoStoryConfig, apiService)
            instance = ins
            instance
        }
    }
}