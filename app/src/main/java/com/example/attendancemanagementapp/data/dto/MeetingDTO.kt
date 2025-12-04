package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object MeetingDTO {
    /* 회의록 상세 조회 응답 */
    data class GetMeetingResponse(
        @Json(name = "bgnde")               val startDate: String = "",                         // 회의 시작 일시 (yyyy-MM-ddTHH:mm:ss)
        @Json(name = "endde")               val endDate: String = "",                           // 회의 종료 일시 (yyyy-MM-ddTHH:mm:ss)
        @Json(name = "mtgAtdrnList")        val attendees: List<AttendeesInfo> = emptyList(),   // 회의 참석자 리스트
        @Json(name = "mtgCn")               val content: String = "",                           // 회의 내용
        @Json(name = "mtgCtUseDtlsList")    val expenses: List<ExpensesInfo> = emptyList(),     // 회의비 사용 내역 리스트
        @Json(name = "mtgRcordId")          val id: Long = 0,                                   // 회의록 아이디
        @Json(name = "place")               val place: String = "",                             // 회의 장소
        @Json(name = "prjctNm")             val projectName: String = "",                       // 프로젝트명
        @Json(name = "rm")                  val remark: String? = "",                           // 비고
        @Json(name = "sj")                  val title: String = ""                              // 회의록 제목
    )

    /* 회의 참석자 목록 데이터 */
    data class AttendeesInfo(
        @Json(name = "atdrnTy")     val type: String,           // 회의 참석자 유형 (I: 내부인, O: 외부인)
        @Json(name = "mtgAtdrnId")  val id: Long,               // 회의 참석자 아이디
        @Json(name = "nm")          val name: String,           // 이름
        @Json(name = "ofcps")       val grade: String,          // 직위
        @Json(name = "psitn")       val department: String      // 소속
    )

    /* 회의비 사용 내역 목록 데이터 */
    data class ExpensesInfo(
        @Json(name = "amount")  val amount: Int,    // 금액
        @Json(name = "mtgCtId") val id: Long,       // 회의비 아이디
        @Json(name = "se")      val type: String    // 여비 내역
    )

    /* 회의록 등록 요청 */
    data class AddMeetingRequest(
        @Json(name = "bgnde")               val startDate: String = "",                             // 시작일
        @Json(name = "endde")               val endDate: String = "",                               // 종료일
        @Json(name = "mtgAtdrnList")        val attendees: List<AddAttendeesInfo> = emptyList(),    // 회의 참석자 리스트
        @Json(name = "mtgCn")               val content: String? = "",                              // 회의 내용
        @Json(name = "mtgCtUseDtlsList")    val expenses: List<AddExpensesInfo> = emptyList(),      // 회의비 사용 내역 리스트
        @Json(name = "place")               val place: String = "",                                 // 장소
        @Json(name = "prjctId")             val projectId: String = "",                             // 프로젝트 아이디
        @Json(name = "rm")                  val remark: String? = "",                               // 비고
        @Json(name = "sj")                  val title: String = ""                                  // 제목
    )

    /* 회의 참석자 등록 목록 데이터 */
    data class AddAttendeesInfo(
        @Json(name = "atdrnTy")     val type: String,           // 회의 참석자 유형 (I: 내부인, O: 외부인)
        @Json(name = "nm")          val name: String?,          // 이름
        @Json(name = "ofcps")       val grade: String?,         // 직위
        @Json(name = "psitn")       val department: String?,    // 소속
        @Json(name = "userId")      val userId: String?         // 내부인 사용자 ID (참석자 유형이 I일 경우)
    )

    /* 회의비 사용 내역 등록 목록 데이터 */
    data class AddExpensesInfo(
        @Json(name = "amount")  val amount: Int,   // 금액
        @Json(name = "se")      val type: String    // 여비 내역
    )
}