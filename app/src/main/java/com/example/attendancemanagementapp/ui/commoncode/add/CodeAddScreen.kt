package com.example.attendancemanagementapp.ui.commoncode.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.BigEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.RadioEditBar
import com.example.attendancemanagementapp.ui.components.SearchEditBar
import com.example.attendancemanagementapp.ui.commoncode.CodeInfoField
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel

@Preview
@Composable
private fun Preview_CodeAddScreen() {
    val navController = rememberNavController()
    val codeViewModel: CodeViewModel = viewModel()
    CodeAddScreen(navController, codeViewModel)
}

/* 공통코드 등록 화면 */
@Composable
fun CodeAddScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val codeAddUiState by codeViewModel.codeAddUiState.collectAsState()

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 등록",
                onClickNavIcon = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Spacer(modifier = Modifier.height(22.dp))
                SearchEditBar(name = "상위코드", value = codeAddUiState.inputData.upperCode, onClick = { /* TODO: [이동] 상위코드 검색 화면 */} )

                EditBar(name = "상위코드명", value = codeAddUiState.inputData.upperCodeName, enabled = false)

                EditBar(name = "코드", value = codeAddUiState.inputData.code, enabled = false, isRequired = true)
                EditBar(
                    name = "코드명",
                    value = codeAddUiState.inputData.codeName,
                    onValueChange = { codeViewModel.onCodeInfoFieldChange(field = CodeInfoField.CODENAME, input = it) },
                    isRequired = true
                )
                EditBar(
                    name = "코드 설정값",
                    value = codeAddUiState.inputData.codeValue,
                    onValueChange = { codeViewModel.onCodeInfoFieldChange(field = CodeInfoField.CODEVALUE, input = it) }
                )
                RadioEditBar(name = "사용여부", selected = "사용", isRequired = true)
                BigEditBar(
                    name = "설명",
                    value = codeAddUiState.inputData.description,
                    onValueChange = { codeViewModel.onCodeInfoFieldChange(field = CodeInfoField.DESCRIPTION, input = it) }
                )
            }


            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicLongButton(name = "수정", onClick = { codeViewModel.updateCode() })
            }
        }
    }
}