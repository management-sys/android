package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.RetrofitInstance
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.retrofit.service.CommonCodeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CommonCodeRepository @Inject constructor() {
    private val service = RetrofitInstance.commonCodeService

    // 공통코드 목록 조회 및 검색
    fun getCommonCodes(searchType: SearchType, searchKeyword: String, page: Int): Flow<Result<CommonCodeDTO.GetCommonCodesResponse>> = flow {
        val response = service.getCommonCodes(
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
        val response = service.getCommonCodeDetail(
            code = code
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 공통코드 등록
    fun addCommonCode(commonCodeData: CommonCodeDTO.AddUpdateCommonCodeRequest): Flow<Result<String>> = flow {
        val response = service.addCommonCode(
            request = commonCodeData
        )
        emit(Result.success(response.code))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 공통코드 수정
    fun updateCommonCode(commonCodeData: CommonCodeDTO.AddUpdateCommonCodeRequest): Flow<Result<String>> = flow {
        val response = service.updateCommonCode(
            request = commonCodeData
        )
        emit(Result.success(response.code))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 공통카드 삭제
    fun deleteCommonCode(code: String): Flow<Result<Int>> = flow {
        val response = service.deleteCommonCode(
            code = code
        )
        emit(Result.success(response.count))
    }.catch { e ->
        emit(Result.failure(e))
    }
}