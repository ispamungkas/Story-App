package com.example.submissionaplikasistory.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissionaplikasistory.datasource.model.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_active")

class SettingPreference private constructor(private val dataStore: DataStore<Preferences>) {

    object PreferenceKey {
        val userId = stringPreferencesKey("user_id")
        val name = stringPreferencesKey("name")
        val token = stringPreferencesKey("token")
    }

    fun getUserActive(): Flow<LoginResult> {
        return dataStore.data.map { preference ->
            val userId = preference[PreferenceKey.userId]
            val name = preference[PreferenceKey.name]
            val token = preference[PreferenceKey.token]
            LoginResult(userId, name, token)
        }
    }

    suspend fun saveUserSession(loginResult: LoginResult) {
        dataStore.edit { preferences ->
            preferences[PreferenceKey.userId] = loginResult.userId!!
            preferences[PreferenceKey.name] = loginResult.name!!
            preferences[PreferenceKey.token] = loginResult.token!!
        }
    }

    suspend fun deleteUserSession() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var instance: SettingPreference? = null
        fun getInstance(dataStore: DataStore<Preferences>) = instance ?: synchronized(this) {
            val ins = SettingPreference(dataStore)
            instance = ins
            instance
        }
    }

}