package com.example.attendancemanagementapp.retrofit.token

import android.util.Log
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/* API 헤더에 토큰 자동 주입 */
class AuthInterceptor(private val tokenDataStore: TokenDataStore): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url().encodedPath()

        // 토큰 제외할 요청 리스트
        val noAuthPaths = listOf(
            "/api/auth/login",  // 로그인
            "/api/auth/refresh" // 토큰 재발급
        )

        // 제외 대상이면 그대로 진행
        if (noAuthPaths.contains(path)) {
            return chain.proceed(request)
        }

        // 그 외 요청은 토큰 추가
        val accessToken = runBlocking { tokenDataStore.accessTokenFlow.first() }

        val newRequest = request.newBuilder()
            .addHeader("Authorization", "Bearer ${accessToken}")
            .build()

        return chain.proceed(newRequest)
    }
}