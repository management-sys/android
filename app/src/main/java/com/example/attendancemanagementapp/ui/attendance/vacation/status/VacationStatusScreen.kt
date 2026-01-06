package com.example.attendancemanagementapp.ui.attendance.vacation.status

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.ui.attendance.vacation.VacationViewModel
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.util.rememberOnce

/* 휴가 현황 화면 */
@Composable
fun VacationStatusScreen(navController: NavController, vacationViewModel: VacationViewModel) {
    val onEvent = vacationViewModel::onStatusEvent
    val vacationStatusState by vacationViewModel.vacationStatusState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "휴가 현황",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
                .padding(horizontal = 26.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(onClick = { onEvent(VacationStatusEvent.ClickedVacation(id = "VCAT_000000000000007")) })
            ) {
                Text(
                    text = "VCAT_000000000000007"
                )
            }
        }
    }
}