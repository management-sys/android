package com.example.attendancemanagementapp

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.attendancemanagementapp.data.datastore.TokenDataStore
import com.example.attendancemanagementapp.navigation.MainNavGraph
import com.example.attendancemanagementapp.ui.theme.AttendanceManagementAppTheme
import com.example.attendancemanagementapp.ui.theme.FixedFontScaleTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var tokenDataStore: TokenDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FixedFontScaleTheme {
                AttendanceManagementAppTheme {
                    MainNavGraph(tokenDataStore = tokenDataStore)
                }
            }
        }
    }
}

@HiltAndroidApp
class Attendancemanagementapp : Application()