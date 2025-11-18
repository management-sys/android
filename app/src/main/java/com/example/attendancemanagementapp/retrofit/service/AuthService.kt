package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.AuthDTO
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
    // 로그인
    @POST("/api/auth/login")
    suspend fun login(
        @Body request: AuthDTO.LoginRequest
    ): AuthDTO.TokenInfo

    // 로그아웃
    @POST("/api/auth/logout")
    suspend fun logout(): String

    // 토큰 재발급
    @POST("/api/auth/refresh")
    suspend fun tokenRefresh(
        @Body request: AuthDTO.TokenRefreshRequest
    ): AuthDTO.TokenInfo
}