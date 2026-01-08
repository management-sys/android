package com.example.attendancemanagementapp.data.dto

import com.squareup.moshi.Json

object VacationDTO {
    /* 휴가 신청 요청 */
    data class AddVacationRequest(
        @Json(name = "bgnde")       val startDate: String = "",                     // 시작일
        @Json(name = "confmerIds")  val approverIds: List<String> = emptyList(),    // 승인자 아이디 목록 (승인 순서대로)
        @Json(name = "detailCn")    val detail: String = "",                        // 세부사항
        @Json(name = "endde")       val endDate: String = "",                       // 휴가 종료일
        @Json(name = "userId")      val userId: String = "",                        // 사용자 아이디
        @Json(name = "vcatnKnd")    val type: String = ""                           // 휴가 종류 (연차/반차 등)
    )

    /* 휴가 신청 상세 조회 응답 */
    data class GetVacationResponse(
        @Json(name = "beginHour")   val startHour: String = "",         // 휴가 시작 시
        @Json(name = "beginMnt")    val startMinute: String = "",       // 휴가 시작 분
        @Json(name = "bgnde")       val startDate: String = "",         // 휴가 시작일시
        @Json(name = "clsf")        val grade: String = "",             // 직급
        @Json(name = "confmAt")     val status: String = "",            // 상태
        @Json(name = "confmerId")   val approverId: String = "",        // 승인자 아이디
        @Json(name = "confmerNm")   val approverName: String = "",      // 승인자 이름
        @Json(name = "deptNm")      val departmentName: String = "",    // 부서명
        @Json(name = "detailCn")    val detail: String = "",            // 세부사항
        @Json(name = "endHour")     val endHour: String = "",           // 휴가 종료 시
        @Json(name = "endMnt")      val endMinute: String = "",         // 휴가 종료 분
        @Json(name = "endde")       val endDate: String = "",           // 휴가 종료일시
        @Json(name = "period")      val period: String = "",            // 휴가 기간
        @Json(name = "returnResn")  val rejection: String? = "",        // 반려사유
        @Json(name = "rgsde")       val appliedDate: String = "",       // 신청일
        @Json(name = "useAt")       val useAt: String = "",             // 사용 여부 (Y/N)
        @Json(name = "userId")      val userId: String = "",            // 사용자 아이디
        @Json(name = "userNm")      val userName: String = "",          // 이름
        @Json(name = "vcatnId")     val id: String = "",                // 휴가 아이디
        @Json(name = "vcatnKnd")    val type: String = ""               // 휴가 종류
    )

    /* 이전 승인자 불러오기 응답 */
    data class GetPrevApproversResponse(
        @Json(name = "confmers")    val approvers: List<ApproversInfo>  // 가장 최근 휴가 신청서의 승인자 목록 (아이디, 이름)
    )

    /* 승인자 목록 데이터 */
    data class ApproversInfo(
        @Json(name = "name")    val name: String,   // 승인자 이름
        @Json(name = "userId")  val userId: String, // 승인자 아이디
    )

    /* 휴가 현황 목록 조회 응답 */
    data class GetVacationsResponse(
        @Json(name = "minusCo")     val minusCarryover: String = "",                        // 마이너스 이월 수
        @Json(name = "officalCo")   val officalCarryover: String = "",                      // 공가 수
        @Json(name = "pageInfo")    val pageInfo: PageDTO.PageInfo = PageDTO.PageInfo(),    // 페이지 정보
        @Json(name = "sickCo")      val sickCarryover: String = "",                         // 병가 수
        @Json(name = "specialCo")   val specialCarryover: String = "",                      // 특별 휴가 수
        @Json(name = "totalCo")     val totalCarryover: String = "",                        // 전체 휴가 수
        @Json(name = "unusedCo")    val unusedCarryover: String = "",                       // 미사용 휴가 수
        @Json(name = "usedCo")      val usedCarryover: String = "",                         // 사용 휴가 수
        @Json(name = "vcatnList")   val vacations: List<VacationsInfo> = emptyList(),       // 휴가 목록
        @Json(name = "yrycList")    val years: List<YearsInfo> = emptyList()                // 연차 목록
    )

    /* 휴가 목록 데이터 */
    data class VacationsInfo(
        @Json(name = "bgnde")       val startDate: String = "",     // 휴가 시작일시
        @Json(name = "confmAt")     val status: String = "",        // 상태
        @Json(name = "detailCn")    val detail: String = "",        // 세부사항
        @Json(name = "endde")       val endDate: String = "",       // 휴가 종료일시
        @Json(name = "period")      val period: String = "",        // 휴가 기간
        @Json(name = "rgsde")       val appliedDate: String = "",   // 신청일
        @Json(name = "vcatnId")     val id: String = "",            // 휴가 아이디
        @Json(name = "vcatnKnd")    val type: String = ""           // 휴가 종류
    )

    /* 연차 목록 데이터 */
    data class YearsInfo(
        @Json(name = "bgnde")   val startDate: String = "", // 연차 시작일
        @Json(name = "endde")   val endDate: String = "",   // 연차 종료일
        @Json(name = "yryc")    val year: Int = 0,          // 연차
        @Json(name = "yrycId")  val id: Int = 0             // 연차 아이디
    )
}