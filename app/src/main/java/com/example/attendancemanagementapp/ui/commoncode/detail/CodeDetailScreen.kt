package com.example.attendancemanagementapp.ui.commoncode.detail

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Preview_CodeDetailScreen() {
    val navController = rememberNavController()
    val codeViewModel: CodeViewModel = viewModel()
    CodeDetailScreen(navController, codeViewModel)
}

/* 공통코드 상세 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeDetailScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val codeDetailUiState by codeViewModel.codeDetailUiState.collectAsState()

    val infos = listOf(
        Pair("상위코드", codeDetailUiState.codeInfo.upperCode),
        Pair("상위코드명", codeDetailUiState.codeInfo.upperCodeName),
        Pair("코드", codeDetailUiState.codeInfo.code),
        Pair("코드명", codeDetailUiState.codeInfo.codeName),
        Pair("코드 설정값", codeDetailUiState.codeInfo.codeValue),
        Pair("설명", codeDetailUiState.codeInfo.description),
        Pair("사용여부", if (codeDetailUiState.codeInfo.isUse) "사용중" else "미사용")
    )

    var openDialog by remember { mutableStateOf(false) }    // 삭제 확인 디알로그 열림 상태

    if (openDialog) {
        BasicDialog(
            title = "삭제하시겠습니까?",
            onDismiss = {
                openDialog = false
            },
            onClickConfirm = {
                codeViewModel.deleteCode()
            }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 상세",
                actIcon = Icons.Default.Delete,
                actTint = Color.Red,
                onClickNavIcon = { navController.popBackStack() },
                onClickActIcon = { openDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            for (info in infos) {
                InfoBar(name = info.first, value = info.second)
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

/* 정보 출력 바 */
@Composable
private fun InfoBar(name: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.35f)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.weight(0.65f)
        )
    }
}