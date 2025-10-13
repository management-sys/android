package com.example.attendancemanagementapp.ui.hr.employee.edit

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.HrDTO

enum class EmployeeEditField { NAME, DEPARTMENT, GRADE, TITLE, PHONE, BIRTHDATE, HIREDATE }
enum class SalaryField { YEAR, AMOUNT }

sealed interface EmployeeEditEvent {
    // 화면 초기화
    data object Init: EmployeeEditEvent
    data class InitWith(
        val employeeInfo: HrDTO.EmployeeInfo,
        val departments: List<HrDTO.DepartmentsInfo>
    ): EmployeeEditEvent

    // 직원 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: EmployeeEditField,
        val value: String
    ): EmployeeEditEvent

    // 연봉 필드 값 변경 이벤트
    data class ChangedSalaryWith(
        val field: SalaryField,
        val value: String,
        val idx: Int
    ): EmployeeEditEvent

    // 부서 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): EmployeeEditEvent

    // 연봉 아이템 추가 버튼 클릭 이벤트
    data object ClickedAddSalary: EmployeeEditEvent

    // 연봉 아이템 삭제 버튼 클릭 이벤트
    data class ClickedDeleteSalary(
        val idx: Int
    ): EmployeeEditEvent

    // 부서 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: EmployeeEditEvent

    // 부서 선택 이벤트
    data class SelectedDepartment(
        val departmentName: String,
        val departmentId: String
    ): EmployeeEditEvent

    // 권한 수정 버튼 클릭 이벤트
    data class ClickedEditAuth(
        val selected: Set<AuthorDTO.GetAuthorsResponse>
    ): EmployeeEditEvent

    // 생년월일 초기화 버튼 클릭 이벤트
    data object ClickedInitBirthDate: EmployeeEditEvent

    // 부서 검색 버튼 클릭 이벤트
    data object ClickedSearch: EmployeeEditEvent

    // 직원 정보 수정 버튼 클릭 이벤트
    data object ClickedUpdate: EmployeeEditEvent
}