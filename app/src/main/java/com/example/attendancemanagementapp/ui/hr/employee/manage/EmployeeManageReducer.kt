package com.example.attendancemanagementapp.ui.hr.employee.manage

import com.example.attendancemanagementapp.retrofit.param.PaginationState

object EmployeeManageReducer {
    fun reduce(s: EmployeeManageState, e: EmployeeManageEvent): EmployeeManageState = when (e) {
        EmployeeManageEvent.Init -> handleInit(s)
        is EmployeeManageEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        EmployeeManageEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        is EmployeeManageEvent.SelectedDropDownWith -> handleSelectedDropDown(s, e.field, e.value)
        else -> s
    }

    private fun handleInit(
        state: EmployeeManageState
    ): EmployeeManageState {
        return state.copy(
            searchText = "",
            dropDownState = DropDownState(department = "부서", grade = "직급", title = "직책"),
            paginationState = PaginationState()
        )
    }

    private fun handleChangedSearch(
        state: EmployeeManageState,
        value: String
    ): EmployeeManageState {
        return state.copy(searchText = value, paginationState = state.paginationState.copy(currentPage = 0))
    }

    private fun handleClickedInitSearch(
        state: EmployeeManageState
    ): EmployeeManageState {
        return EmployeeManageState(dropDownMenu = state.dropDownMenu)
    }

    private val dropDownUpdaters: Map<DropDownField, (EmployeeManageState, String) -> EmployeeManageState> =
        mapOf(
            DropDownField.DEPARTMENT    to { s, v -> s.copy(dropDownState = s.dropDownState.copy(department = v), paginationState = s.paginationState.copy(currentPage = 0)) },
            DropDownField.GRADE         to { s, v -> s.copy(dropDownState = s.dropDownState.copy(grade = v), paginationState = s.paginationState.copy(currentPage = 0)) },
            DropDownField.TITLE         to { s, v -> s.copy(dropDownState = s.dropDownState.copy(title = v), paginationState = s.paginationState.copy(currentPage = 0)) }
        )

    private fun handleSelectedDropDown(
        state: EmployeeManageState,
        field: DropDownField,
        value: String
    ): EmployeeManageState {
        val updater = dropDownUpdaters[field] ?: return state
        return updater(state, value)
    }
}