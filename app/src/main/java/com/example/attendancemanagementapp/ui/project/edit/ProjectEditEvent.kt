package com.example.attendancemanagementapp.ui.project.edit

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.project.add.ProjectAddField
import com.example.attendancemanagementapp.ui.project.add.ProjectAddSearchField

sealed interface ProjectEditEvent {
    // 초기화
    data object Init: ProjectEditEvent
    data class InitWith(
        val data: ProjectDTO.GetProjectResponse
    ): ProjectEditEvent

    // 프로젝트 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: ProjectAddField,
        val value: String
    ): ProjectEditEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedUpdate: ProjectEditEvent

    // 구분 선택 이벤트
    data class SelectedTypeWith(
        val type: String
    ): ProjectEditEvent

    // 담당부서 선택 이벤트
    data class SelectedDepartmentWith(
        val department: DepartmentDTO.DepartmentsInfo
    ): ProjectEditEvent

    // 프로젝트 책임자 선택 이벤트
    data class SelectedManagerWith(
        val manager: EmployeeDTO.ManageEmployeesInfo
    ): ProjectEditEvent

    // 투입인력 체크박스 클릭 이벤트
    data class CheckedAssignedPersonnelWith(
        val checked: Boolean,
        val employee: EmployeeDTO.ManageEmployeesInfo
    ): ProjectEditEvent

    // 투입인력 구분 선택 이벤트
    data class SelectedPersonnelTypeWith(
        val id: String,
        val type: String
    ): ProjectEditEvent

    // 검색어 필드 값 변경 이벤트
    data class ChangedSearchValueWith(
        val field: ProjectAddSearchField,
        val value: String
    ): ProjectEditEvent

    // 검색 버튼 클릭 이벤트
    data class ClickedSearchWith(
        val field: ProjectAddSearchField
    ): ProjectEditEvent

    // 검색어 초기화 버튼 클릭 이벤트
    data class ClickedSearchInitWith(
        val field: ProjectAddSearchField
    ): ProjectEditEvent

    // 다음 페이지 조회 이벤트
    data class LoadNextPage(
        val field: ProjectAddSearchField
    ): ProjectEditEvent
}