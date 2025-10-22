package com.example.attendancemanagementapp.ui.hr.employee.edit

import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO

object EmployeeEditReducer {
    fun reduce(s: EmployeeEditState, e: EmployeeEditEvent): EmployeeEditState = when (e) {
        is EmployeeEditEvent.InitWith -> handleInit(s, e.employeeInfo, e.departments)
        is EmployeeEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is EmployeeEditEvent.ChangedSalaryWith -> handleChangedSalary(s, e.field, e.value, e.idx)
        is EmployeeEditEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        EmployeeEditEvent.ClickedAddSalary -> handleClickedAddSalary(s)
        is EmployeeEditEvent.ClickedDeleteSalaryWith -> handleClickedDeleteSalary(s, e.idx)
        EmployeeEditEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        is EmployeeEditEvent.SelectedDepartmentWith -> handleSelectedDepartment(s, e.departmentName, e.departmentId)
        is EmployeeEditEvent.ClickedEditAuthWith -> handleClickedEditAuth(s, e.selected)
        EmployeeEditEvent.ClickedInitBirthDate -> handleClickedInitBirthDate(s)
        else -> s
    }

    private fun handleInit(
        state: EmployeeEditState,
        employeeInfo: EmployeeDTO.EmployeeInfo,
        departments: List<DepartmentDTO.DepartmentsInfo>
    ): EmployeeEditState {
        val data = state.copy(
            inputData = employeeInfo,
            selectAuthor = state.authors.filter { it.name in employeeInfo.authors.toHashSet() }, // 권한 이름으로 권한 코드 찾기
            selectDepartmentId = departments.firstOrNull { it.name == employeeInfo.department }?.id.orEmpty(), // 부서 이름으로 부서 아이디 찾기
            dropDownMenu = state.dropDownMenu.copy(
                departmentMenu = state.dropDownMenu.departmentMenu + departments
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

    private val employeeUpdaters: Map<EmployeeEditField, (EmployeeDTO.EmployeeInfo, String) -> EmployeeDTO.EmployeeInfo> =
        mapOf(
            EmployeeEditField.NAME          to { s, v -> s.copy(name = v) },
            EmployeeEditField.DEPARTMENT    to { s, v -> s.copy(department = v) },
            EmployeeEditField.GRADE         to { s, v -> s.copy(grade = v) },
            EmployeeEditField.TITLE         to { s, v -> s.copy(title = v) },
            EmployeeEditField.PHONE         to { s, v -> s.copy(phone = v.filter(Char::isDigit).take(11)) }, // 숫자만 입력 가능, 최대 11자로 제한
            EmployeeEditField.BIRTHDATE     to { s, v -> s.copy(birthDate = v) },
            EmployeeEditField.HIREDATE      to { s, v -> s.copy(hireDate = v) },
        )

    private fun handleChangedValue(
        state: EmployeeEditState,
        field: EmployeeEditField,
        value: String
    ): EmployeeEditState {
        val updater = employeeUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleChangedSalary(
        state: EmployeeEditState,
        field: SalaryField,
        value: String,
        idx: Int
    ): EmployeeEditState {
        val year = if (field == SalaryField.YEAR) value.filter(Char::isDigit) else state.inputData.salaries[idx].year
        val amount = if (field == SalaryField.AMOUNT) value.filter(Char::isDigit).toInt() else state.inputData.salaries[idx].amount
        val updated = state.inputData.salaries.mapIndexed { i, s -> // 수정한 인덱스의 값을 입력한 값으로 변경
            if (i == idx) s.copy(year = year, amount = amount) else s
        }

        return state.copy(inputData = state.inputData.copy(salaries = updated))
    }

    private fun handleChangedSearch(
        state: EmployeeEditState,
        value: String
    ): EmployeeEditState {
        return state.copy(searchText = value)
    }

    private fun handleClickedAddSalary(
        state: EmployeeEditState
    ): EmployeeEditState {
        return state.copy(inputData = state.inputData.copy(
            salaries = state.inputData.salaries + EmployeeDTO.SalaryInfo(null, "", 0))
        )
    }

    private fun handleClickedDeleteSalary(
        state: EmployeeEditState,
        idx: Int
    ): EmployeeEditState {
        val salaries = state.inputData.salaries
        val updated = salaries.toMutableList().apply { removeAt(idx) }

        return state.copy(inputData = state.inputData.copy(salaries = updated))
    }

    private fun handleClickedInitSearch(
        state: EmployeeEditState
    ): EmployeeEditState {
        return state.copy(
            dropDownMenu = _root_ide_package_.com.example.attendancemanagementapp.ui.hr.employee.manage.DropDownMenu(),
            searchText = ""
        )
    }

    private fun handleSelectedDepartment(
        state: EmployeeEditState,
        departmentName: String,
        departmentId: String
    ): EmployeeEditState {
        return state.copy(
            inputData = state.inputData.copy(department = departmentName),
            selectDepartmentId = departmentId
        )
    }

    private fun handleClickedEditAuth(
        state: EmployeeEditState,
        selected: Set<AuthorDTO.GetAuthorsResponse>
    ): EmployeeEditState {
        val orderSelected = state.authors.filter { it in selected }
        return state.copy(selectAuthor = orderSelected)
    }

    private fun handleClickedInitBirthDate(
        state: EmployeeEditState
    ): EmployeeEditState {
        return state.copy(inputData = state.inputData.copy(birthDate = ""))
    }
}