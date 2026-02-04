package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.param.ApproverQuery
import com.example.attendancemanagementapp.retrofit.service.ApproverService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApproverRepository @Inject constructor(private val service: ApproverService) {
    // 결재 요청 목록 조회
    fun getApprovers(query: ApproverQuery, page: Int): Flow<Result<ApproverDTO.GetApproversResponse>> = flow {
        val response = service.getApprovers(
            approvalType = query.approvalType.toKey(),
            applicationType = query.applicationType.toKey(),
            year = query.year,
            month = query.month,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}