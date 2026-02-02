package com.example.attendancemanagementapp.ui.home.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.project.status.ProjectListItem
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusEvent
import com.example.attendancemanagementapp.ui.project.status.ProjectStatusState
import com.example.attendancemanagementapp.ui.theme.TextGray
import kotlinx.coroutines.flow.distinctUntilChanged

/* 프로젝트 화면 */
@Composable
fun ProjectScreen(projectViewModel: ProjectViewModel) {
    val projectState by projectViewModel.projectStatusState.collectAsState()
    val onEvent = projectViewModel::onStatusEvent

    LaunchedEffect(Unit) {
        onEvent(ProjectStatusEvent.InitFirst)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 26.dp).padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ProjectsInfo(projectState, onEvent)
    }
}

/* 프로젝트 목록 */
@Composable
fun ProjectsInfo(projectState: ProjectStatusState, onEvent: (ProjectStatusEvent) -> Unit) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !projectState.paginationState.isLoading && projectState.paginationState.currentPage < projectState.paginationState.totalPage) {
                onEvent(ProjectStatusEvent.LoadNextPage)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (projectState.projects.isEmpty()) {
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
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    state = listState
                ) {
                    items(projectState.projects) { projectInfo ->
                        ProjectListItem(
                            projectInfo = projectInfo,
                            onClick = { onEvent(ProjectStatusEvent.ClickedProjectWith(projectInfo.projectId)) }
                        )
                    }

                    item {
                        Spacer(Modifier.height(15.dp))
                    }
                }
            }
        }
    }
}