package com.example.attendancemanagementapp.data.dto

import com.example.attendancemanagementapp.data.dto.EmployeeDTO.ManageEmployeesInfo
import com.squareup.moshi.Json

object ProjectDTO {
    /* 프로젝트 상세 데이터 */
    data class ProjectInfo(
        @Json(name = "bsnsBgnde")           val businessStartDate: String,                          // 사업 시작일
        @Json(name = "bsnsEndde")           val businessEndDate: String,                            // 사업 종료일
        @Json(name = "chrgDeptNm")          val departmentName: String,                             // 담당부서명
        @Json(name = "mnnstNm")             val companyName: String?,                               // 주관기관명
        @Json(name = "mtgCt")               val meetingExpense: Int?,                               // 회의비
        @Json(name = "planBgnde")           val planStartDate: String?,                             // 계획 시작일
        @Json(name = "planEndde")           val planEndDate: String?,                               // 계획 종료일
        @Json(name = "prjctId")             val projectId: String,                                  // 프로젝트 아이디
        @Json(name = "prjctInptHnfList")    val assignedPersonnels: List<AssignedPersonnelInfo>?,   // 투입 인력 리스트
        @Json(name = "prjctNm")             val projectName: String,                                // 프로젝트명
        @Json(name = "prjctRspnberNm")      val managerName: String,                                // 프로젝트 책임자 이름
        @Json(name = "progrsSttus")         val status: String,                                     // 진행상태
        @Json(name = "realBgnde")           val realStartDate: String?,                             // 실제 시작일
        @Json(name = "realEndde")           val realEndDate: String?,                               // 실제 종료일
        @Json(name = "rm")                  val remark: String?,                                    // 비고
        @Json(name = "se")                  val type: String,                                       // 구분 (용역/내부/국가과제)
        @Json(name = "wct")                 val businessExpense: Int?                               // 사업비
    )

    /* 투입 인력 조회 목록 데이터 */
    data class AssignedPersonnelInfo(
        @Json(name = "chrgNm")  val name: String,   // 담당자 이름
        @Json(name = "chrgSe")  val type: String    // 담당 구분
    )

    /* 프로젝트 등록 요청 */
    data class AddProjectRequest(
        @Json(name = "bsnsBgnde")           val businessStartDate: String = "",                                 // 사업 시작일
        @Json(name = "bsnsEndde")           val businessEndDate: String = "",                                   // 사업 종료일
        @Json(name = "chrgDeptId")          val departmentId: String = "",                                      // 담당부서 아이디
        @Json(name = "mnnstNm")             val companyName: String? = "",                                      // 주관기관명
        @Json(name = "mtgCt")               val meetingExpense: Int? = 0,                                       // 회의비
        @Json(name = "planBgnde")           val planStartDate: String? = "",                                    // 계획 시작일
        @Json(name = "planEndde")           val planEndDate: String? = "",                                      // 계획 종료일
        @Json(name = "prjctInptHnfList")    val assignedPersonnels: List<AssignedPersonnelRequestInfo> = emptyList(), // 투입 인력 리스트
        @Json(name = "prjctNm")             val projectName: String = "",                                       // 프로젝트명
        @Json(name = "prjctRspnberId")      val managerId: String = "",                                         // 프로젝트 책임자 아이디
        @Json(name = "realBgnde")           val realStartDate: String? = "",                                    // 실제 시작일
        @Json(name = "realEndde")           val realEndDate: String? = "",                                      // 실제 종료일
        @Json(name = "rm")                  val remark: String? = "",                                           // 비고
        @Json(name = "se")                  val type: String = "선택",                                           // 구분 (용역/내부/국가과제)
        @Json(name = "wct")                 val businessExpense: Int? = 0                                       // 사업비
    )

    /* 투입 인력 요청 목록 데이터 */
    data class AssignedPersonnelRequestInfo(
        @Json(name = "chargerId")   val chargerId: String,  // 담당자 아이디
        @Json(name = "chrgSe")      val type: String        // 담당 구분
    )

    /* 프로젝트 상세 조회 응답 */
    data class GetProjectResponse(
        @Json(name = "bsnsBgnde")           val businessStartDate: String = "",                                 // 사업 시작일
        @Json(name = "bsnsEndde")           val businessEndDate: String = "",                                   // 사업 종료일
        @Json(name = "chrgDeptNm")          val departmentName: String = "",                                    // 담당부서명
        @Json(name = "mnnstNm")             val companyName: String? = "",                                      // 주관기관명
        @Json(name = "mtgCt")               val meetingExpense: Int? = 0,                                       // 회의비
        @Json(name = "mtgs")                val meetings: List<MeetingsInfo> = emptyList(),                     // 회의록 리스트
        @Json(name = "planBgnde")           val planStartDate: String? = "",                                    // 계획 시작일
        @Json(name = "planEndde")           val planEndDate: String? = "",                                      // 계획 종료일
        @Json(name = "prjctId")             val projectId: String = "",                                         // 프로젝트 아이디
        @Json(name = "prjctInptHnfList")    val assignedPersonnels: List<AssignedPersonnelInfo>? = emptyList(), // 투입 인력 리스트
        @Json(name = "prjctNm")             val projectName: String = "",                                       // 프로젝트명
        @Json(name = "prjctRspnberNm")      val managerName: String = "",                                       // 프로젝트 책임자 이름
        @Json(name = "progrsSttus")         val status: String = "",                                            // 진행상태
        @Json(name = "realBgnde")           val realStartDate: String? = "",                                    // 실제 시작일
        @Json(name = "realEndde")           val realEndDate: String? = "",                                      // 실제 종료일
        @Json(name = "rm")                  val remark: String? = "",                                           // 비고
        @Json(name = "se")                  val type: String = "",                                              // 구분 (용역/내부/국가과제)
        @Json(name = "totalPrjctMtgCost")   val totalMeetingExpense: Int = 0,                                   // 회의비 총 사용 금액
        @Json(name = "wct")                 val businessExpense: Int? = 0                                       // 사업비
    )

    /* 회의록 목록 데이터 */
    data class MeetingsInfo(
        @Json(name = "bgnde")           val startDate: String,  // 시작일
        @Json(name = "endde")           val endDate: String,    // 종료일
        @Json(name = "mtgAtdrn")        val attendee: String,   // 참석자
        @Json(name = "mtgRcordId")      val id: Long,           // 회의록 아이디
        @Json(name = "sj")              val title: String,      // 회의록 제목
        @Json(name = "totalMtgCost")    val expense: Int,       // 회의비
    )

    /* 프로젝트 투입 인력 목록 조회 응답 */
    data class GetPersonnelResponse(
        @Json(name = "content")     val content: List<PersonnelInfo>,   // 투입 인력 목록
        @Json(name = "totalPages")  val totalPages: Int                 // 총 페이지 개수
    )

    /* 투입 인력 목록 데이터 */
    data class PersonnelInfo(
        @Json(name = "clsf")    val grade: String,      // 직책
        @Json(name = "deptNm")  val department: String, // 부서명
        @Json(name = "userId")  val id: String,         // 사용자 ID
        @Json(name = "userNm")  val name: String        // 사용자명
    )
}