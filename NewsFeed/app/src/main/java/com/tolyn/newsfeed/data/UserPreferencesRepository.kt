package com.tolyn.newsfeed.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import com.tolyn.newsfeed.UserPreferences
import kotlinx.coroutines.flow.*
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"
private const val DATA_STORE_FILE_NAME = "user_prefs.pb"

class UserPreferencesRepository private constructor(context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null

        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferencesRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    private val sharedPrefsMigration = SharedPreferencesMigration(
        context,
        USER_PREFERENCES_NAME
    ) { sharedPrefs: SharedPreferencesView, currentData: UserPreferences ->
        currentData
    }

    // Build the DataStore
    private val userPreferencesStore: DataStore<UserPreferences> = context.createDataStore(
        fileName = DATA_STORE_FILE_NAME,
        serializer = UserPreferencesSerializer,
        migrations = listOf(sharedPrefsMigration)
    )

    private val userPreferencesFlow: Flow<UserPreferences> = userPreferencesStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(UserPreferences.getDefaultInstance())
            } else {
                throw exception
            }
        }

    suspend fun updateProviderSubscribe(newsProvider: NewsProvider, isSubscribe: Boolean) {
        userPreferencesStore.updateData { currentPreferences ->
            currentPreferences.toBuilder().putProviderSubscribe(newsProvider.name, isSubscribe).build()
        }
    }

    suspend fun getIsProviderSubscribe(newsProvider: NewsProvider,): Boolean? {
        return userPreferencesFlow.firstOrNull()?.providerSubscribeMap?.get(newsProvider.name)
    }
}