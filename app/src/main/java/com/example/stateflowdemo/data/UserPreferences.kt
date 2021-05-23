package com.example.stateflowdemo.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesUserInfo"
private val Context.dataStore by preferencesDataStore("user_prefs")
enum class UserStatus {
    APPROVED,
    NOT_APPROVED,
    NEW
}
data class UserPreferences(
    val userStatus: UserStatus,
    val authToken: String?,
    val email: String?
)

@Singleton
public class UserPreferencesRepository @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKey {
        val USER_STATUS = stringPreferencesKey("user_status")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_EMAIL = stringPreferencesKey("email")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            Log.e(TAG, "Error reading preferences.", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        val userStatus = UserStatus.valueOf(preferences[PreferencesKey.USER_STATUS] ?: UserStatus.NEW.name)
        val authToken = preferences[PreferencesKey.AUTH_TOKEN]
        val email = preferences[PreferencesKey.USER_EMAIL]
        UserPreferences(userStatus, authToken, email)
    }
    suspend fun updateUserStatus(userStatus: UserStatus) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_STATUS] = userStatus.name
        }
    }
    suspend fun updateAuthToken(authToken: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.AUTH_TOKEN] = authToken
        }
    }
    suspend fun updateEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_EMAIL] = email
        }
    }
}