package com.example.attendancemanagementapp.ui.project.personnel

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DepthDropDownField
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.SubButton
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.TwoLineDropdownEditBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Green
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.LightBlue
import com.example.attendancemanagementapp.ui.theme.LightGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.formatDeptGradeTitle
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.flow.distinctUntilChanged

/* 프로젝트 투입 현황 화면 */
@Composable
fun ProjectPersonnelScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onPersonnelEvent
    val projectPersonnelState by projectViewModel.projectPersonnelState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    LaunchedEffect(Unit) {
        onEvent(ProjectPersonnelEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "투입 현황",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            PersonnelList(
                projectPersonnelState = projectPersonnelState,
                onEvent = onEvent
            )
        }
    }
}

/* 투입 현황 목록 */
@Composable
private fun PersonnelList(projectPersonnelState: ProjectPersonnelState, onEvent: (ProjectPersonnelEvent) -> Unit) {
    var openSheet by remember { mutableStateOf(false) }

    if (openSheet) {
        PersonnelSearchBottomSheet(
            projectPersonnelState = projectPersonnelState,
            onEvent = onEvent,
            onDismiss = { openSheet = false }
        )
    }

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val info = listState.layoutInfo
            val lastVisibleIndex = info.visibleItemsInfo.lastOrNull()?.index ?: -1
            val total = info.totalItemsCount
            lastVisibleIndex >= total - 3 && total > 0  // 끝에서 2개 남았을 때 미리 조회
        }.distinctUntilChanged().collect { shouldLoad ->
            if (shouldLoad && !projectPersonnelState.paginationState.isLoading && projectPersonnelState.paginationState.currentPage < projectPersonnelState.paginationState.totalPage) {
                onEvent(ProjectPersonnelEvent.LoadNextPage)
            }
        }
    }

    // 부서 목록 조회될 때마다 첫번째 아이템으로 이동 (최상단으로 이동)
    LaunchedEffect(projectPersonnelState.personnels) {
        listState.scrollToItem(0)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchBar(
                    modifier = Modifier.weight(0.85f),
                    searchState = SearchState(
                        value = projectPersonnelState.filter.userName,
                        onValueChange = { onEvent(ProjectPersonnelEvent.ChangedSearchTextWith(it)) },
                        onClickSearch = {
                            if (projectPersonnelState.paginationState.currentPage <= projectPersonnelState.paginationState.totalPage) {
                                onEvent(ProjectPersonnelEvent.ClickedSearch)
                            }
                        },
                        onClickInit = { onEvent(ProjectPersonnelEvent.ClickedInitSearchText) }
                    ),
                    hint = "이름을 입력하세요"
                )

                IconButton(
                    modifier = Modifier.weight(0.15f),
                    onClick = { openSheet = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "검색 필터",
                        tint = if (projectPersonnelState.filter.year != 0 || projectPersonnelState.filter.departmentId.isNotBlank()) MainBlue else DarkGray
                    )
                }
            }

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
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    state = listState
                ) {
                    items(projectPersonnelState.personnels) { personnelInfo ->
                        PersonnelInfoItem(
                            personnelInfo = personnelInfo,
                            onClick = { onEvent(ProjectPersonnelEvent.ClickedPersonnelWith(personnelInfo.id)) }
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

/* 투입 현황 검색 필터 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PersonnelSearchBottomSheet(projectPersonnelState: ProjectPersonnelState, onEvent: (ProjectPersonnelEvent) -> Unit, onDismiss: () -> Unit) {
    var filter by remember { mutableStateOf(projectPersonnelState.filter) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = Modifier.wrapContentHeight(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = Color.White
    ) {
        val yearList = listOf("전체") + (2016..2025).map { it.toString() }

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TwoLineDropdownEditBar(
                name = "연도",
                options = yearList,
                selected = if (filter.year == 0) "전체" else filter.year.toString(),
                onSelected = { filter = filter.copy(year = it.toInt()) }
            )

            TwoLineDropdownEditBar(
                name = "부서",
                options = projectPersonnelState.departments.map { it.name },
                selected = if (filter.departmentId == "") "전체" else projectPersonnelState.departments.find { it.id == filter.departmentId }?.name ?: "",
                onSelected = { filter = filter.copy(departmentId = projectPersonnelState.departments.find { department -> department.name == it }?.id ?: "") }
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubButton(
                    name = "초기화",
                    onClick = {
                        onEvent(ProjectPersonnelEvent.ClickedInitFilter)
                        onDismiss()
                    }
                )

                BasicButton(
                    name = "적용",
                    onClick = {
                        onEvent(ProjectPersonnelEvent.ClickedUseFilter(filter = filter))
                        onDismiss()
                    }
                )
            }
        }
    }
}

/* 투입 현황 목록 아이템 */
@Composable
private fun PersonnelInfoItem(personnelInfo: ProjectDTO.PersonnelInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatDeptGradeTitle(
                    personnelInfo.departmentName,
                    personnelInfo.grade,
                    personnelInfo.name
                ),
                fontSize = 14.sp
            )

            Divider(modifier = Modifier.padding(top = 8.dp), color = LightGray)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp).height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    text = personnelInfo.notStartCnt.toString(),
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                Text(
                    text = personnelInfo.inProgressCnt.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MainBlue
                )
                VerticalDivider(modifier = Modifier.fillMaxHeight())
                Text(
                    text = personnelInfo.completeCnt.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ApprovalInfoItem_Green
                )
            }
        }
    }
}