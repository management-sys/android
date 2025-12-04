package com.example.attendancemanagementapp.ui.project.personnel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DepthDropDownField
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Green
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.util.rememberOnce

/* 프로젝트 투입 현황 화면 */
@Composable
fun ProjectPersonnelScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onPersonnelEvent
    val projectPersonnelState by projectViewModel.projectPersonnelState.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        onEvent(ProjectPersonnelEvent.Init)
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "투입 현황",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            SearchBar(
                projectPersonnelState = projectPersonnelState
            )

            if (projectPersonnelState.personnels.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "조회된 결과가 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                Spacer(Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    state = listState
                ) {
                    projectPersonnelState.personnels.forEachIndexed { idx, personnelInfo ->
                        item {
                            PersonnelInfoItem(
                                idx = idx,
                                personnelInfo = personnelInfo,
                                onClick = {}
                            )
                        }
                    }

                    item {
                        Spacer(Modifier.height(70.dp))
                    }
                }
            }
        }
    }
}

/* 검색 필터 및 검색어 바 */
@Composable
private fun SearchBar(projectPersonnelState: ProjectPersonnelState) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropDownField(  // 검색 키워드 선택 드롭다운
                modifier = Modifier.weight(1f),
                options = listOf("전체", "연도", "참여자 이름"),
                selected = projectPersonnelState.selectedKeyword,
                onSelected = {}
            )

            DepthDropDownField( // 부서 선택 드롭다운
                modifier = Modifier.weight(1f),
                options = projectPersonnelState.departments,
                selected = projectPersonnelState.selectedDepartment,
                onSelected = {}
            )
        }

        SearchBar(
            searchState = SearchState(
                value = projectPersonnelState.searchText,
                onValueChange = {},
                onClickSearch = {
                    // 검색 버튼 클릭 시 키보드 숨기기, 포커스 해제
                    if (projectPersonnelState.paginationState.currentPage <= projectPersonnelState.paginationState.totalPage) {
                        // 검색
                    }
                },
                onClickInit = {}
            ),
            hint = "이름"
        )
    }
}

/* 투입 현황 목록 아이템 */
@Composable
private fun PersonnelInfoItem(idx: Int, personnelInfo: ProjectDTO.PersonnelInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TwoInfoBar((idx + 1).toString(), formatDeptGradeTitle(personnelInfo.department, personnelInfo.grade, personnelInfo.name))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = 0.toString(),
                color = Color.Red,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = 1.toString(),
                color = MainBlue,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = 0.toString(),
                color = ApprovalInfoItem_Green,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}