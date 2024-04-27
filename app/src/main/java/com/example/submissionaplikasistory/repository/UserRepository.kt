package com.example.submissionaplikasistory.repository

import com.example.submissionaplikasistory.datasource.api.ApiConfiguration
import com.example.submissionaplikasistory.datasource.model.LoginResponse
import com.example.submissionaplikasistory.datasource.model.RegisterResponse
import com.example.submissionaplikasistory.utils.wrapEspressoIdlingResource
import retrofit2.Response

class UserRepository {

    suspend fun userRegister(
        name: String,
        email: String,
        password: String
    ) : Response<RegisterResponse> {
        return ApiConfiguration.getApiService().register(name, email, password)
    }

    suspend fun userLogin(
        email: String,
        password: String
    ): Response<LoginResponse> {
        wrapEspressoIdlingResource {
            return ApiConfiguration.getApiService().login(email, password)
        }
    }
    
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance() : UserRepository? {
            return instance ?: synchronized(this) {
                instance = UserRepository()
                instance
            }
        }
    }

}