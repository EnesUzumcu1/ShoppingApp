package com.enesuzumcu.shoppingapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.datastore by preferencesDataStore(name = "user_settings")

class DataStoreManager (context: Context){
    private object PreferencesKeys{
        val ON_BOARDING_VISIBLE = booleanPreferencesKey("on_boarding_visible")
    }

    private val dataStore = context.datastore

    suspend fun setOnBoardingVisible(isVisible: Boolean){
        dataStore.edit {  mutablePreferences ->
            mutablePreferences[PreferencesKeys.ON_BOARDING_VISIBLE] = isVisible
        }
    }

    val getOnBoardingVisible: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ON_BOARDING_VISIBLE] ?: false
    }
}