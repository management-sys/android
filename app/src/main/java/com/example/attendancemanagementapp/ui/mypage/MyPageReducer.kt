package com.example.attendancemanagementapp.ui.mypage

object MyPageReducer {
    fun reduce(s: MyPageState, e: MyPageEvent) = when (e) {
        is MyPageEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        else -> s
    }

    private val myPageUpdaters: Map<MyPageField, (MyPageState, String) -> MyPageState> =
        mapOf(
            MyPageField.NAME            to { s, v -> s.copy(id = v) },
            MyPageField.PHONE           to { s, v -> s.copy(phone = v) },
            MyPageField.BIRTHDATE       to { s, v -> s.copy(birthDate = v) },
            MyPageField.PASSWORD        to { s, v -> s.copy(password = v) },
            MyPageField.NEW_PASSWORD    to { s, v -> s.copy(newPassword = v) },
        )

    private fun handleChangedValue(
        state: MyPageState,
        field: MyPageField,
        value: String
    ): MyPageState {
        val updater = myPageUpdaters[field] ?: return state
        return updater(state, value)
    }
}