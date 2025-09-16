package com.example.attendancemanagementapp.ui.commoncode.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.commoncode.SearchField
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.CategoryChip
import com.example.attendancemanagementapp.ui.components.search.CategorySearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchUiState

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Preview_CodeManageScreen() {
    val navController = rememberNavController()
    val codeViewModel: CodeViewModel = viewModel()
    CodeListScreen(navController, codeViewModel)
}

/* 공통코드 관리 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeListScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val codeListUiState by codeViewModel.codeListUiState.collectAsState()

    val categories = listOf("전체", "상위코드", "상위코드 이름", "코드", "코드 이름") // 검색 카테고리 칩

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 관리",
                actIcon = Icons.Default.Menu,
                onClickNavIcon = { navController.popBackStack() },
                onClickActIcon = { /* TODO: [UI] 메뉴 아이콘 클릭 이벤트 - 드로어 열림 */ }
            )
        },
        floatingActionButton = {
            BasicFloatingButton(onClick = { navController.navigate("codeAdd") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            CategorySearchBar(
                searchUiState = SearchUiState(
                    value = codeListUiState.searchText.value,
                    onValueChange = { codeViewModel.onSearchFieldChange(field = SearchField.SEARCHTEXT, input = it) },
                    onClickSearch = {
                        // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                        codeViewModel.getFilteredCode()
                        keyboardController?.hide()
                        focusManager.clearFocus(force = true)
                    },
                    selected = codeListUiState.selectedFilter.value,
                    categories = categories,
                    onClickCategory = { codeViewModel.onSearchFieldChange(field = SearchField.FILTER, input = it) }
                )
            )

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(codeListUiState.codes) { item ->
                    CodeInfoItem(
                        upperCodeInfo = item,
                        onClick = {
                            navController.navigate("codeDetail")
                            codeViewModel.getCodeInfo()
                        }
                    )
                }
                item {
                    Spacer(Modifier.height(70.dp))
                }
            }
        }
    }
}

/* 공통코드 목록 아이템 */
@Composable
private fun CodeInfoItem(upperCodeInfo: CommonCodeDTO.CommonCodesInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(upperCodeInfo.upperCode ?: "-", upperCodeInfo.upperCodeName ?: "-")
        TwoInfoBar(upperCodeInfo.code, upperCodeInfo.codeName)
        Spacer(modifier = Modifier.height(14.dp))
        TwoInfoBar(upperCodeInfo.registerDate, upperCodeInfo.isUse, TextGray)
        Spacer(modifier = Modifier.height(12.dp))
    }
}