package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.CarDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CarService {
    // 차량 목록 조회 및 검색
    @GET("/api/vhcles")
    suspend fun getCars(
        @Query("keyword") keyword: String,  // 검색 키워드
        @Query("searchType") type: String,  // 검색 타입 (전체, 차량명, 차량번호)
    ): CarDTO.GetCarsResponse

    // 차량 등록
    @POST("/api/vhcles")
    suspend fun addCar(
        @Body request: CarDTO.AddCarRequest
    ): CarDTO.GetCarResponse

    // 차량 정보 상세 조회
    @GET("/api/vhcles/{vhcleId}")
    suspend fun getCar(
        @Path("vhcleId") id: String
    ): CarDTO.GetCarResponse

    // 차량 정보 수정
    @PUT("/api/vhcles/{vhcleId}")
    suspend fun updateCar(
        @Path("vhcleId") id: String,
        @Body request: CarDTO.AddCarRequest
    ): CarDTO.GetCarResponse

    // 차량 정보 삭제
    @DELETE("/api/vhcles/{vhcleId}")
    suspend fun deleteCar(
        @Path("vhcleId") id: String,
    )

    // 전체 차량 예약 현황 목록 조회 및 검색
    @GET("/api/vhcles/rsrvs")
    suspend fun getReservations(
        @Query("keyword") keyword: String,
        @Query("searchType") type: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): CarDTO.GetCarUsagesResponse

    // 전체 차량 사용 이력 목록 조회 및 검색
    @GET("/api/vhcles/usHst")
    suspend fun getHistories(
        @Query("keyword") keyword: String,
        @Query("searchType") type: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): CarDTO.GetCarUsagesResponse
}