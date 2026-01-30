package com.example.attendancemanagementapp.data.repository

import android.content.Context
import android.net.Uri
import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.data.param.TripsQuery
import com.example.attendancemanagementapp.retrofit.service.TripService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripRepository @Inject constructor(private val service: TripService, private val fileRepository: FileRepository) {
    // 출장 현황 목록 조회
    fun getTrips(query: TripsQuery, page: Int): Flow<Result<TripDTO.GetTripsResponse>> = flow {
        val response = service.getTrips(
            year = query.year,
            filter = query.filter,
            page = page
        )
        emit (Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 신청
    fun addTrip(request: TripDTO.AddTripRequest): Flow<Result<Unit>> = flow {
        val response = service.addTrip(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 현황 상세 조회
    fun getTrip(id: String): Flow<Result<TripDTO.GetTripResponse>> = flow {
        val response = service.getTrip(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 품의서 수정
    fun updateTrip(id: String, request: TripDTO.UpdateTripRequest): Flow<Result<TripDTO.GetTripResponse>> = flow {
        val response = service.updateTrip(
            id = id,
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 신청 삭제
    fun deleteTrip(id: String): Flow<Result<Unit>> = flow {
        val response = service.deleteTrip(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 신청 취소
    fun cancelTrip(id: String): Flow<Result<TripDTO.GetTripResponse>> = flow {
        val response = service.cancelTrip(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 품의서 다운로드(PDF)
    fun downloadTripPdf(id: String): Flow<Result<Uri>> = flow {
        val response = service.downloadTripPdf(id = id)
        val body = response.body() ?: throw IllegalStateException("Empty body")

        val uri = withContext(Dispatchers.IO) {
            fileRepository.savePdf(
                body = body,
                contentDisposition = response.headers()["content-disposition"],
                fallback = "trip_$id.pdf"
            )
        }

        emit(Result.success(uri))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 이전 승인자 불러오기
    fun getPrevApprovers(): Flow<Result<ApproverDTO.GetPrevApproversResponse>> = flow {
        val response = service.getPrevApprovers()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 복명서 등록
    fun addTripReport(request: TripDTO.AddTripReportRequest): Flow<Result<Unit>> = flow {
        val response = service.addTripReport(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 복명서 조회
    fun getTripReport(id: String): Flow<Result<TripDTO.GetTripReportResponse>> = flow {
        val response = service.getTripReport(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 복명서 삭제
    fun deleteTripReport(id: String): Flow<Result<Unit>> = flow {
        val response = service.deleteTripReport(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 출장 복명서 취소
    fun cancelTripReport(id: String): Flow<Result<TripDTO.GetTripReportResponse>> = flow {
        val response = service.cancelTripReport(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}