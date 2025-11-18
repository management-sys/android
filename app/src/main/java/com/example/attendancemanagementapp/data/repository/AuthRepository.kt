package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.AuthDTO
import com.example.attendancemanagementapp.retrofit.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepository @Inject constructor(private val service: AuthService) {
//    private val service = RetrofitInstance.authService

    // 로그인
    fun login(request: AuthDTO.LoginRequest): Flow<Result<AuthDTO.TokenInfo>> = flow {
        val response = service.login(request)
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 로그아웃
    fun logout(): Flow<Result<String>> = flow {
        val response = service.logout()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}