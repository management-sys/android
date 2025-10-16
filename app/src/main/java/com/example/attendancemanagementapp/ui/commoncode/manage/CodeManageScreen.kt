package com.example.attendancemanagementapp.ui.commoncode.manage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.commoncode.CodeViewModel
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.CategorySearchBar
import com.example.attendancemanagementapp.ui.components.search.CodeSearchUiState
import com.example.attendancemanagementapp.ui.components.search.SearchUiState
import com.example.attendancemanagementapp.ui.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 공통코드 관리 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeManageScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val codeManageState by codeViewModel.codeManageUiState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisiblaIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisiblaIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !codeManageState.isLoading && codeManageState.currentPage < codeManageState.totalPage) {
                codeViewModel.getCodes()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            codeViewModel.initSearchState()
        }
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "공통코드 관리",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            BasicFloatingButton(onClick = {
                navController.navigate("codeAdd")
                codeViewModel.initSearchState()
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp)
        ) {
            CategorySearchBar(
                codeSearchUiState = CodeSearchUiState(
                    searchUiState = SearchUiState(
                        value = codeManageState.searchText,
                        onValueChange = { codeViewModel.onSearchTextChange(it) },
                        onClickSearch = {
                            // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                            codeViewModel.getCodes()
                            keyboardController?.hide()
                            focusManager.clearFocus(force = true)
                        },
                        onClickInit = {
                            codeViewModel.onSearchTextChange("")
                            codeViewModel.getCodes()
                        }
                    ),
                    selectedCategory = codeManageState.selectedCategory,
                    categories = SearchType.entries,
                    onClickCategory = { codeViewModel.onSearchTypeChange(it) }
                )
            )

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(codeManageState.codes) { item ->
                    CodeInfoItem(
                        upperCodeInfo = item,
                        onClick = {
                            codeViewModel.getCodeInfo(item.code)
                            navController.navigate("codeDetail")
                        }
                    )
                }

                if (codeManageState.isLoading) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
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
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(upperCodeInfo.upperCode ?: "", upperCodeInfo.upperCodeName ?: "")
        TwoInfoBar(upperCodeInfo.code, upperCodeInfo.codeName)
        Spacer(modifier = Modifier.height(14.dp))
        TwoInfoBar(upperCodeInfo.registerDate, upperCodeInfo.isUse, TextGray)
        Spacer(modifier = Modifier.height(12.dp))
    }
}