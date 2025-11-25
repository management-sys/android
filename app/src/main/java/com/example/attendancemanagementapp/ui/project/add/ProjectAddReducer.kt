package com.example.attendancemanagementapp.ui.project.add

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO

object ProjectAddReducer {
    fun reduce(s: ProjectAddState, e: ProjectAddEvent): ProjectAddState = when (e) {
        is ProjectAddEvent.Init -> ProjectAddState()
        is ProjectAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is ProjectAddEvent.SelectedTypeWith -> handleSelectedType(s, e.type)
        is ProjectAddEvent.SelectedDepartmentWith -> handleSelectedDepartment(s, e.department)
        is ProjectAddEvent.SelectedManagerWith -> handleSelectedManager(s, e.manager)
        is ProjectAddEvent.CheckedAssignedPersonnelWith -> handleCheckedAssignedPersonnel(s, e.checked, e.employee)
        else -> s
    }

    private val projectUpdaters: Map<ProjectAddField, (ProjectDTO.AddProjectRequest, String) -> ProjectDTO.AddProjectRequest> =
        mapOf(
            ProjectAddField.PROJECT_NAME        to { s, v -> s.copy(projectName = v) },
            ProjectAddField.COMPANY_NAME        to { s, v -> s.copy(companyName = v) },
            ProjectAddField.DEPARTMENT_ID       to { s, v -> s.copy(departmentId = v) },
            ProjectAddField.MANAGER_ID          to { s, v -> s.copy(managerId = v) },
            ProjectAddField.BUSINESS_EXPENSE    to { s, v -> s.copy(businessExpense = v.toInt()) },
            ProjectAddField.MEETING_EXPENSE     to { s, v -> s.copy(meetingExpense = v.toInt()) },
            ProjectAddField.BUSINESS_START      to { s, v -> s.copy(businessStartDate = v) },
            ProjectAddField.BUSINESS_END        to { s, v -> s.copy(businessEndDate = v) },
            ProjectAddField.PLAN_START          to { s, v -> s.copy(planStartDate = v) },
            ProjectAddField.PLAN_END            to { s, v -> s.copy(planEndDate = v) },
            ProjectAddField.REAL_START          to { s, v -> s.copy(realStartDate = v) },
            ProjectAddField.REAL_END            to { s, v -> s.copy(realEndDate = v) },
            ProjectAddField.REMARK              to { s, v -> s.copy(remark = v) }
        )

    private fun handleChangedValue(
        state: ProjectAddState,
        field: ProjectAddField,
        value: String
    ): ProjectAddState {
        val updater = projectUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedType(
        state: ProjectAddState,
        type: String
    ): ProjectAddState {
        return state.copy(inputData = state.inputData.copy(type = type))
    }

    private fun handleSelectedDepartment(
        state: ProjectAddState,
        department: DepartmentDTO.DepartmentsInfo
    ): ProjectAddState {
        return state.copy(inputData = state.inputData.copy(departmentId = department.id), departmentName = department.name)
    }

    private fun handleSelectedManager(
        state: ProjectAddState,
        manager: EmployeeDTO.EmployeesInfo
    ): ProjectAddState {
        return state.copy(inputData = state.inputData.copy(managerId = manager.id), managerName = manager.name)
    }

    private fun handleCheckedAssignedPersonnel(
        state: ProjectAddState,
        checked: Boolean,
        employee: EmployeeDTO.EmployeesInfo
    ): ProjectAddState {
        val updatedList = if (checked) state.checkedAssignedPersonnel + employee else state.checkedAssignedPersonnel - employee
        val sortedList = updatedList.sortedBy { employee -> state.employees.indexOfFirst { it.id == employee.id } }

        return state.copy(checkedAssignedPersonnel = sortedList)
    }
}