package com.example.attendancemanagementapp.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.LightBlue
import com.example.attendancemanagementapp.ui.theme.LightGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.MiddleBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.view.component.BasicFloatingButton
import com.example.attendancemanagementapp.view.component.BasicTopBar
import com.example.attendancemanagementapp.viewmodel.CodeViewModel
import com.example.attendancemanagementapp.viewmodel.SearchField

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Preview_CodeManageScreen() {
    val navController = rememberNavController()
    val codeViewModel: CodeViewModel = viewModel()
    CodeManageScreen(navController, codeViewModel)
}

/* 공통코드 관리 화면 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeManageScreen(navController: NavController, codeViewModel: CodeViewModel) {
    val focusManager = LocalFocusManager.current                        // 포커스 관리
    val keyboardController = LocalSoftwareKeyboardController.current    // 키보드 관리

    val codeManageUiState by codeViewModel.codeManageUiState.collectAsState()

    val categories = listOf("전체", "상위코드", "상위코드 이름", "코드", "코드 이름")
    val upperCodeInfo = listOf("IRREGULAR_PAYMENT", "격월/부정기 지급", "TRAVEL_EXPENSE_TOTAL", "출장비", "2025-09-11", "사용중")

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
            BasicFloatingButton(onClick = { /* TODO: [이동] 공통코드 등록 화면 */ })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            SearchBar(
                value = codeManageUiState.searchText.value,
                onValueChange = { codeViewModel.onSearchFieldChange(field = SearchField.SEARCHTEXT, input = it) },
                onClickSearch = {
                    // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                    codeViewModel.getFilteredCode()
                    keyboardController?.hide()
                    focusManager.clearFocus(force = true)
                }
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        selected = codeManageUiState.selectedFilter.value,
                        name = category,
                        onClick = { codeViewModel.onSearchFieldChange(field = SearchField.FILTER, input = it) }
                    )
                }
            }

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(10) {
                    CodeInfoItem(
                        upperCodeInfo = upperCodeInfo,
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

/* 검색 창 */
@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit, onClickSearch: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier.weight(0.88f),
            value = value,
            onValueChange = { onValueChange(it) },
            singleLine = true,
            shape = RoundedCornerShape(90.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = LightGray,
                focusedContainerColor = LightGray,
                unfocusedIndicatorColor = Color.Transparent,    // 테두리 색상
                focusedIndicatorColor = Color.Transparent
            ),
            placeholder = {
                Text(
                    text = "검색어를 입력하세요",
                    fontSize = 15.sp,
                    color = DarkGray
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onClickSearch() }
            )
        )

        IconButton(
            modifier = Modifier.weight(0.12f),
            onClick = { onValueChange("") }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "검색어 초기화 버튼",
                tint = DarkGray
            )
        }
    }
}

/* 카테고리 칩 */
@Composable
private fun CategoryChip(selected: String, name: String, onClick: (String) -> Unit) {
    val selected = selected == name

    FilterChip(
        selected = selected,
        onClick = { onClick(name) },
        label = {
            Text(
                text = name,
                fontSize = 14.sp
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = LightBlue,
            labelColor = Color.Black,
            selectedLabelColor = MiddleBlue
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderWidth = 0.5.dp,
            borderColor = DarkGray,
            selectedBorderColor = MiddleBlue
        )
    )
}

/* 공통코드 목록 아이템 */
@Composable
private fun CodeInfoItem(upperCodeInfo: List<String>, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar(upperCodeInfo[0], upperCodeInfo[1])
        TwoInfoBar(upperCodeInfo[2], upperCodeInfo[3])
        Spacer(modifier = Modifier.height(14.dp))
        TwoInfoBar(upperCodeInfo[4], upperCodeInfo[5], TextGray)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

/* 정보 출력 바 */
@Composable
private fun TwoInfoBar(value1: String, value2: String, color: Color = Color.Black) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value1,
            fontSize = 16.sp,
            color = color
        )

        Text(
            text = value2,
            fontSize = 16.sp,
            color = if (value2 == "사용중") MainBlue else color
        )
    }
}