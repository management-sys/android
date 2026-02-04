package com.example.attendancemanagementapp.data.repository

import android.net.Uri
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.VacationDTO
import com.example.attendancemanagementapp.data.param.VacationsQuery
import com.example.attendancemanagementapp.retrofit.service.VacationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class VacationRepository @Inject constructor(private val service: VacationService, private val fileRepository: FileRepository) {
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

    // 휴가 정보 수정
    fun updateVacation(vacationId: String, request: VacationDTO.UpdateVacationRequest): Flow<Result<VacationDTO.GetVacationResponse>> = flow {
        val response = service.updateVacation(
            vacationId = vacationId,
            request = request
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
    fun getPrevApprovers(userId: String): Flow<Result<EmployeeDTO.GetPrevApproversResponse>> = flow {
        val response = service.getPrevApprovers(
            userId = userId
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 현황 목록 조회
    fun getVacations(userId: String, query: VacationsQuery, page: Int): Flow<Result<VacationDTO.GetVacationsResponse>> = flow {
        val response = service.getVacations(
            userId = userId,
            year = query.year,
            filter =  query.filter.toKey(),
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 휴가 신청서 다운로드(PDF)
    fun downloadVacationPdf(vacationId: String): Flow<Result<Uri>> = flow {
        val response = service.downloadVacationPdf(vacationId = vacationId)
        val body = response.body() ?: throw IllegalStateException("Empty body")

        val uri = withContext(Dispatchers.IO) {
            fileRepository.savePdf(
                body = body,
                contentDisposition = response.headers()["content-disposition"],
                fallback = "vacation_$vacationId.pdf"
            )
        }

        emit(Result.success(uri))
    }.catch { e ->
        emit(Result.failure(e))
    }
}