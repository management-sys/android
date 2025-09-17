package com.example.attendancemanagementapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.commoncode.add.CodeAddScreen
import com.example.attendancemanagementapp.ui.commoncode.detail.CodeDetailScreen
import com.example.attendancemanagementapp.ui.commoncode.edit.CodeEditScreen
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.commoncode.list.CodeListScreen

@Composable
fun MainNavGraph(navController: NavHostController = rememberNavController()) {
    val codeViewModel: CodeViewModel = hiltViewModel()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        codeViewModel.snackbar.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "codeList",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("codeList") { CodeListScreen(navController, codeViewModel) }     // 공통코드 목록 화면
            composable("codeDetail") { CodeDetailScreen(navController, codeViewModel) } // 공통코드 상세 화면
            composable("codeEdit") { CodeEditScreen(navController, codeViewModel) }     // 공통코드 수정 화면
            composable("codeAdd") { CodeAddScreen(navController, codeViewModel) }       // 공통코드 등록 화면
        }
    }
}