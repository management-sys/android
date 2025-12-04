package com.example.attendancemanagementapp.ui.meeting.add

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO.AddAttendeesInfo
import com.example.attendancemanagementapp.data.dto.ProjectDTO.AssignedPersonnelInfo

object MeetingAddReducer {
    fun reduce(s: MeetingAddState, e: MeetingAddEvent): MeetingAddState = when (e) {
        is MeetingAddEvent.InitWith -> handleInit(e.projectId, e.projectName, e.assignedPersonnels)
        is MeetingAddEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is MeetingAddEvent.ClickedAddExternalAttendeeWith -> handleClickedAddExternalAttendee(s, e.newAttendee)
        MeetingAddEvent.ClickedAddExpense -> handleClickedAddExpense(s)
        is MeetingAddEvent.ChangedExpenseWith -> handleChangedExpense(s, e.field, e.value, e.idx)
        is MeetingAddEvent.ClickedDeleteExpenseWith -> handleClickedDeleteExpense(s, e.idx)
        is MeetingAddEvent.CheckedAttendeeWith -> handleCheckedAttendee(s, e.checked, e.employee)
        is MeetingAddEvent.ClickedDeleteAttendeeWith -> handleCheckedDeleteAttendee(s, e.idx)
        is MeetingAddEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        MeetingAddEvent.ClickedSearchInit -> handleClickedSearchInit(s)
        else -> s
    }

    private fun handleInit(
        projectId: String,
        projectName: String,
        assignedPersonnels: List<AssignedPersonnelInfo>
    ): MeetingAddState {
        return MeetingAddState(inputData = MeetingDTO.AddMeetingRequest(projectId = projectId), projectName = projectName, assignedPersonnels = assignedPersonnels)
    }

    private val meetingUpdaters: Map<MeetingAddField, (MeetingDTO.AddMeetingRequest, String) -> MeetingDTO.AddMeetingRequest> =
        mapOf(
            MeetingAddField.TITLE   to { s, v -> s.copy(title = v) },
            MeetingAddField.START   to { s, v -> s.copy(startDate = v) },
            MeetingAddField.END     to { s, v -> s.copy(endDate = v) },
            MeetingAddField.PLACE   to { s, v -> s.copy(place = v) },
            MeetingAddField.CONTENT to { s, v -> s.copy(content = v) },
            MeetingAddField.REMARK  to { s, v -> s.copy(remark = v) },
        )

    private fun handleChangedValue(
        state: MeetingAddState,
        field: MeetingAddField,
        value: String
    ): MeetingAddState {
        val updater = meetingUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleClickedAddExternalAttendee(
        state: MeetingAddState,
        newAttendee: AddAttendeesInfo
    ): MeetingAddState {
        return state.copy(inputData = state.inputData.copy(attendees = state.inputData.attendees + newAttendee))
    }

    private fun handleClickedAddExpense(
        state: MeetingAddState
    ): MeetingAddState {
        return state.copy(inputData = state.inputData.copy(
            expenses = state.inputData.expenses + MeetingDTO.AddExpensesInfo(0, "")
        ))
    }

    private fun handleChangedExpense(
        state: MeetingAddState,
        field: ExpenseField,
        value: String,
        idx: Int
    ): MeetingAddState {
        val type = if (field == ExpenseField.TYPE) value else state.inputData.expenses[idx].type
        val amount = if (field == ExpenseField.AMOUNT) value.filter(Char::isDigit).toInt() else state.inputData.expenses[idx].amount
        val updated = state.inputData.expenses.mapIndexed { i, s -> // 수정한 인덱스의 값을 입력한 값으로 변경
            if (i == idx) s.copy(type = type, amount = amount) else s
        }

        return state.copy(inputData = state.inputData.copy(expenses = updated))
    }

    private fun handleClickedDeleteExpense(
        state: MeetingAddState,
        idx: Int
    ): MeetingAddState {
        val expenses = state.inputData.expenses
        val updated = expenses.toMutableList().apply { removeAt(idx) }

        return state.copy(inputData = state.inputData.copy(expenses = updated))
    }

    private fun handleCheckedAttendee(
        state: MeetingAddState,
        checked: Boolean,
        employee: EmployeeDTO.ManageEmployeesInfo
    ): MeetingAddState {
        val newAttendee = AddAttendeesInfo(type = "I", name = employee.name, grade = employee.grade, department = employee.department, userId = employee.userId)
        val updatedList = if (checked) state.inputData.attendees + newAttendee else state.inputData.attendees - newAttendee
        val sortedList = updatedList.sortedBy { attendee -> state.employeeState.employees.indexOfFirst { it.userId == attendee.userId } }

        return state.copy(inputData = state.inputData.copy(attendees = sortedList))
    }

    private fun handleCheckedDeleteAttendee(
        state: MeetingAddState,
        idx: Int
    ): MeetingAddState {
        val attendees = state.inputData.attendees
        val updated = attendees.toMutableList().apply { removeAt(idx) }

        return state.copy(inputData = state.inputData.copy(attendees = updated))
    }

    private fun handleChangedSearchValue(
        state: MeetingAddState,
        value: String
    ): MeetingAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value))
    }

    private fun handleClickedSearchInit(
        state: MeetingAddState
    ): MeetingAddState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }
}