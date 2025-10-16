package com.example.attendancemanagementapp.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue

@Composable
fun BasicOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White,
    unfocusedBorderColor = DarkGray,
    focusedBorderColor = MainBlue,
    disabledContainerColor = DisableGray,
    disabledBorderColor = DarkGray,
    disabledTextColor = DarkGray,
    cursorColor = MainBlue,
)