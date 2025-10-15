package com.example.attendancemanagementapp.ui.hr.employee.add

import android.util.Log
import com.example.attendancemanagementapp.data.dto.AuthorDTO
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO

object EmployeeAddReducer {
    fun reduce(s: EmployeeAddState, e: EmployeeAddEvent): EmployeeAddState = when (e) {
        EmployeeAddEvent.Init -> s
        is EmployeeAddEvent.InitWith -> handleInit(s, e.departments)
        is EmployeeAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is EmployeeAddEvent.ChangedSalaryWith -> handleChangedSalary(s, e.field, e.value, e.idx)
        is EmployeeAddEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        EmployeeAddEvent.ClickedAddSalary -> handleClickedAddSalary(s)
        is EmployeeAddEvent.ClickedDeleteSalaryWith -> handleClickedDeleteSalary(s, e.idx)
        EmployeeAddEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        is EmployeeAddEvent.SelectedDepartmentWith -> handleSelectedDepartment(s, e.departmentName, e.departmentId)
        is EmployeeAddEvent.ClickedEditAuthWith -> handleClickedEditAuth(s, e.selected)
        EmployeeAddEvent.ClickedInitBirthDate -> handleClickedInitBirthDate(s)
        EmployeeAddEvent.ClickedSearch -> s
        EmployeeAddEvent.ClickedAdd -> s
    }

    private fun handleInit(
        state: EmployeeAddState,
        departments: List<DepartmentDTO.DepartmentsInfo>
    ): EmployeeAddState {
        val data = state.copy(
            inputData = state.inputData.copy(grade = "선택", title = "선택"),
            dropDownMenu = state.dropDownMenu.copy(
                departmentMenu = state.dropDownMenu.departmentMenu + departments
            )
        )

        return data
    }

    private val employeeUpdaters: Map<EmployeeAddField, (EmployeeDTO.EmployeeInfo, String) -> EmployeeDTO.EmployeeInfo> =
        mapOf(
            EmployeeAddField.LOGINID        to { s, v -> s.copy(loginId = v) },
            EmployeeAddField.NAME           to { s, v -> s.copy(name = v) },
            EmployeeAddField.DEPARTMENT     to { s, v -> s.copy(department = v) },
            EmployeeAddField.GRADE          to { s, v -> s.copy(grade = v) },
            EmployeeAddField.TITLE          to { s, v -> s.copy(title = v) },
            EmployeeAddField.PHONE          to { s, v -> s.copy(phone = v.filter(Char::isDigit).take(11)) }, // 숫자만 입력 가능, 최대 11자로 제한
            EmployeeAddField.BIRTHDATE      to { s, v -> s.copy(birthDate = v) },
            EmployeeAddField.HIREDATE       to { s, v -> s.copy(hireDate = v) },
        )

    private fun handleChangedValue(
        state: EmployeeAddState,
        field: EmployeeAddField,
        value: String
    ): EmployeeAddState {
        val updater = employeeUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleChangedSalary(
        state: EmployeeAddState,
        field: SalaryField,
        value: String,
        idx: Int
    ): EmployeeAddState {
        val year = if (field == SalaryField.YEAR) value.filter(Char::isDigit) else state.inputData.salaries[idx].year
        val amount = if (field == SalaryField.AMOUNT) value.filter(Char::isDigit).toInt() else state.inputData.salaries[idx].amount
        val updated = state.inputData.salaries.mapIndexed { i, s -> // 수정한 인덱스의 값을 입력한 값으로 변경
            if (i == idx) s.copy(year = year, amount = amount) else s
        }

        return state.copy(inputData = state.inputData.copy(salaries = updated))
    }

    private fun handleChangedSearch(
        state: EmployeeAddState,
        value: String
    ): EmployeeAddState {
        return state.copy(searchText = value)
    }

    private fun handleClickedAddSalary(
        state: EmployeeAddState
    ): EmployeeAddState {
        return state.copy(inputData = state.inputData.copy(
            salaries = state.inputData.salaries + EmployeeDTO.SalaryInfo(null, "", 0))
        )
    }

    private fun handleClickedDeleteSalary(
        state: EmployeeAddState,
        idx: Int
    ): EmployeeAddState {
        val salaries = state.inputData.salaries
        val updated = salaries.toMutableList().apply { removeAt(idx) }

        return state.copy(inputData = state.inputData.copy(salaries = updated))
    }

    private fun handleClickedInitSearch(
        state: EmployeeAddState
    ): EmployeeAddState {
        return state.copy(
            dropDownMenu = _root_ide_package_.com.example.attendancemanagementapp.ui.hr.employee.manage.DropDownMenu(),
            searchText = ""
        )
    }

    private fun handleSelectedDepartment(
        state: EmployeeAddState,
        departmentName: String,
        departmentId: String
    ): EmployeeAddState {
        return state.copy(
            inputData = state.inputData.copy(department = departmentName),
            selectDepartmentId = departmentId
        )
    }

    private fun handleClickedEditAuth(
        state: EmployeeAddState,
        selected: Set<AuthorDTO.GetAuthorsResponse>
    ): EmployeeAddState {
        val orderSelected = state.authors.filter { it in selected }
        Log.d("권한 선택 리듀서", "${orderSelected}")
        return state.copy(
            inputData = state.inputData.copy(authors = orderSelected.map { it.name }),
            selectAuthor = orderSelected
        )
    }

    private fun handleClickedInitBirthDate(
        state: EmployeeAddState
    ): EmployeeAddState {
        return state.copy(inputData = state.inputData.copy(birthDate = ""))
    }
}