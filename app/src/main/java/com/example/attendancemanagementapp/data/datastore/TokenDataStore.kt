package com.example.attendancemanagementapp.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.attendancemanagementapp.data.dto.AuthDTO
import com.example.attendancemanagementapp.ui.base.UiEffect
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenDataStore (private val context: Context) {
    private val Context.dataStore by preferencesDataStore("token_prefs")

    companion object {
        private const val TAG = "TokenDataStore"
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USERID = stringPreferencesKey("userId")
        val LOGOUT_FLAG = booleanPreferencesKey("logout_flag")
    }

    val accessTokenFlow = context.dataStore.data.map { it[ACCESS_TOKEN] ?: "" }
    val refreshTokenFlow = context.dataStore.data.map { it[REFRESH_TOKEN] ?: "" }
    val userIdFlow = context.dataStore.data.map { it[USERID] ?: "" }
    val logoutFlagFlow = context.dataStore.data.map { it[LOGOUT_FLAG] ?: false }

    private val _uiEffect = MutableSharedFlow<UiEffect>(extraBufferCapacity = 1)
    val uiEffect = _uiEffect.asSharedFlow()

    /* 토큰 저장 */
    suspend fun saveTokens(tokenInfo: AuthDTO.TokenInfo) {
        context.dataStore.edit {
            it[ACCESS_TOKEN] = tokenInfo.accessToken
            it[REFRESH_TOKEN] = tokenInfo.refreshToken
        }
        Log.d(TAG, "[saveTokens] 토큰 저장\nACCESS_TOKEN: ${tokenInfo.accessToken}\nREFRESH_TOKEN: ${tokenInfo.refreshToken}")
    }

    /* 사용자 아이디 저장 */
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit {
            it[USERID] = userId
        }
        Log.d(TAG, "[saveUserId] 사용자 아이디 저장\nUSERID: ${userId}")
    }

    /* 토큰 초기화 */
    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
        Log.d(TAG, "[clearTokens] 토큰 초기화")
    }

    /* 리프레시 실패 시 로그인 화면으로 이동 */
    suspend fun setLogoutFlag(value: Boolean) {
        context.dataStore.edit { it[LOGOUT_FLAG] = value }
        _uiEffect.emit(UiEffect.ShowToast("인증이 만료되었습니다. 다시 로그인해주세요."))
        Log.d(TAG, "[setLogoutFlag] 리프레시 실패, 다시 로그인")
    }
}