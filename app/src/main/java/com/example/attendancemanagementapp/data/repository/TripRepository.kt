package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.ApproverDTO
import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.data.param.TripsQuery
import com.example.attendancemanagementapp.retrofit.service.TripService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TripRepository @Inject constructor(private val service: TripService) {
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

    // 이전 승인자 불러오기
    fun getPrevApprovers(): Flow<Result<ApproverDTO.GetPrevApproversResponse>> = flow {
        val response = service.getPrevApprovers()
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}