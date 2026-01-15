package com.example.attendancemanagementapp.data.repository

import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.retrofit.service.CardService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CardRepository @Inject constructor(private val service: CardService) {
    // 카드 목록 조회 및 검색
    fun getCards(keyword: String, type: String): Flow<Result<CardDTO.GetCardsResponse>> = flow {
        val response = service.getCards(
            keyword = keyword,
            type = type
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 카드 등록
    fun addCard(request: CardDTO.AddCardRequest): Flow<Result<CardDTO.GetCardResponse>> = flow {
        val response = service.addCard(
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 카드 정보 상세 조회
    fun getCard(id: String): Flow<Result<CardDTO.GetCardResponse>> = flow {
        val response = service.getCard(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 카드 정보 수정
    fun updateCard(id: String, request: CardDTO.AddCardRequest): Flow<Result<CardDTO.GetCardResponse>> = flow {
        val response = service.updateCard(
            id = id,
            request = request
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 카드 정보 삭제
    fun deleteCard(id: String): Flow<Result<Unit>> = flow {
        val response = service.deleteCard(
            id = id
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 카드 예약 현황 목록 조회 및 검색
    fun getReservations(keyword: String, type: String, page: Int): Flow<Result<CardDTO.GetCardUsagesResponse>> = flow {
        val response = service.getReservations(
            keyword = keyword,
            type = type,
            page = page
        )
        emit(Result.success(response))
    }.catch { e ->
        emit(Result.failure(e))
    }

    // 카드 예약 현황 목록 조회 및 검색
    fun getHistories(keyword: String, type: String, page: Int): Flow<Result<CardDTO.GetCardUsagesResponse>> = flow {
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