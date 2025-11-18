package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.retrofit.service.AuthorService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthorRepository @Inject constructor(private val service: AuthorService) {
    // 권한 목록 조회
    fun getAuthors(): Flow<Result<List<AuthorDTO.GetAuthorsResponse>>> = flow {
        val response = service.getAuthors()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}