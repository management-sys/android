package com.example.attendancemanagementapp.ui.attendance.trip.add

import com.example.attendancemanagementapp.data.dto.TripDTO
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageState
import com.example.attendancemanagementapp.ui.asset.card.manage.CardManageState
import com.example.attendancemanagementapp.ui.project.add.EmployeeSearchState

data class TripAddState(
    val inputData: TripDTO.AddTripRequest = TripDTO.AddTripRequest(),   // 입력 데이터
    val employeeState: EmployeeSearchState = EmployeeSearchState(),     // 직원 검색 관련 상태 (동행자, 승인자)
    val cardState: CardManageState = CardManageState(),                 // 차량 검색 관련 상태
    val carState: CarManageState = CarManageState(),                    // 카드 검색 관련 상태
    val tripTypeNames: List<String> = emptyList(),                      // 출장 종류 이름
)