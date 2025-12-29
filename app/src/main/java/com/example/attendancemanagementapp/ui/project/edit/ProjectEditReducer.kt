package com.example.attendancemanagementapp.ui.project.edit

import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.project.add.ProjectAddField
import com.example.attendancemanagementapp.ui.project.add.ProjectAddSearchField

object ProjectEditReducer {
    fun reduce(s: ProjectEditState, e: ProjectEditEvent): ProjectEditState = when (e) {
        is ProjectEditEvent.InitWith -> handleInitWith(e.data)
        is ProjectEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is ProjectEditEvent.SelectedTypeWith -> handleSelectedType(s, e.type)
        is ProjectEditEvent.SelectedDepartmentWith -> handleSelectedDepartment(s, e.department)
        is ProjectEditEvent.SelectedManagerWith -> handleSelectedManager(s, e.manager)
        is ProjectEditEvent.CheckedAssignedPersonnelWith -> handleCheckedAssignedPersonnel(s, e.checked, e.employee)
        is ProjectEditEvent.SelectedPersonnelTypeWith -> handleSelectedPersonnelType(s, e.id, e.type)
        is ProjectEditEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.field, e.value)
        is ProjectEditEvent.ClickedSearchInitWith -> handleClickedSearchInit(s, e.field)
        else -> s
    }

    private fun handleInitWith(
        data: ProjectDTO.GetProjectResponse
    ): ProjectEditState {
        val newData = ProjectDTO.UpdateProjectRequest(
            businessStartDate = data.businessStartDate,
            businessEndDate = data.businessEndDate,
            departmentId = data.departmentId,
            companyName = data.companyName,
            meetingExpense = data.meetingExpense,
            planStartDate = data.planStartDate,
            planEndDate = data.planEndDate,
            assignedPersonnels = data.assignedPersonnels ?: emptyList(),
            projectName = data.projectName,
            managerId = data.managerId,
            realStartDate = data.realStartDate,
            realEndDate = data.realEndDate,
            remark = data.remark,
            type = data.type,
            businessExpense = data.businessExpense
        )
        return ProjectEditState(inputData = newData, departmentName = data.departmentName, managerName = data.managerName)
    }

    private val projectUpdaters: Map<ProjectAddField, (ProjectDTO.UpdateProjectRequest, String) -> ProjectDTO.UpdateProjectRequest> =
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
        state: ProjectEditState,
        field: ProjectAddField,
        value: String
    ): ProjectEditState {
        val updater = projectUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleSelectedType(
        state: ProjectEditState,
        type: String
    ): ProjectEditState {
        return state.copy(inputData = state.inputData.copy(type = type))
    }

    private fun handleSelectedDepartment(
        state: ProjectEditState,
        department: DepartmentDTO.DepartmentsInfo
    ): ProjectEditState {
        return state.copy(inputData = state.inputData.copy(departmentId = department.id), departmentName = department.name)
    }

    private fun handleSelectedManager(
        state: ProjectEditState,
        manager: EmployeeDTO.ManageEmployeesInfo
    ): ProjectEditState {
        return state.copy(inputData = state.inputData.copy(managerId = manager.userId), managerName = manager.name)
    }

    private fun handleCheckedAssignedPersonnel(
        state: ProjectEditState,
        checked: Boolean,
        employee: EmployeeDTO.ManageEmployeesInfo
    ): ProjectEditState {
        val newPersonnel = ProjectDTO.AssignedPersonnelInfo(managerId = employee.userId, name = employee.name, type = "선택", id = null)
        val updatedList = if (checked) state.inputData.assignedPersonnels + newPersonnel else state.inputData.assignedPersonnels - newPersonnel
        val sortedList = updatedList.sortedBy { personnel -> state.employeeState.employees.indexOfFirst { it.userId == personnel.managerId } }

        return state.copy(inputData = state.inputData.copy(assignedPersonnels = sortedList))
    }

    private fun handleSelectedPersonnelType(
        state: ProjectEditState,
        id: String,
        type: String
    ): ProjectEditState {
        val updatedList = state.inputData.assignedPersonnels.map { personnel ->
            if (personnel.managerId == id) {
                personnel.copy(type = type)
            }
            else {
                personnel
            }
        }

        return state.copy(inputData = state.inputData.copy(assignedPersonnels = updatedList))
    }

    private fun handleChangedSearchValue(
        state: ProjectEditState,
        field: ProjectAddSearchField,
        value: String
    ): ProjectEditState {
        return when (field) {
            ProjectAddSearchField.DEPARTMENT -> state.copy(departmentState = state.departmentState.copy(searchText = value, paginationState = state.departmentState.paginationState.copy(currentPage = 0)))
            ProjectAddSearchField.EMPLOYEE -> state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
        }
    }

    private fun handleClickedSearchInit(
        state: ProjectEditState,
        field: ProjectAddSearchField
    ): ProjectEditState {
        return when (field) {
            ProjectAddSearchField.DEPARTMENT -> state.copy(departmentState = state.departmentState.copy(searchText = "", paginationState = state.departmentState.paginationState.copy(currentPage = 0)))
            ProjectAddSearchField.EMPLOYEE -> state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
        }
    }
}