package com.example.attendancemanagementapp.ui.components.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.attendancemanagementapp.data.dto.CommonCodeDTO
import com.example.attendancemanagementapp.retrofit.param.SearchType
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.LightBlue
import com.example.attendancemanagementapp.ui.theme.LightGray
import com.example.attendancemanagementapp.ui.theme.MiddleBlue
import kotlinx.coroutines.flow.distinctUntilChanged

/* 검색바 */
@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit, onClickSearch: () -> Unit, onClickInit: () -> Unit) {
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
            onClick = { onClickInit() }
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
fun CategoryChip(selected: SearchType, name: String, onClick: () -> Unit) {
    val selected = selected.label == name

    FilterChip(
        selected = selected,
        onClick = { onClick() },
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

/* 카테고리 선택 포함 검색바 */
@Composable
fun CategorySearchBar(searchUiState: SearchUiState) {
    SearchBar(
        value = searchUiState.value,
        onValueChange = { searchUiState.onValueChange(it) },
        onClickSearch = {
            searchUiState.onClickSearch()
        },
        onClickInit = {
            searchUiState.onClickInit()
        }
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(searchUiState.categories) { category ->
            CategoryChip(
                selected = searchUiState.selectedCategory,
                name = category.label,
                onClick = { searchUiState.onClickCategory(category) }
            )
        }
    }
}

/* 공통코드 검색 디알로그 */
@Composable
fun CommonCodeDialog(
    listState: LazyListState,
    isLoading: Boolean,
    searchUiState: SearchUiState,
    commonCodes: List<CommonCodeDTO.CommonCodesInfo>,
    onDismiss: () -> Unit = {},
    onClickItem: (CommonCodeDTO.CommonCodesInfo) -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 30.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
        ) {
            CategorySearchBar(searchUiState = searchUiState)

            Spacer(Modifier.height(15.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                state = listState
            ) {
                items(commonCodes) { item ->
                    CodeInfoItem(
                        upperCodeInfo = item,
                        onClick = {
                            onClickItem(item)
                            onDismiss()
                        }
                    )
                }

                if (isLoading) {
                    item {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }

                item {
                    Spacer(Modifier.height(5.dp))
                }
            }
        }
    }
}

/* 공통코드 검색 목록 아이템 */
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
        TwoInfoBar(upperCodeInfo.upperCode ?: "-", upperCodeInfo.upperCodeName ?: "-", fontSize = 14.sp)
        TwoInfoBar(upperCodeInfo.code, upperCodeInfo.codeName, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(14.dp))
    }
}