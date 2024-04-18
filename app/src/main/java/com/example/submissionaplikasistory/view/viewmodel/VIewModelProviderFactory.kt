package com.example.submissionaplikasistory.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionaplikasistory.repository.StoryRepository
import com.example.submissionaplikasistory.repository.UserRepository
import com.example.submissionaplikasistory.utils.SettingPreference

class ViewModelProviderFactory(private val repo: Any?, private val preference: SettingPreference?): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(repo as UserRepository, preference!!) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(repo as StoryRepository) as T
        }
        throw IllegalStateException("Can't instance view model because repository not found")
    }

}