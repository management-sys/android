package com.example.attendancemanagementapp.ui.hr.department.detail

import com.example.attendancemanagementapp.data.dto.DepartmentDTO

object DepartmentDetailReducer {
    fun reduce(s: DepartmentDetailState, e: DepartmentDetailEvent): DepartmentDetailState = when (e) {
        is DepartmentDetailEvent.ChangedSearchWith -> handleChangedSearch(s, e.value)
        DepartmentDetailEvent.ClickedSearch -> handleClickedInitSearch(s)
        DepartmentDetailEvent.InitAddEmployeeList -> handleInitAddEmployeeList(s)
        is DepartmentDetailEvent.SelectedAddEmployeeWith -> handleSelectedAddEmployee(s, e.isChecked, e.user)
        DepartmentDetailEvent.ClickedSaveAddEmployee -> handleClickedSaveAddEmployee(s)
        is DepartmentDetailEvent.SelectedHeadWith -> handleSelectedHead(s, e.isHead, e.id)
//        is DepartmentDetailEvent.SelectedHeadWith -> handleSelectedHead(s, e.isHead, e.idName)
        is DepartmentDetailEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is DepartmentDetailEvent.SelectedSaveEmployeeWith -> handleSelectedSaveEmployee(s, e.isChecked, e.id)
        else -> s
    }

    private fun handleChangedSearch(
        state: DepartmentDetailState,
        value: String
    ): DepartmentDetailState {
        return state.copy(searchText = value)
    }

    private fun handleClickedInitSearch(
        state: DepartmentDetailState
    ): DepartmentDetailState {
        return state.copy(searchText = "", employees = emptyList())
    }

    private fun handleInitAddEmployeeList(
        state: DepartmentDetailState
    ): DepartmentDetailState {
        return state.copy(searchText = "", selectedEmployees = emptyList())
    }

    private fun handleSelectedAddEmployee(
        state: DepartmentDetailState,
        isChecked: Boolean,
        user: DepartmentDTO.DepartmentUserInfo
    ): DepartmentDetailState {
        return state.copy(selectedEmployees = if (isChecked) state.selectedEmployees - user else state.selectedEmployees + user)
    }

    private fun handleClickedSaveAddEmployee(
        state: DepartmentDetailState
    ): DepartmentDetailState {
        return state.copy(
            searchText = "",
            users = (state.users + state.selectedEmployees),
            selectedEmployees = emptyList(),
            selectedSave = state.selectedSave + state.selectedEmployees.map { DepartmentDTO.AddUserInfo(userId = it.id) }
//            selectedSave = state.selectedEmployees.map { it.id }.toSet()
        )
    }

    private fun handleSelectedHead(
        state: DepartmentDetailState,
        isHead: Boolean,
//        idName: Pair<String, String>
        id: String
    ): DepartmentDetailState {
        return state.copy(
            users = state.users.map { user ->
                if (user.id == id) {
                    user.copy(isHead = if (isHead) "Y" else "N")
                }
                else {
                    user
                }
            },
            selectedSave = state.selectedSave.map { user ->
                if (user.userId == id) {
                    user.copy(isHead = if (isHead) "Y" else "N")
                }
                else {
                    user
                }
            }
        )
//        return state.copy(selectedHead = if (isHead) state.selectedHead.filterNot { it.first == idName.first }.toSet() else state.selectedHead + idName)
    }

    private val departmentUpdaters: Map<DepartmentField, (DepartmentDTO.DepartmentInfo, String) -> DepartmentDTO.DepartmentInfo> =
        mapOf(
            DepartmentField.NAME        to { s, v -> s.copy(name = v) },
            DepartmentField.DESCRIPTION to { s, v -> s.copy(description = v) }
        )

    private fun handleChangedValue(
        state: DepartmentDetailState,
        field: DepartmentField,
        value: String
    ): DepartmentDetailState {
        val updater = departmentUpdaters[field] ?: return state
        return state.copy(updateInfo = updater(state.updateInfo, value))
    }

    private fun handleSelectedSaveEmployee(
        state: DepartmentDetailState,
        isChecked: Boolean,
        id: String
    ): DepartmentDetailState {
        return state.copy(
            selectedSave = if (isChecked) {
                state.selectedSave.filterNot { it.userId == id }
            } else {
                state.selectedSave + DepartmentDTO.AddUserInfo(userId = id)
            }
        )
//        return state.copy(selectedSave = if (isChecked) state.selectedSave - id else state.selectedSave + id)
    }
}