package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import retrofit2.http.GET

interface AuthorService {
    // 권한 목록 조회
    @GET("/api/authors")
    suspend fun getAuthors(): List<AuthorDTO.GetAuthorsResponse>
}