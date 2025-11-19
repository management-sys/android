package com.example.attendancemanagementapp.retrofit.token

import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.data.dto.AuthDTO
import com.example.attendancemanagementapp.retrofit.service.AuthService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit

/* 토큰 리프레시 처리 */
class TokenAuthenticator(private val tokenDataStore: TokenDataStore, private val authRetrofit: Retrofit): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking {
            tokenDataStore.refreshTokenFlow.first()
        }
        if (refreshToken.isEmpty()) return null

        val authService = authRetrofit.create(AuthService::class.java)

        val result = runBlocking {
            try {
                authService.tokenRefresh(AuthDTO.TokenRefreshRequest(refreshToken))
            } catch (e: Exception) {
                tokenDataStore.setLogoutFlag(true)
                return@runBlocking null
            }
        }

        if (result == null) {
            runBlocking {
                tokenDataStore.setLogoutFlag(true)
            }
            return null
        }

        val newAccess = result.accessToken
        val newRefresh = result.refreshToken

        runBlocking {
            tokenDataStore.saveTokens(
                AuthDTO.TokenInfo(
                    accessToken = newAccess,
                    refreshToken = newRefresh
                )
            )
        }

        return response.request().newBuilder()
            .header("Authorization", "Bearer ${newAccess}")
            .build()
    }
}