package com.example.attendancemanagementapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.attendancemanagementapp.navigation.MainNavGraph
import com.example.attendancemanagementapp.ui.theme.AttendanceManagementAppTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendanceManagementAppTheme {
                MainNavGraph()
            }
        }
    }
}

@HiltAndroidApp
class Attendancemanagementapp : Application()