package com.example.attendancemanagementapp.retrofit

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import retrofit2.http.GET

interface JsonService {
    // 전체 공통코드 조회
    @GET("/api/codes")
    suspend fun getCommonCodes(): CommonCodeDTO.GetCommonCodesResponse
}