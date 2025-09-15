package com.example.attendancemanagementapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.view.CodeAddScreen
import com.example.attendancemanagementapp.view.CodeDetailScreen
import com.example.attendancemanagementapp.view.CodeEditScreen
import com.example.attendancemanagementapp.view.CodeManageScreen
import com.example.attendancemanagementapp.viewmodel.CodeViewModel

@Composable
fun MainNavGraph(navController: NavHostController = rememberNavController()) {
    val codeViewModel: CodeViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "codeManage") {
        composable("codeManage") { CodeManageScreen(navController, codeViewModel) } // 공통코드 관리 화면
        composable("codeDetail") { CodeDetailScreen(navController, codeViewModel) } // 공통코드 상세 화면
        composable("codeEdit") { CodeEditScreen(navController, codeViewModel) }     // 공통코드 수정 화면
        composable("codeAdd") { CodeAddScreen(navController, codeViewModel) }       // 공통코드 등록 화면
    }
}