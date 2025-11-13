package com.example.attendancemanagementapp.ui.login

object LoginReducer {
    fun reduce(s: LoginState, e: LoginEvent): LoginState = when (e) {
        is LoginEvent.ChangedValueWith -> handleChangedValue(s, e.field, e.value)
        else -> s
    }

    private fun handleChangedValue(
        state: LoginState,
        field: LoginField,
        value: String
    ): LoginState {
        return when (field) {
            LoginField.ID -> state.copy(id = value)
            LoginField.PASSWORD -> state.copy(password = value)
        }
    }
}