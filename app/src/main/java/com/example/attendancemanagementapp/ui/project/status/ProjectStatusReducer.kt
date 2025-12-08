package com.example.attendancemanagementapp.ui.project.status

object ProjectStatusReducer {
    fun reduce(s: ProjectStatusState, e: ProjectStatusEvent): ProjectStatusState = when (e) {
        is ProjectStatusEvent.ChangedSearchTextWith -> handleChangedSearchText(s, e.value)
        ProjectStatusEvent.ClickedInitSearchText -> handleClickedInitSearchText(s)
        is ProjectStatusEvent.SelectedSearchFilterWith -> handleSelectedSearchFilter(s, e.field, e.value)
        is ProjectStatusEvent.SelectedProjectWith -> handleSelectedProject(s, e.projectId)
        else -> s
    }

    private fun handleChangedSearchText(
        state: ProjectStatusState,
        value: String
    ): ProjectStatusState {
        return state.copy(searchText = value)
    }

    private fun handleClickedInitSearchText(
        state: ProjectStatusState
    ): ProjectStatusState {
        return state.copy(searchText = "")
    }

    private val projectUpdaters: Map<ProjectStatusField, (ProjectStatusState, String) -> ProjectStatusState> =
        mapOf(
            ProjectStatusField.YEAR         to { s, v -> s.copy(selectedYear = v) },
            ProjectStatusField.MONTH        to { s, v -> s.copy(selectedMonth = v) },
            ProjectStatusField.DEPARTMENT   to { s, v -> s.copy(selectedDepartment = v) },
            ProjectStatusField.KEYWORD      to { s, v -> s.copy(selectedKeyword = v) }
        )

    private fun handleSelectedSearchFilter(
        state: ProjectStatusState,
        field: ProjectStatusField,
        value: String
    ): ProjectStatusState {
        val updater = projectUpdaters[field] ?: return state
        return updater(state, value)
    }

    private fun handleSelectedProject(
        state: ProjectStatusState,
        projectId: String
    ): ProjectStatusState {
        return state.copy(selectedProjectId = projectId)
    }
}