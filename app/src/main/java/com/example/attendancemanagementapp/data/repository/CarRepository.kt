package com.example.attendancemanagementapp.data.repository

import androidx.compose.runtime.key
import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.retrofit.service.CarService
import com.example.attendancemanagementapp.retrofit.service.CommonCodeService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CarRepository @Inject constructor(private val service: CarService) {
    // 차량 목록 조회 및 검색
    fun getCars(keyword: String, type: String): Flow<Result<CarDTO.GetCarsResponse>> = flow {
        val response = service.getCars(
            keyword = keyword,
            type = type
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 차량 등록
    fun addCar(request: CarDTO.AddCarRequest): Flow<Result<CarDTO.GetCarResponse>> = flow {
        val response = service.addCar(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 차량 정보 상세 조회
    fun getCar(id: String): Flow<Result<CarDTO.GetCarResponse>> = flow {
        val response = service.getCar(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 차량 정보 수정
    fun updateCar(id: String, request: CarDTO.AddCarRequest): Flow<Result<CarDTO.GetCarResponse>> = flow {
        val response = service.updateCar(
            id = id,
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 차량 정보 삭제
    fun deleteCar(id: String): Flow<Result<Unit>> = flow {
        val response = service.deleteCar(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 전체 차량 예약 현황 목록 조회 및 검색
    fun getReservations(keyword: String, type: String, page: Int): Flow<Result<CarDTO.GetCarUsagesResponse>> = flow {
        val response = service.getReservations(
            keyword = keyword,
            type = type,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 전체 차량 사용 이력 목록 조회 및 검색
    fun getHistories(keyword: String, type: String, page: Int): Flow<Result<CarDTO.GetCarUsagesResponse>> = flow {
        val response = service.getHistories(
            keyword = keyword,
            type = type,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }
}