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
class TokenAuthenticator(
    private val tokenDataStore: TokenDataStore,
    private val authRetrofit: Retrofit
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {

        // 1) refresh API 자체에서 401 나오면 재시도 금지
        if (response.request().url().encodedPath() == "/api/auth/refresh") {
            return null
        }

        // 2) 재시도 횟수 제한 (안 하면 무한루프)
        if (responseCount(response) >= 2) {
            return null
        }

        val refreshToken = runBlocking {
            tokenDataStore.refreshTokenFlow.first()
        }

        if (refreshToken.isBlank()) {
            return null
        }

        val authService = authRetrofit.create(AuthService::class.java)

        val result = runBlocking {
            try {
                authService.tokenRefresh(AuthDTO.TokenRefreshRequest(refreshToken))
            } catch (e: Exception) {
                // refresh 자체 실패
                tokenDataStore.setLogoutFlag(true)
                return@runBlocking null
            }
        } ?: return null

        // 3) 새 토큰 저장
        runBlocking {
            tokenDataStore.saveTokens(
                AuthDTO.TokenInfo(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken
                )
            )
        }

        // 4) 새 accessToken으로 재요청
        return response.request().newBuilder()
            .header("Authorization", "Bearer ${result.accessToken}")
            .build()
    }

    // 무한 루프 방지용 response count 계산 함수
    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse()
        while (prior != null) {
            result++
            prior = prior.priorResponse()
        }
        return result
    }
}

// TODO: 리프레시 동작 제대로 되는지 확인 필요
//class TokenAuthenticator(private val tokenDataStore: TokenDataStore, private val authRetrofit: Retrofit): Authenticator {
//    override fun authenticate(route: Route?, response: Response): Request? {
//        val refreshToken = runBlocking {
//            tokenDataStore.refreshTokenFlow.first()
//        }
//        if (refreshToken.isEmpty()) return null
//
//        val authService = authRetrofit.create(AuthService::class.java)
//
//        val result = runBlocking {
//            try {
//                authService.tokenRefresh(AuthDTO.TokenRefreshRequest(refreshToken))
//            } catch (e: Exception) {
//                tokenDataStore.setLogoutFlag(true)
//                return@runBlocking null
//            }
//        }
//
//        if (result == null) {
//            runBlocking {
//                tokenDataStore.setLogoutFlag(true)
//            }
//            return null
//        }
//
//        val newAccess = result.accessToken
//        val newRefresh = result.refreshToken
//
//        runBlocking {
//            tokenDataStore.saveTokens(
//                AuthDTO.TokenInfo(
//                    accessToken = newAccess,
//                    refreshToken = newRefresh
//                )
//            )
//        }
//
//        return response.request().newBuilder()
//            .header("Authorization", "Bearer ${newAccess}")
//            .build()
//    }
//}