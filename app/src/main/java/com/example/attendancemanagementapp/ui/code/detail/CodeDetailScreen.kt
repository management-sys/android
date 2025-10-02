package com.example.attendancemanagementapp.ui.code.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.code.CodeViewModel
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.util.rememberOnce

/* 공통코드 상세 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeDetailScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val codeDetailUiState by codeViewModel.codeDetailUiState.collectAsState()

    var openDialog by remember { mutableStateOf(false) }    // 삭제 확인 디알로그 열림 상태

    val infos = listOf(
        Pair("상위코드", codeDetailUiState.codeInfo.upperCode),
        Pair("상위코드명", codeDetailUiState.codeInfo.upperCodeName),
        Pair("코드", codeDetailUiState.codeInfo.code),
        Pair("코드명", codeDetailUiState.codeInfo.codeName),
        Pair("코드 설정값", codeDetailUiState.codeInfo.codeValue),
        Pair("설명", codeDetailUiState.codeInfo.description),
        Pair("사용여부", codeDetailUiState.codeInfo.isUse)
    )

    if (openDialog) {
        BasicDialog(
            title = "삭제하시겠습니까?",
            onDismiss = {
                openDialog = false
            },
            onClickConfirm = {
                codeViewModel.deleteCode(isSuccess = {
                    codeViewModel.initSearchState()
                    navController.popBackStack()
                })
            }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 상세",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
                onClickNavIcon = rememberOnce { navController.popBackStack() },
                onClickActIcon = { openDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp)
        ) {
            for (info in infos) {
                InfoBar(name = info.first, value = info.second ?: "")
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 90.dp),
                horizontalArrangement = Arrangement.End
            ) {
                BasicButton(name = "수정", onClick = { navController.navigate("codeEdit") })
            }
        }
    }
}