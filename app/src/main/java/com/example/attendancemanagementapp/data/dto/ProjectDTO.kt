package com.example.attendancemanagementapp.data.dto

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
        @Json(name = "prjctInptHnfList")    val assignedPersonnels: List<AssignedPersonnelRequestInfo>? = emptyList(), // 투입 인력 리스트
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
}