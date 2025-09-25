package com.example.attendancemanagementapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.attendancemanagementapp.R

@Composable
fun ProfileImage() {
    Image(
        painter = painterResource(id = R.drawable.profile),
        contentDescription = "프로필 이미지",
        modifier = Modifier.size(140.dp).clip(RoundedCornerShape(20.dp))
    )
}

@Composable
fun CalendarImage() {
    Image(
        painter = painterResource(id = R.drawable.calendar),
        contentDescription = "캘린더 이미지",
        modifier = Modifier.size(24.dp).padding(end = 8.dp)
    )
}

@Composable
fun AnnualLeaveImage() {
    Image(
        painter = painterResource(id = R.drawable.annaul_leave_calendar),
        contentDescription = "연차 잔여일수 이미지",
        modifier = Modifier.size(60.dp)
    )
}

@Composable
fun StartWorkImage() {
    Image(
        painter = painterResource(id = R.drawable.start_work),
        contentDescription = "출근 이미지",
        modifier = Modifier.size(40.dp)
    )
}

@Composable
fun FinishWorkImage() {
    Image(
        painter = painterResource(id = R.drawable.finish_work),
        contentDescription = "퇴근 이미지",
        modifier = Modifier.size(40.dp)
    )
}