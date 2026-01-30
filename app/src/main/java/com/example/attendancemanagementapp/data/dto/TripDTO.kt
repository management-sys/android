package com.example.attendancemanagementapp.data.dto

import com.example.attendancemanagementapp.data.dto.VacationDTO.YearsInfo
import com.squareup.moshi.Json

object TripDTO {
    /* 출장 신청 요청 */
    data class AddTripRequest(
        @Json(name = "bgnde")           val startDate: String = "",                         // 출장 시작일시
        @Json(name = "bsrpPlace")       val place: String = "",                             // 출장지
        @Json(name = "bsrpPurps")       val purpose: String = "",                           // 출장목적
        @Json(name = "bsrpSe")          val type: String = "",                              // 출장구분 (국내/해외)
        @Json(name = "cardUseList")     val cardUsages: List<CardUsagesInfo> = emptyList(), // 법인카드 사용 목록
        @Json(name = "cn")              val content: String = "",                           // 품의 내용
        @Json(name = "confmerIds")      val approverIds: List<String> = emptyList(),        // 승인자 아이디 목록 (승인 순서대로)
        @Json(name = "endde")           val endDate: String = "",                           // 출장 종료일시
        @Json(name = "nmprs")           val attendeeIds: List<String> = emptyList(),        // 동행자 아이디 목록
        @Json(name = "vhcleUseList")    val carUsages: List<CarUsagesInfo> = emptyList()    // 법인차량 사용 목록
    )

    /* 법인카드 사용 목록 데이터 */
    data class CardUsagesInfo(
        @Json(name = "bgnde")   val startDate: String = "", // 사용 시작일시
        @Json(name = "cardId")  val id: String = "",        // 카드 아이디
        @Json(name = "endde")   val endDate: String = ""    // 사용 종료일시
    )

    /* 법인차량 사용 목록 데이터 */
    data class CarUsagesInfo(
        @Json(name = "bgnde")       val startDate: String = "", // 사용 시작일시
        @Json(name = "driverId")    val driverId: String = "",  // 운전자 아이디
        @Json(name = "endde")       val endDate: String = "",   // 사용 종료일시
        @Json(name = "vhcleId")     val id: String = ""         // 차량 아이디
    )

    /* 출장 현황 상세 조회 응답 */
    data class GetTripResponse(
        @Json(name = "beginHour")   val startHour: String = "",                         // 출장 시작 시
        @Json(name = "beginMnt")    val startMin: String = "",                          // 출장 시작 분
        @Json(name = "bgnde")       val startDate: String = "",                         // 출장 시작일시
        @Json(name = "bsrpId")      val id: String = "",                                // 출장 아이디
        @Json(name = "bsrpPlace")   val place: String = "",                             // 출장지
        @Json(name = "bsrpPurps")   val purpose: String = "",                           // 출장 목적
        @Json(name = "bsrpSe")      val type: String = "",                              // 출장 구분
        @Json(name = "cardList")    val cards: List<CardsInfo> = emptyList(),           // 법인카드 목록
        @Json(name = "clsf")        val grade: String = "",                             // 직급
        @Json(name = "cn")          val content: String = "",                           // 품의 내용
        @Json(name = "confmAt")     val status: String = "",                            // 상태
        @Json(name = "confmerId")   val approverId: String = "",                        // 승인자 아이디
        @Json(name = "confmerNm")   val approverName: String = "",                      // 승인자 이름
        @Json(name = "deptNm")      val departmentName: String = "",                    // 부서명
        @Json(name = "endHour")     val endHour: String = "",                           // 출장 종료 시
        @Json(name = "endMnt")      val endMin: String = "",                            // 출장 종료 분
        @Json(name = "endde")       val endDate: String = "",                           // 출장 종료일시
        @Json(name = "nmprList")    val attendees: List<AttendeesInfo> = emptyList(),   // 동행자 목록
        @Json(name = "period")      val period: String = "",                            // 출장 기간
        @Json(name = "returnResn")  val rejection: String? = "",                        // 반려사유
        @Json(name = "rgsde")       val appliedDate: String = "",                       // 신청일
        @Json(name = "rportAt")     val hasReport: String = "",                         // 출장 복명서 등록 여부
        @Json(name = "userId")      val userId: String = "",                            // 사용자 아이디
        @Json(name = "userNm")      val userName: String = "",                          // 이름
        @Json(name = "vhcleList")   val cars: List<CarsInfo> = emptyList(),             // 법인차량 목록
    )

    /* 법인카드 목록 데이터 */
    data class CardsInfo(
        @Json(name = "beginHour")   val startHour: String = "", // 시작 시
        @Json(name = "beginMnt")    val startMin: String = "",  // 시작 분
        @Json(name = "bgnde")       val startDate: String = "", // 시작일시
        @Json(name = "endHour")     val endHour: String = "",   // 종료 시
        @Json(name = "endMnt")      val endMin: String = "",    // 종료 분
        @Json(name = "endde")       val endDate: String = "",   // 종료일시
        @Json(name = "id")          val id: String,             // 아이디
        @Json(name = "name")        val name: String            // 이름
    )

    /* 동행자 목록 데이터 */
    data class AttendeesInfo(
        @Json(name = "id")      val id: String,     // 아이디
        @Json(name = "name")    val name: String    // 이름
    )

    /* 법인차량 목록 데이터 */
    data class CarsInfo(
        @Json(name = "beginHour")   val startHour: String = "", // 시작 시
        @Json(name = "beginMnt")    val startMin: String = "",  // 시작 분
        @Json(name = "bgnde")       val startDate: String = "", // 시작일시
        @Json(name = "driverId")    val driverId: String = "",  // 운전자 아이디
        @Json(name = "driverNm")    val driverNm: String = "",  // 운전자 이름
        @Json(name = "endHour")     val endHour: String = "",   // 종료 시
        @Json(name = "endMnt")      val endMin: String = "",    // 종료 분
        @Json(name = "endde")       val endDate: String = "",   // 종료일시
        @Json(name = "id")          val id: String = "",        // 아이디
        @Json(name = "name")        val name: String = ""       // 이름
    )

