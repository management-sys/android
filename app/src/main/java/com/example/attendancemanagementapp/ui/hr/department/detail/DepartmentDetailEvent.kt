package com.example.attendancemanagementapp.ui.hr.department.detail

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

enum class DepartmentField { NAME, DESCRIPTION }

sealed interface DepartmentDetailEvent {
    // 직원 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): DepartmentDetailEvent

    // 직원 검색 버튼 클릭 이벤트
    data object ClickedSearch: DepartmentDetailEvent

    // 직원 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: DepartmentDetailEvent

    // 추가할 직원 목록 상태 초기화 이벤트
    data object InitAddEmployeeList: DepartmentDetailEvent

    // 부서 사용자 추가 버튼 클릭 이벤트
    data object ClickedAddEmployee: DepartmentDetailEvent

    // 추가할 직원 목록 체크박스 선택 이벤트
    data class SelectedAddEmployeeWith(
        val isChecked: Boolean,
        val user: DepartmentDTO.DepartmentUserInfo
    ): DepartmentDetailEvent

    // 추가할 직원 목록 저장 버튼 클릭 이벤트
    data object ClickedSaveAddEmployee: DepartmentDetailEvent

    // 부서장 선택 이벤트
    data class SelectedHeadWith(
        val isHead: Boolean,
        val id: String
//        val idName: Pair<String, String>
    ): DepartmentDetailEvent

    // 부서 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: DepartmentField,
        val value: String
    ): DepartmentDetailEvent

    // 저장할 직원 목록 체크박스 선택 이벤트
    data class SelectedSaveEmployeeWith(
        val isChecked: Boolean,
        val id: String
    ): DepartmentDetailEvent

    // 하위 부서 추가 버튼 클릭 이벤트
    data object ClickedAddDepartment: DepartmentDetailEvent

//    // 부서 삭제 버튼 클릭 이벤트
//    data object ClickedDeleteDepartment: DepartmentDetailEvent
//
//    // 부서 수정 버튼 클릭 이벤트
//    data object ClickedUpdateDepartment: DepartmentDetailEvent
//
//    // 부서 사용자 저장 버튼 클릭 이벤트
//    data object ClickedSaveEmployee: DepartmentDetailEvent
}