package com.example.attendancemanagementapp.ui.base

sealed interface UiEffect {
    data object NavigateBack: UiEffect  // 뒤로 가기
    data class Navigate(val route: String): UiEffect    // 특정 화면으로 이동
    data class AllDeleteNavigate(val route: String): UiEffect   // 이전 스택 모두 지우고 특정 화면으로 이동
    data class ShowToast(val message: String): UiEffect // 토스트 출력
}