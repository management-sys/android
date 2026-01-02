package com.example.attendancemanagementapp.ui.meeting.edit

import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO
import com.example.attendancemanagementapp.data.dto.MeetingDTO.AttendeesInfo
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.meeting.add.ExpenseField
import com.example.attendancemanagementapp.ui.meeting.add.MeetingAddField

object MeetingEditReducer {
    fun reduce(s: MeetingEditState, e: MeetingEditEvent): MeetingEditState = when (e) {
        is MeetingEditEvent.InitWith -> handleInit(e.data)
        is MeetingEditEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        is MeetingEditEvent.ClickedAddExternalAttendeeWith -> handleClickedAddExternalAttendee(s, e.newAttendee)
        MeetingEditEvent.ClickedAddExpense -> handleClickedAddExpense(s)
        is MeetingEditEvent.ChangedExpenseWith -> handleChangedExpense(s, e.field, e.value, e.idx)
        is MeetingEditEvent.ClickedDeleteExpenseWith -> handleClickedDeleteExpense(s, e.idx)
        is MeetingEditEvent.CheckedAttendeeWith -> handleCheckedAttendee(s, e.checked, e.employee)
        is MeetingEditEvent.ClickedDeleteAttendeeWith -> handleCheckedDeleteAttendee(s, e.idx)
        is MeetingEditEvent.ChangedSearchValueWith -> handleChangedSearchValue(s, e.value)
        MeetingEditEvent.ClickedInitSearch -> handleClickedSearchInit(s)
        else -> s
    }

    private fun handleInit(
        data: MeetingDTO.GetMeetingResponse
    ): MeetingEditState {
        val initData = MeetingDTO.UpdateMeetingRequest(
            startDate = data.startDate,
            endDate = data.endDate,
            attendees = data.attendees,
            content = data.content,
            expenses = data.expenses,
            place = data.place,
            remark = data.remark,
            title = data.title
        )
        return MeetingEditState(inputData = initData, projectName = data.projectName, meetingId = data.id)
//        return MeetingEditState(inputData = initData, projectId = data.id, projectName = data.projectName, meetingId = data.id)
    }

    private val meetingUpdaters: Map<MeetingAddField, (MeetingDTO.UpdateMeetingRequest, String) -> MeetingDTO.UpdateMeetingRequest> =
        mapOf(
            MeetingAddField.TITLE   to { s, v -> s.copy(title = v) },
            MeetingAddField.START   to { s, v -> s.copy(startDate = v) },
            MeetingAddField.END     to { s, v -> s.copy(endDate = v) },
            MeetingAddField.PLACE   to { s, v -> s.copy(place = v) },
            MeetingAddField.CONTENT to { s, v -> s.copy(content = v) },
            MeetingAddField.REMARK  to { s, v -> s.copy(remark = v) },
        )

    private fun handleChangedValue(
        state: MeetingEditState,
        field: MeetingAddField,
        value: String
    ): MeetingEditState {
        val updater = meetingUpdaters[field] ?: return state
        return state.copy(inputData = updater(state.inputData, value))
    }

    private fun handleClickedAddExternalAttendee(
        state: MeetingEditState,
        newAttendee: MeetingDTO.AttendeesInfo
    ): MeetingEditState {
        return state.copy(inputData = state.inputData.copy(attendees = state.inputData.attendees + newAttendee))
    }

    private fun handleClickedAddExpense(
        state: MeetingEditState
    ): MeetingEditState {
        return state.copy(inputData = state.inputData.copy(
            expenses = state.inputData.expenses + MeetingDTO.ExpensesInfo(0, null, "")
        ))
    }

    private fun handleChangedExpense(
        state: MeetingEditState,
        field: ExpenseField,
        value: String,
        idx: Int
    ): MeetingEditState {
        val type = if (field == ExpenseField.TYPE) value else state.inputData.expenses[idx].type
        val amount = if (field == ExpenseField.AMOUNT) value.filter(Char::isDigit).toInt() else state.inputData.expenses[idx].amount
        val updated = state.inputData.expenses.mapIndexed { i, s -> // 수정한 인덱스의 값을 입력한 값으로 변경
            if (i == idx) s.copy(type = type, amount = amount) else s
        }

        return state.copy(inputData = state.inputData.copy(expenses = updated))
    }

    private fun handleClickedDeleteExpense(
        state: MeetingEditState,
        idx: Int
    ): MeetingEditState {
        val expenses = state.inputData.expenses
        val updated = expenses.toMutableList().apply { removeAt(idx) }

        return state.copy(inputData = state.inputData.copy(expenses = updated))
    }

    private fun handleCheckedAttendee(
        state: MeetingEditState,
        checked: Boolean,
        employee: ProjectDTO.EmployeeInfo
    ): MeetingEditState {
        val newAttendee = AttendeesInfo(type = "I", name = employee.name, grade = employee.grade, department = employee.department, userId = employee.id, id = null)
        val updatedList = if (checked) state.inputData.attendees + newAttendee else state.inputData.attendees - newAttendee
        val sortedList = updatedList.sortedBy { attendee -> state.employeeState.employees.indexOfFirst { it.id == attendee.userId } }

        return state.copy(inputData = state.inputData.copy(attendees = sortedList))
    }

    private fun handleCheckedDeleteAttendee(
        state: MeetingEditState,
        idx: Int
    ): MeetingEditState {
        val attendees = state.inputData.attendees
        val updated = attendees.toMutableList().apply { removeAt(idx) }

        return state.copy(inputData = state.inputData.copy(attendees = updated))
    }

    private fun handleChangedSearchValue(
        state: MeetingEditState,
        value: String
    ): MeetingEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = value, paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }

    private fun handleClickedSearchInit(
        state: MeetingEditState
    ): MeetingEditState {
        return state.copy(employeeState = state.employeeState.copy(searchText = "", paginationState = state.employeeState.paginationState.copy(currentPage = 0)))
    }
}