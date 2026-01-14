package com.example.attendancemanagementapp.ui.asset.car.detail

sealed interface CarDetailEvent {
    // 삭제 버튼 클릭 이벤트
    data object ClickedDelete: CarDetailEvent

    // 수정 버튼 클릭 이벤트
    data object ClickedUpdate: CarDetailEvent
}