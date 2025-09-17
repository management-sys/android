package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.JsonService
import com.example.attendancemanagementapp.retrofit.RetrofitInstance
import com.example.attendancemanagementapp.retrofit.param.SearchType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommonCodeRepository @Inject constructor() {
    private val jsonService: JsonService = RetrofitInstance.retrofit.create(JsonService::class.java)

    // 공통코드 목록 조회
    fun getCommonCodes(searchType: SearchType, searchKeyword: String, page: Int): Flow<Result<CommonCodeDTO.GetCommonCodesResponse>> = flow {
        val response = jsonService.getCommonCodes(
            searchType = searchType.toString(),
            searchKeyword = searchKeyword,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 공통코드 상세 조회
    fun getCommonCodeDetail(code: String): Flow<Result<CommonCodeDTO.CommonCodeInfo>> = flow {
        val response = jsonService.getCommonCodeDetail(
            code = code
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 공통코드 등록
    fun addCommonCode(commonCodeData: CommonCodeDTO.AddUpdateCommonCodeRequest): Flow<Result<String>> = flow {
        val response = jsonService.addCommonCode(
            request = commonCodeData
        )
        emit(Result.success(response.code))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 공통코드 수정
    fun updateCommonCode(commonCodeData: CommonCodeDTO.AddUpdateCommonCodeRequest): Flow<Result<String>> = flow {
        val response = jsonService.updateCommonCode(
            request = commonCodeData
        )
        emit(Result.success(response.code))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 공통카드 삭제
    fun deleteCommonCode(code: String): Flow<Result<Int>> = flow {
        val response = jsonService.deleteCommonCode(
            code = code
        )
        emit(Result.success(response.count))
    }.catch { e ->
        emit(Result.failure(e))
    }
}