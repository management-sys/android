package com.example.attendancemanagementapp.ui.hr.employee.edit

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.HrDTO

enum class EmployeeEditField { NAME, DEPARTMENT, GRADE, TITLE, PHONE, BIRTHDATE, HIREDATE }
enum class SalaryField { YEAR, AMOUNT }

sealed interface EmployeeEditEvent {
    data object Init: EmployeeEditEvent // 수정 데이터 가져오기 (화면 생성 시 실행)
    data class InitWith( // 수정 데이터 가져오기 (화면 생성 시 실행)
        val employeeInfo: HrDTO.EmployeeInfo,
        val departments: List<HrDTO.DepartmentsInfo>
    ): EmployeeEditEvent

    data class ChangedValue( // 직원 수정 필드 값 변경 이벤트
        val field: EmployeeEditField,
        val value: String
    ): EmployeeEditEvent

    data class ChangedSalary( // 연봉 필드 값 변경 이벤트
        val field: SalaryField,
        val value: String,
        val idx: Int
    ): EmployeeEditEvent

    data class SearchChanged( // 부서 검색어 변경 이벤트
        val value: String
    ): EmployeeEditEvent

    data class SelectDepartment( // 부서 선택 이벤트
        val departmentName: String,
        val departmentId: String
    ): EmployeeEditEvent

    data class ClickSelectAuth( // 권한 추가 확인 버튼 클릭 이벤트
        val selected: Set<AuthorDTO.GetAuthorsResponse>
    ): EmployeeEditEvent

    data class ClickDeleteSalary( // 연봉 아이템 삭제 버튼 클릭 이벤트
        val idx: Int
    ): EmployeeEditEvent

    data object ClickSearch: EmployeeEditEvent // 부서 검색 버튼 클릭 이벤트

    data object ClickInitSearch: EmployeeEditEvent // 검색어 초기화 버튼 클릭 이벤트

    data object ClickAddSalary: EmployeeEditEvent // 연봉 아이템 추가 버튼 클릭 이벤트

    data object ClickUpdate: EmployeeEditEvent // 직원 정보 수정 버튼 클릭 이벤트

    data object ClickInitBrth: EmployeeEditEvent // 생년월일 초기화 버튼 클릭 이벤트
}