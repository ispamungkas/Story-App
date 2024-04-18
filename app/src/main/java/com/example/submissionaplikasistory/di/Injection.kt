package com.example.submissionaplikasistory.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.submissionaplikasistory.datasource.local.DaoStoryConfig
import com.example.submissionaplikasistory.repository.StoryRepository
import com.example.submissionaplikasistory.repository.UserRepository
import com.example.submissionaplikasistory.utils.SettingPreference
import com.example.submissionaplikasistory.view.viewmodel.ViewModelProviderFactory

object Injection {
    fun getUserRepositoryInstance(dataStore: DataStore<Preferences>) : ViewModelProviderFactory {
        val repo = UserRepository.getInstance()
        val pref = SettingPreference.getInstance(dataStore)!!
        return ViewModelProviderFactory(repo, pref)
    }

    fun getStoryRepositoryInstance(context: Context): ViewModelProviderFactory {
        val dao = DaoStoryConfig.getInstance(context)
        val repo = StoryRepository.getInstance(dao)
        return ViewModelProviderFactory(repo, null)
    }
}