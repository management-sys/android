package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.retrofit.service.VacationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VacationRepository @Inject constructor(private val service: VacationService) {
    // 휴가 신청
    fun addVacation(request: VacationDTO.AddVacationRequest): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.addVacation(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청 상세 조회
    fun getVacation(vacationId: String): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.getVacation(
            vacationId = vacationId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청 삭제
    fun deleteVacation(vacationId: String): Flow<Result<Unit>> = flow {
        val response = service.deleteVacation(
            vacationId = vacationId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청 취소
    fun cancelVacation(vacationId: String): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.cancelVacation(
            vacationId = vacationId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 이전 승인자 불러오기
    fun getPrevApprovers(userId: String): Flow<Result<VacationDTO.GetPrevApproversResponse>> = flow {
        val response = service.getPrevApprovers(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}