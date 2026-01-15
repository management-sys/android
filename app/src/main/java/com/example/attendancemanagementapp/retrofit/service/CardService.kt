package com.example.attendancemanagementapp.retrofit.service

import com.example.attendancemanagementapp.data.dto.CardDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CardService {
    // 카드 목록 조회 및 검색
    @GET("/api/cards")
    suspend fun getCards(
        @Query("keyword") keyword: String,
        @Query("searchType") type: String
    ): CardDTO.GetCardsResponse

    // 카드 등록
    @POST("/api/cards")
    suspend fun addCard(
        @Body request: CardDTO.AddCardRequest
    ): CardDTO.GetCardResponse

    // 카드 정보 상세 조회
    @GET("/api/cards/{cardId}")
    suspend fun getCard(
        @Path("cardId") id: String
    ): CardDTO.GetCardResponse

    // 카드 정보 수정
    @PUT("/api/cards/{cardId}")
    suspend fun updateCard(
        @Path("cardId") id: String,
        @Body request: CardDTO.AddCardRequest
    ): CardDTO.GetCardResponse

    // 카드 정보 삭제
    @DELETE("/api/cards/{cardId}")
    suspend fun deleteCard(
        @Path("cardId") id: String
    )

    // 카드 예약 현황 목록 조회 및 검색
    @GET("/api/cards/rsrvs")
    suspend fun getReservations(
        @Query("keyword") keyword: String,
        @Query("searchType") type: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): CardDTO.GetCardUsagesResponse

    // 카드 사용 이력 목록 조회 및 검색
    @GET("/api/cards/rsrvs")
    suspend fun getHistories(
        @Query("keyword") keyword: String,
        @Query("searchType") type: String,
        @Query("page") page: Int? = null,  // 페이지 번호: 0부터 시작
        @Query("size") size: Int? = null   // 페이지 당 데이터 개수
    ): CardDTO.GetCardUsagesResponse
}