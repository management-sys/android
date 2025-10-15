package com.example.attendancemanagementapp.ui.hr.employee.manage

object EmployeeManageReducer {
    fun reduce(s: EmployeeManageState, e: EmployeeManageEvent): EmployeeManageState = when (e) {
        EmployeeManageEvent.Init -> handleInit(s)
        is EmployeeManageEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        EmployeeManageEvent.ClickedSearch -> s
        EmployeeManageEvent.ClickedInitSearch -> handleClickedInitSearch(s)
        is EmployeeManageEvent.SelectedEmployeeWith -> s
        is EmployeeManageEvent.SelectedDropDownWith -> handleSelectedDropDown(s, e.field, e.value)
    }

    private fun handleInit(
        state: EmployeeManageState
    ): EmployeeManageState {
        return EmployeeManageState(dropDownMenu = state.dropDownMenu)
    }

    private fun handleChangedSearch(
        state: EmployeeManageState,
        value: String
    ): EmployeeManageState {
        return state.copy(searchText = value, currentPage = 0)
    }

    private fun handleClickedInitSearch(
        state: EmployeeManageState
    ): EmployeeManageState {
        return EmployeeManageState(dropDownMenu = state.dropDownMenu)
    }

    private val dropDownUpdaters: Map<DropDownField, (EmployeeManageState, String) -> EmployeeManageState> =
        mapOf(
            DropDownField.DEPARTMENT    to { s, v -> s.copy(dropDownState = s.dropDownState.copy(department = v), currentPage = 0) },
            DropDownField.GRADE         to { s, v -> s.copy(dropDownState = s.dropDownState.copy(grade = v), currentPage = 0) },
            DropDownField.TITLE         to { s, v -> s.copy(dropDownState = s.dropDownState.copy(title = v), currentPage = 0) }
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