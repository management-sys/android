package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object AuthDTO {
    /* 로그인 요청 */
    data class LoginRequest(
        @Json(name = "loginId")     val id: String,         // 로그인 아이디
        @Json(name = "password")    val password: String    // 비밀번호
    )

    /* 토큰 데이터 */
    data class TokenInfo(
        @Json(name = "accessToken")     val accessToken: String,    // 액세스 토큰
        @Json(name = "refreshToken")    val refreshToken: String    // 리프레시 토큰
    )

    /* 토큰 재발급 */
    data class TokenRefreshRequest(
        @Json(name = "refreshToken")    val refreshToken: String    // 리프레시 토큰
    )
}