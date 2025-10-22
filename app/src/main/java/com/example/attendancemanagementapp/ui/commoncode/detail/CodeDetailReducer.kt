package com.example.attendancemanagementapp.ui.commoncode.detail

object CodeDetailReducer {
    fun reduce(s: CodeDetailState, e: CodeDetailEvent): CodeDetailState = when (e) {
        CodeDetailEvent.ClickedDelete -> s
    }
}