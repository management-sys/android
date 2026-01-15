package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object CarDTO {
    /* 차량 목록 조회 및 검색 응답 */
    data class GetCarsResponse(
        @Json(name = "vhcleList")   val cars: List<CarsInfo> = emptyList()  // 차량 목록
    )

    /* 차량 목록 데이터 */
    data class CarsInfo(
        @Json(name = "vhcleId") val id: String = "",    // 차량 아이디
        @Json(name = "vhcleNm") val name: String = ""   // 차량명
    )

    /* 차량 등록 요청 */
    data class AddCarRequest(
        @Json(name = "chargerId")   val managerId: String = "",  // 담당자 아이디
        @Json(name = "fuelKnd")     val fuelType: String = "",   // 연료종류
        @Json(name = "posesnStle")  val ownership: String = "",  // 소유형태
        @Json(name = "rm")          val remark: String = "",     // 비고
        @Json(name = "sttus")       val status: String = "",     // 상태 (정상, 수리, 폐차)
        @Json(name = "vhcleNm")     val name: String = "",       // 차량명
        @Json(name = "vhcleNo")     val number: String = "",     // 차량번호
        @Json(name = "vhcty")       val type: String = ""        // 차종
    )

    /* 차량 정보 상세 조회 */
    data class GetCarResponse(
        @Json(name = "chargerId")   val managerId: String = "",                             // 담당자 아이디
        @Json(name = "chargerNm")   val managerName: String = "",                           // 담당자
        @Json(name = "fuelKnd")     val fuelType: String = "",                              // 연료종류
        @Json(name = "historyList") val history: List<HistoryInfo> = emptyList(),           // 예약 현황 목록
        @Json(name = "pageInfo")    val pageInfo: PageDTO.PageInfo = PageDTO.PageInfo(),    // 페이지 정보
        @Json(name = "posesnStle")  val ownership: String = "",                             // 소유형태
        @Json(name = "rm")          val remark: String = "",                                // 비고
        @Json(name = "sttus")       val status: String = "",                                // 상태 (정상, 수리, 폐차)
        @Json(name = "vhcleId")     val id: String = "",                                    // 차량 아이디
        @Json(name = "vhcleNm")     val name: String = "",                                  // 차량명
        @Json(name = "vhcleNo")     val number: String = "",                                // 차량번호
        @Json(name = "vhcty")       val type: String = ""                                   // 차종
    )

    /* 예약 현황 목록 데이터 */
    data class HistoryInfo(
        @Json(name = "beginHour")       val startHour: String,  // 사용 시작 시
        @Json(name = "beginMnt")        val startMin: String,   // 사용 시작 분
        @Json(name = "bgnde")           val startDate: String,  // 사용 시작일시
        @Json(name = "driverNm")        val driverName: String, // 운행자
        @Json(name = "endHour")         val endHour: String,    // 사용 종료 시
        @Json(name = "endMnt")          val endMin: String,     // 사용 종료 분
        @Json(name = "endde")           val endDate: String,    // 사용 종료일시
        @Json(name = "status")          val status: String,     // 상태 (예약, 사용중, 반납)
        @Json(name = "vhcleUseHistId")  val id: String,         // 차량 사용 이력 아이디
        @Json(name = "vhcty")           val type: String,       // 차종
    )

    /* 전체 차량 예약현황/사용이력 목록 조회 및 검색 응답 */
    data class GetCarUsagesResponse(
        @Json(name = "pageInfo")    val pageInfo: PageDTO.PageInfo = PageDTO.PageInfo(),    // 페이지 정보
        @Json(name = "useHistList") val reservations: List<UsageInfo> = emptyList()         // 차량 예약현황/사용이력 목록
    )

    /* 차량 예약현황/사용이력 목록 데이터 */
    data class UsageInfo(
        @Json(name = "beginHour")       val startHour: String,      // 사용 시작 시
        @Json(name = "beginMnt")        val startMin: String,       // 사용 시작 분
        @Json(name = "bgnde")           val startDate: String,      // 사용 시작일시
        @Json(name = "deptNm")          val departmentName: String, // 부서
        @Json(name = "driverNm")        val driverName: String,     // 운행자
        @Json(name = "endHour")         val endHour: String,        // 사용 종료 시
        @Json(name = "endMnt")          val endMin: String,         // 사용 종료 분
        @Json(name = "endde")           val endDate: String,        // 사용 종료일시
        @Json(name = "vhcleNm")         val name: String,           // 차량명
        @Json(name = "vhcleNo")         val number: String,         // 차량번호
        @Json(name = "vhcleUseHistId")  val id: String,             // 차량 사용 이력 아이디
        @Json(name = "vhcty")           val type: String,           // 차종
    )
}