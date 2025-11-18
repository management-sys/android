package com.example.attendancemanagementapp.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.attendancemanagementapp.data.dto.AuthDTO
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenDataStore (private val context: Context) {
    private val Context.dataStore by preferencesDataStore("token_prefs")

    companion object {
        private const val TAG = "TokenDataStore"
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }

    val accessTokenFlow = context.dataStore.data.map { it[ACCESS_TOKEN] ?: "" }
    val refreshTokenFlow = context.dataStore.data.map { it[REFRESH_TOKEN] ?: "" }

    /* 토큰 저장 */
    suspend fun saveTokens(tokenInfo: AuthDTO.TokenInfo) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = tokenInfo.accessToken
            it[REFRESH_TOKEN] = tokenInfo.refreshToken
            Log.d(TAG, "[saveTokens] 토큰 저장\nACCESS_TOKEN: ${it[ACCESS_TOKEN]}\nREFRESH_TOKEN: ${it[REFRESH_TOKEN]}")
        }
    }

    /* 토큰 초기화 */
    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
        Log.d(TAG, "[clearTokens] 토큰 초기화")
    }
}