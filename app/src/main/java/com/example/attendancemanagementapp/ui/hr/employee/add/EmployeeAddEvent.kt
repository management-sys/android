package com.example.attendancemanagementapp.ui.hr.employee.add

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.DepartmentDTO

enum class EmployeeAddField { LOGINID, NAME, DEPARTMENT, GRADE, TITLE, PHONE, BIRTHDATE, HIREDATE }
enum class SalaryField { YEAR, AMOUNT }

sealed interface EmployeeAddEvent {
    // 초기화
    data object Init: EmployeeAddEvent
    data class InitWith(
        val departments: List<DepartmentDTO.DepartmentsInfo>
    ): EmployeeAddEvent

    // 직원 정보 필드 값 변경 이벤트
    data class ChangedValueWith(
        val field: EmployeeAddField,
        val value: String
    ): EmployeeAddEvent

    // 연봉 필드 값 변경 이벤트
    data class ChangedSalaryWith(
        val field: SalaryField,
        val value: String,
        val idx: Int
    ): EmployeeAddEvent

    // 부서 검색 필드 값 변경 이벤트
    data class ChangedSearchWith(
        val value: String
    ): EmployeeAddEvent

    // 연봉 아이템 추가 버튼 클릭 이벤트
    data object ClickedAddSalary: EmployeeAddEvent

    // 연봉 아이템 삭제 버튼 클릭 이벤트
    data class ClickedDeleteSalaryWith(
        val idx: Int
    ): EmployeeAddEvent

    // 부서 검색어 초기화 버튼 클릭 이벤트
    data object ClickedInitSearch: EmployeeAddEvent

    // 부서 선택 이벤트
    data class SelectedDepartmentWith(
        val departmentName: String,
        val departmentId: String
    ): EmployeeAddEvent

    // 권한 수정 버튼 클릭 이벤트
    data class ClickedEditAuthWith(
        val selected: Set<AuthorDTO.GetAuthorsResponse>
    ): EmployeeAddEvent

    // 생년월일 초기화 버튼 클릭 이벤트
    data object ClickedInitBirthDate: EmployeeAddEvent

    // 부서 검색 버튼 클릭 이벤트
    data object ClickedSearch: EmployeeAddEvent

    // 직원 정보 추가 버튼 클릭 이벤트
    data object ClickedAdd: EmployeeAddEvent
}