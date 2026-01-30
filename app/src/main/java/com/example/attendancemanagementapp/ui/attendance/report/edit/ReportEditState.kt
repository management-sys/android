package com.example.attendancemanagementapp.ui.attendance.report.edit

import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageState
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class ReportEditState(
    val inputData: TripDTO.UpdateTripReportRequest = TripDTO.UpdateTripReportRequest(), // 입력 데이터
    val tripInfo: TripDTO.GetTripResponse = TripDTO.GetTripResponse(),                  // 출장 정보
    val employeeState: EmployeeSearchState = EmployeeSearchState(),                     // 직원 검색 관련 상태 (승인자, 결제자)
    val cardState: CardManageState = CardManageState(),                                 // 차량 검색 관련 상태
)