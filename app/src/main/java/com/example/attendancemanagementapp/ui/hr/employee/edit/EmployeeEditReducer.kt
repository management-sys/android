package com.example.attendancemanagementapp.ui.hr.employee.edit

import com.example.attendancemanagementapp.data.dto.HrDTO

object EmployeeEditReducer {
    fun reduce(s: EmployeeEditState, e: EmployeeEditEvent): EmployeeEditState = when (e) {
        is EmployeeEditEvent.InitWith -> handleInit(s, e.employeeInfo, e.departments)
        is EmployeeEditEvent.ChangedSalary -> s
        is EmployeeEditEvent.ChangedValue -> s
        EmployeeEditEvent.ClickAddSalary -> s
        is EmployeeEditEvent.ClickDeleteSalary -> s
        EmployeeEditEvent.ClickInitBrth -> s
        EmployeeEditEvent.ClickInitSearch -> s
        EmployeeEditEvent.ClickSearch -> s
        is EmployeeEditEvent.ClickSelectAuth -> s
        EmployeeEditEvent.ClickUpdate -> s
        EmployeeEditEvent.Init -> s
        is EmployeeEditEvent.SearchChanged -> s
        is EmployeeEditEvent.SelectDepartment -> s
    }

    private fun handleInit(
        s: EmployeeEditState,
        employeeInfo: HrDTO.EmployeeInfo,
        departments: List<HrDTO.DepartmentsInfo>
    ): EmployeeEditState {
        val data = s.copy(
            inputData = employeeInfo,
            selectAuthor = s.authors.filter { it.name in employeeInfo.authors.toHashSet() }, // 권한 이름으로 권한 코드 찾기
            selectDepartmentId = departments.firstOrNull { it.name == employeeInfo.department }?.id.orEmpty(), // 부서 이름으로 부서 아이디 찾기
            dropDownMenu = s.dropDownMenu.copy(
                departmentMenu = s.dropDownMenu.departmentMenu + departments
            )
        )

        // 직책 값이 null인 경우 초기값 설정
        val title = data.inputData.title
        return if (title.isNullOrBlank()) {
            data.copy(inputData = data.inputData.copy(title = "직책"))
        } else {
            data
        }
    }
}