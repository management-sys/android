package com.example.attendancemanagementapp.ui.asset.car.add

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.asset.car.edit.CarEditField

sealed interface CarAddEvent {
    // 초기화
    data class InitWith(
        val carInfo: CarDTO.GetCarResponse
    ): CarAddEvent

    // 차량정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: CarEditField,
        val value: String
    ): CarAddEvent

    // 매니저 선택 이벤트
    data class SelectedManagerWith(
        val mangerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CarAddEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val value: String
    ): CarAddEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: CarAddEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedSearchInit: CarAddEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: CarAddEvent

    // 등록 버튼 클릭 이벤트
    data object ClickedAdd: CarAddEvent
}