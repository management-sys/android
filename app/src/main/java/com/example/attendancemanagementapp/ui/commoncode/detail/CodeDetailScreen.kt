package com.example.attendancemanagementapp.ui.commoncode.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.util.rememberOnce

/* 공통코드 상세 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeDetailScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val onEvent = codeViewModel::onDetailEvent
    val codeDetailState by codeViewModel.codeDetailState.collectAsState()

    var openDialog by remember { mutableStateOf(false) }    // 삭제 확인 디알로그 열림 상태

    val infos = listOf(
        Pair("상위코드", codeDetailState.codeInfo.upperCode),
        Pair("상위코드명", codeDetailState.codeInfo.upperCodeName),
        Pair("코드", codeDetailState.codeInfo.code),
        Pair("코드명", codeDetailState.codeInfo.codeName),
        Pair("코드 설정값", codeDetailState.codeInfo.codeValue),
        Pair("설명", codeDetailState.codeInfo.description),
        Pair("사용여부", codeDetailState.codeInfo.isUse)
    )

    if (openDialog) {
        BasicDialog(
            title = "삭제하시겠습니까?",
            onDismiss = {
                openDialog = false
            },
            onClickConfirm = {
                onEvent(CodeDetailEvent.ClickedDelete)
                openDialog = false
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
            CodeInfoCard(
                infos = infos,
                onClick = { navController.navigate("codeEdit") }
            )
        }
    }
}

/* 공통코드 정보 카드 */
@Composable
private fun CodeInfoCard(infos: List<Pair<String, String?>>, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (info in infos) {
                InfoBar(name = info.first, value = info.second ?: "")
                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 90.dp),
                horizontalArrangement = Arrangement.End
            ) {
                BasicButton(name = "수정", onClick = { onClick() })
            }
        }
    }
}