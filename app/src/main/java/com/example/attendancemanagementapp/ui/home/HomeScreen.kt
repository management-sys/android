package com.example.attendancemanagementapp.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.ui.components.BasicBottomBar
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DrawerTopBar
import com.example.attendancemanagementapp.ui.home.attendance.AttendanceScreen
import com.example.attendancemanagementapp.ui.home.attendance.AttendanceViewModel
import com.example.attendancemanagementapp.ui.home.calendar.CalendarScreen
import com.example.attendancemanagementapp.ui.home.calendar.CalendarViewModel
import com.example.attendancemanagementapp.util.rememberOnce

@Composable
fun HomeScreen(navController: NavController, calendarViewModel: CalendarViewModel, attendanceViewModel: AttendanceViewModel, onOpenDrawer: () -> Unit) {
    var selected by rememberSaveable { mutableStateOf(1) }  // 출력 화면
    var openMonthInfo by rememberSaveable { mutableStateOf(false) } // 월 현황 열림 여부

    Scaffold(
        topBar = {
            DrawerTopBar(
                onClickNavIcon = { onOpenDrawer() },
                onClickActIcon = { /* TODO: 마이페이지 화면 이동 */ }
            )
        },
        bottomBar = {
            BasicBottomBar(
                selected = selected,
                onSelected = { selected = it }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selected) {
                0 -> { AttendanceScreen(navController, attendanceViewModel) }
                1 -> { CalendarScreen(navController, calendarViewModel, openMonthInfo, onClick = { openMonthInfo = !openMonthInfo }) }
                2 -> {}
            }
        }
    }
}