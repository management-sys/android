package com.example.attendancemanagementapp.ui.asset.car.edit

import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO

enum class CarEditField {
    TYPE, NUMBER, FUEL, OWNER, NAME, REMARK, STATUS
}

sealed interface CarEditEvent {
    // 초기화
    data class InitWith(
        val carInfo: CarDTO.GetCarResponse
    ): CarEditEvent

    // 차량정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: CarEditField,
        val value: String
    ): CarEditEvent

    // 매니저 선택 이벤트
    data class SelectedManagerWith(
        val mangerInfo: EmployeeDTO.ManageEmployeesInfo
    ): CarEditEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val value: String
    ): CarEditEvent

    // 검색 버튼 클릭 이벤트
    data object ClickedSearch: CarEditEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data object ClickedSearchInit: CarEditEvent

    // 다음 페이지 조회 이벤트
    data object LoadNextPage: CarEditEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedUpdate: CarEditEvent
}