    /* 출장 현황 목록 조회 응답 */
    data class GetTripsResponse(
        @Json(name = "bsrpList")    val trips: List<TripsInfo> = emptyList(),               // 출장 목록
        @Json(name = "inCo")        val inCnt: String = "",                                 // 국내 출장 수
        @Json(name = "outCo")       val outCnt: String = "",                                // 해외 출장 수
        @Json(name = "pageInfo")    val pageInfo: PageDTO.PageInfo = PageDTO.PageInfo(),    // 페이지 정보
        @Json(name = "totalCo")     val totalCnt: String = "",                              // 전체 출장 수
        @Json(name = "yrycList")    val years: List<YearsInfo> = emptyList()                // 연차 목록
    )

    /* 출장 목록 데이터 */
    data class TripsInfo(
        @Json(name = "bgnde")   val startDate: String = "",     // 출장 시작일시
        @Json(name = "bsrpId")  val id: String = "",            // 출장 아이디
        @Json(name = "bsrpSe")  val type: String = "",          // 구분
        @Json(name = "confmAt") val status: String = "",        // 상태
        @Json(name = "endde")   val endDate: String = "",       // 출장 종료일시
        @Json(name = "period")  val period: String = "",        // 출장 기간
        @Json(name = "rgsde")   val appliedDate: String = ""    // 신청일
    )

    /* 출장 품의서 수정 요청 */
    data class UpdateTripRequest(
        @Json(name = "bgnde")           val startDate: String = "",
        @Json(name = "bsrpPlace")       val place: String = "",                             // 출장지
        @Json(name = "bsrpPurps")       val purpose: String = "",                           // 출장 목적
        @Json(name = "bsrpSe")          val type: String = "",                              // 출장 구분 (국내/해외)
        @Json(name = "cardUseList")     val cardUsages: List<CardUsagesInfo> = emptyList(), // 법인카드 사용 목록
        @Json(name = "cn")              val content: String = "",                           // 품의 내용
        @Json(name = "confmerIds")      val approverIds: List<String> = emptyList(),        // 승인자 아이디 목록 (승인 순서대로)
        @Json(name = "endde")           val endDate: String = "",                           // 종료일시
        @Json(name = "nmprs")           val attendeeIds: List<String> = emptyList(),        // 동행자 아이디 목록
        @Json(name = "vhcleUseList")    val carUsages: List<CarUsagesInfo> = emptyList()    // 법인차량 사용 목록
    )

    /* 출장 복명서 등록 요청 */
    data class AddTripReportRequest(
        @Json(name = "bsrpId")      val tripId: String = "",                                    // 출장 아이디
        @Json(name = "cn")          val content: String = "",                                   // 복명내용
        @Json(name = "confmerIds")  val approverIds: List<String> = emptyList(),                // 승인자 아이디 목록 (승인 순서대로)
        @Json(name = "trvctList")   val tripExpenses: List<AddTripExpenseInfo> = emptyList()    // 여비 계산 목록
    )

    /* 여비 계산 목록 등록 데이터 */
    data class AddTripExpenseInfo(
        @Json(name = "amount")      val amount: Int = 0,        // 금액
        @Json(name = "se")          val type: String = "",      // 구분 (개인/법인)
        @Json(name = "setleMbyId")  val buyerId: String = "",   // 결제 주체 아이디 (법인카드코드: CARD / 결제자아이디: USER)
        @Json(name = "ty")          val category: String = ""   // 타입 (교통, 운임비 등)
    )

    /* 출장 복명서 조회 응답 */
    data class GetTripReportResponse(
        @Json(name = "bsrpId")      val tripId: String = "",                                // 출장 아이디
        @Json(name = "cn")          val content: String = "",                               // 복명내용
        @Json(name = "confmAt")     val status: String = "",                                // 상태
        @Json(name = "confmerId")   val approverId: String = "",                            // 승인자 아이디
        @Json(name = "confmerNm")   val approverName: String = "",                          // 승인자 이름
        @Json(name = "returnResn")  val rejection: String? = "",                            // 반려사유
        @Json(name = "rgsde")       val appliedDate: String = "",                           // 신청일
        @Json(name = "trvctList")   val tripExpenses: List<TripExpenseInfo> = emptyList()   // 여비 계산 목록
    )

    /* 여비 계산 목록 데이터 */
    data class TripExpenseInfo(
        @Json(name = "amount")      val amount: Int = 0,        // 금액
        @Json(name = "bsrpTrvctId") val id: Int = 0,            // 여비 아이디
        @Json(name = "se")          val type: String = "",      // 구분 (개인/법인)
        @Json(name = "setleMbyId")  val buyerId: String = "",   // 결제 주체 아이디 (법인카드코드/결제자아이디)
        @Json(name = "setleMbyNm")  val buyerNm: String = "",   // 결제 주체명 (법인카드/결제자)
        @Json(name = "ty")          val category: String = ""   // 타입 (교통, 운임비 등)
    )

    /* 출장 복명서 수정 요청 */
    data class UpdateTripReportRequest(
        @Json(name = "cn")          val content: String = "",                                   // 복명내용
        @Json(name = "confmerIds")  val approverIds: List<String> = emptyList(),                // 승인자 아이디
        @Json(name = "trvctList")   val tripExpenses: List<AddTripExpenseInfo> = emptyList()    // 여비 계산 목록
    )
}