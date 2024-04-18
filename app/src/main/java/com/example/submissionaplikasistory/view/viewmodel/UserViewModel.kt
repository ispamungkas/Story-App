package com.example.submissionaplikasistory.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissionaplikasistory.datasource.model.ErrorResponse
import com.example.submissionaplikasistory.datasource.model.LoginResponse
import com.example.submissionaplikasistory.datasource.model.LoginResult
import com.example.submissionaplikasistory.datasource.model.RegisterResponse
import com.example.submissionaplikasistory.repository.UserRepository
import com.example.submissionaplikasistory.utils.Resources
import com.example.submissionaplikasistory.utils.SettingPreference
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response

class UserViewModel(
    private val userRepository: UserRepository,
    private val preference: SettingPreference
) : ViewModel() {

    private val registerResult: MutableLiveData<Resources<RegisterResponse>> = MutableLiveData()
    val getRegisterResponseResult: LiveData<Resources<RegisterResponse>> = registerResult

    private val loginResult: MutableLiveData<Resources<LoginResponse>> = MutableLiveData()
    val getLoginResponseResult: LiveData<Resources<LoginResponse>> = loginResult

    fun requestRegisterAccountStory(
        name: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        registerResult.postValue(Resources.Loading())
        try {
            val response = userRepository.userRegister(name, email, password)
            registerResult.postValue(handlerRegisterAccountStory(response))
        } catch (e: HttpException) {
            registerResult.postValue(Resources.OnFailure(e.message().toString()))
        }
    }

    fun requestLoginAccountStory(
        email: String,
        password: String
    ) = viewModelScope.launch {
        loginResult.postValue(Resources.Loading())
        try {
            val response = userRepository.userLogin(email, password)
            loginResult.postValue(handlerLoginAccountStory(response))
        } catch (e: HttpException) {
            loginResult.postValue(Resources.OnFailure(e.message().toString()))
        }
    }

    fun getUserSession() : LiveData<LoginResult> {
        return preference.getUserActive().asLiveData()
    }

    fun saveUserSession(loginResult: LoginResult) = viewModelScope.launch {
        preference.saveUserSession(loginResult)
    }

    fun deleteUserSession() = viewModelScope.launch {
        preference.deleteUserSession()
    }

    private fun handlerRegisterAccountStory(response: Response<RegisterResponse>) : Resources<RegisterResponse> {
        try {
             if (response.isSuccessful && response.body()?.error == false) {
                 val body = response.body()
                 body?.let {
                     return Resources.OnSuccess(body)
                 }
             }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            errorMessage?.let { return Resources.OnFailure(errorMessage) }
        }

        val jsonInString = response.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
        return Resources.OnFailure(errorBody.message!!)
    }

    private fun handlerLoginAccountStory(response: Response<LoginResponse>) : Resources<LoginResponse> {
        try {
            if (response.isSuccessful && response.body()?.error == false) {
                val body = response.body()
                body?.let {
                    return Resources.OnSuccess(body)
                }
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            errorMessage?.let { return Resources.OnFailure(errorMessage) }
        }

        val jsonInString = response.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
        return Resources.OnFailure(errorBody.message!!)
    }

}