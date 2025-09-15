package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.JsonService
import com.example.attendancemanagementapp.retrofit.RetrofitInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommonCodeRepository @Inject constructor() {
    private val jsonService: JsonService = RetrofitInstance.retrofit.create(JsonService::class.java)

    // 전체 공통코드 조회
    fun getCommonCodes(): Flow<Result<List<CommonCodeDTO.CommonCodesInfo>>> = flow {
        val response = jsonService.getCommonCodes()
        emit(Result.success(response.content))
    }.catch { e ->
        emit(Result.failure(e))
    }
}