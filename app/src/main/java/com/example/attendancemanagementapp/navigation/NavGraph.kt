package com.example.attendancemanagementapp.navigation

import androidx.compose.runtime.Composable
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

    NavHost(navController = navController, startDestination = "codelist") {
        composable("codelist") { CodeListScreen(navController, codeViewModel) }     // 공통코드 관리 화면
        composable("codeDetail") { CodeDetailScreen(navController, codeViewModel) } // 공통코드 상세 화면
        composable("codeEdit") { CodeEditScreen(navController, codeViewModel) }     // 공통코드 수정 화면
        composable("codeAdd") { CodeAddScreen(navController, codeViewModel) }       // 공통코드 등록 화면
    }
}