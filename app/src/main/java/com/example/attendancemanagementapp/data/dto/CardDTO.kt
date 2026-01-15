package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object CardDTO {
    /* 카드 목록 조회 및 검색 응답 */
    data class GetCardsResponse(
        @Json(name = "cardList")    val cards: List<CardsInfo> = emptyList()    // 카드 목록
    )

    /* 카드 목록 데이터 */
    data class CardsInfo(
        @Json(name = "cardId")  val id: String = "",    // 카드 아이디
        @Json(name = "cardNm")  val name: String = ""   // 카드명
    )

    /* 카드 등록 요청 */
    data class AddCardRequest(
        @Json(name = "cardNm")      val name: String = "",      // 카드명
        @Json(name = "chargerId")   val managerId: String = "", // 담당자 아이디
        @Json(name = "rm")          val remark: String = "",    // 비고
        @Json(name = "sttus")       val status: String = ""     // 상태 (정상, 재발급, 폐기)
    )

    /* 카드 정보 상세 조회 응답 */
    data class GetCardResponse(
        @Json(name = "cardId")      val id: String = "",                                    // 카드 아이디
        @Json(name = "cardNm")      val name: String = "",                                  // 카드명
        @Json(name = "chargerId")   val managerId: String = "",                             // 담당자 아이디
        @Json(name = "chargerNm")   val managerName: String = "",                           // 담당자
        @Json(name = "pageInfo")    val pageInfo: PageDTO.PageInfo = PageDTO.PageInfo(),    // 페이지 정보
        @Json(name = "rm")          val remark: String = "",                                // 비고
        @Json(name = "sttus")       val status: String = "",                                // 상태 (정상, 재발급, 폐기)
        @Json(name = "useHistList") val histories: List<HistoryInfo> = emptyList()          // 예약 현황 목록
    )

    /* 예약 현황 목록 데이터 */
    data class HistoryInfo(
        @Json(name = "applcntNm")       val applicantName: String,  // 신청자
        @Json(name = "bgnde")           val startDate: String,      // 사용 시작일시
        @Json(name = "cardUseHistId")   val id: String,             // 카드 사용 이력 아이디
        @Json(name = "deptNm")          val departmentName: String, // 부서
        @Json(name = "endde")           val endDate: String,        // 사용 종료일시
        @Json(name = "status")          val status: String          // 상태 (예약, 사용중, 반납)
    )

    /* 카드 예약현황/사용이력 목록 조회 및 검색 응답 */
    data class GetCardUsagesResponse(
        @Json(name = "pageInfo")    val pageInfo: PageDTO.PageInfo = PageDTO.PageInfo(),    // 페이지 정보
        @Json(name = "useHistList") val usages: List<CardUsageInfo> = emptyList()           // 카드 예약현황/사용이력 목록
    )

    /* 카드 예약현황/사용이력 목록 데이터 */
    data class CardUsageInfo(
        @Json(name = "applcntNm")       val applicantName: String,
        @Json(name = "beginHour")       val startHour: String,
        @Json(name = "beginMnt")        val startMinute: String,
        @Json(name = "bgnde")           val startDate: String,
        @Json(name = "cardNm")          val name: String,
        @Json(name = "cardUserHistId")  val id: String,
        @Json(name = "deptNm")          val departmentName: String,
        @Json(name = "endHour")         val endHour: String,
        @Json(name = "endMnt")          val endMinute: String,
        @Json(name = "endde")           val endDate: String,
    )
}