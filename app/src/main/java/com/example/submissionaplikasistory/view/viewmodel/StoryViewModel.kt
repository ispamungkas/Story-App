package com.example.submissionaplikasistory.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionaplikasistory.datasource.local.EntityDaoStory
import com.example.submissionaplikasistory.datasource.model.ErrorResponse
import com.example.submissionaplikasistory.datasource.model.ListStoryItem
import com.example.submissionaplikasistory.datasource.model.PostResponse
import com.example.submissionaplikasistory.datasource.model.Story
import com.example.submissionaplikasistory.repository.StoryRepository
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.Utils
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryViewModel(
    private val storyRepository: StoryRepository,
) : ViewModel() {

    private val _story : MutableLiveData<Resources<List<ListStoryItem>>> = MutableLiveData()
    val story: LiveData<Resources<List<ListStoryItem>>> = _story

    private val _detailStory: MutableLiveData<Resources<Story>> = MutableLiveData()
    val detailStory: LiveData<Resources<Story>> = _detailStory

    private val _postResult: MutableLiveData<Resources<PostResponse>> = MutableLiveData()
    val postResult: LiveData<Resources<PostResponse>> = _postResult

    val getAllStoryFromDatabase : MutableLiveData<List<EntityDaoStory>> = MutableLiveData()

    init {
        getDataStoryNotPaging()
    }

    fun getDetailStory(token: String, id: String) = viewModelScope.launch {
        val header = Utils.getHeader(token)
        val responseResult = storyRepository.getDetailStory(header, id)
        try {
            if (responseResult.isSuccessful && responseResult.body()?.error == false) {
                responseResult.body()?.story?.let {
                    val result = Resources.OnSuccess(it)
                    _detailStory.postValue(result)
                }
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            errorMessage?.let { _detailStory.postValue(Resources.OnFailure(errorMessage)) }
        }
    }

    fun postStory(token: String, description: RequestBody, file: MultipartBody.Part, lat: Float?, lon: Float?) = viewModelScope.launch {
        val header = Utils.getHeader(token)
        try {
            val responseResult = storyRepository.postStory(header, description, file, lat, lon)
            if (responseResult.isSuccessful && responseResult.body()?.error == false) {
                responseResult.body()?.let {
                    val result = Resources.OnSuccess(it)
                    _postResult.postValue(result)
                }
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            errorMessage?.let { _postResult.postValue(Resources.OnFailure(errorMessage)) }
        }
    }

    fun getStoryDao(token: Map<String, String>) : LiveData<PagingData<EntityDaoStory>> =
        storyRepository.getStoryFromDatabaseDao(token).cachedIn(viewModelScope)

    private fun getDataStoryNotPaging() = viewModelScope.launch {
        getAllStoryFromDatabase.postValue(storyRepository.getOnlyStory())
    }

}