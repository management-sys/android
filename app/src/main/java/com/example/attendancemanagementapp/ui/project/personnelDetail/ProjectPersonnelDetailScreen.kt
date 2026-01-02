package com.example.attendancemanagementapp.ui.project.personnelDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.ProjectDTO
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.ui.components.TowLineInfoBar
import com.example.attendancemanagementapp.ui.home.calendar.EmptyDayBlock
import com.example.attendancemanagementapp.ui.home.calendar.WeekBar
import com.example.attendancemanagementapp.ui.project.ProjectViewModel
import com.example.attendancemanagementapp.ui.project.status.ProjectListItem
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.Schedule_Blue
import com.example.attendancemanagementapp.ui.theme.Schedule_Green
import com.example.attendancemanagementapp.ui.theme.Schedule_Yellow
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.ui.theme.TodayBlockColor
import com.example.attendancemanagementapp.ui.theme.YearMonthBtn
import com.example.attendancemanagementapp.util.rememberOnce
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/* 프로젝트 투입 현황 상세 화면 */
@Composable
fun ProjectPersonnelDetailScreen(navController: NavController, projectViewModel: ProjectViewModel) {
    val onEvent = projectViewModel::onPersonnelDetailEvent
    val projectPersonnelDetailState by projectViewModel.projectPersonnelDetailState.collectAsState()

    val tabs = listOf("사용자 정보", "프로젝트 목록", "일정 캘린더")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    if (projectPersonnelDetailState.openSheet) {
        ProjectsBottomSheet(
            projects = projectPersonnelDetailState.filteredProjects,
            onDismiss = { onEvent(ProjectPersonnelDetailEvent.ClickedCloseSheet) }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "투입 현황 상세",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = MainBlue
                    )
                }
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = pagerState.currentPage == idx,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(idx) }
                        },
                        text = { Text(title) },
                        selectedContentColor = MainBlue,
                        unselectedContentColor = Color.Black
                    )
                }
            }

            Box(Modifier.weight(1f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 26.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        when (page) {
                            0 -> {  // 사용자 정보
                                EmployeeInfoCard(
                                    personnelInfo = projectPersonnelDetailState.personnelInfo
                                )
                            }
                            1 -> {  // 프로젝트 목록
                                ProjectListCard(
                                    projects = projectPersonnelDetailState.personnelInfo.projects
                                )
                            }
                            2 -> {  // 일정 캘린더
                                ScheduleCalendarCard(
                                    projectPersonnelDetailState = projectPersonnelDetailState,
                                    onEvent = onEvent
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            onEvent(ProjectPersonnelDetailEvent.Init)
        }
    }
}

/* 사용자 정보 출력 카드 */
@Composable
private fun EmployeeInfoCard(personnelInfo: ProjectDTO.GetPersonnelDetailResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            ProfileImage()
            TowLineInfoBar(name = "아이디", value = personnelInfo.userId)
            TowLineInfoBar(name = "이름", value = personnelInfo.name)
            TowLineInfoBar(name = "부서", value = personnelInfo.departmentName)
            TowLineInfoBar(name = "직급", value = personnelInfo.grade)
            TowLineInfoBar(name = "직책", value = personnelInfo.title ?: "-")
        }
    }
}

/* 프로젝트 목록 출력 카드 */
@Composable
private fun ProjectListCard(projects: List<ProjectDTO.ProjectStatusInfo>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "참여 중인 프로젝트가 없습니다",
                        color = TextGray,
                        fontSize = 15.sp
                    )
                }
            }
            else {
                Spacer(modifier = Modifier.height(15.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(projects) { projectInfo ->
                        ProjectListItem(
                            projectInfo = projectInfo,
                            onClick = {  }
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

/* 일정 캘린더 출력 카드 */
@Composable
private fun ScheduleCalendarCard(projectPersonnelDetailState: ProjectPersonnelDetailState, onEvent: (ProjectPersonnelDetailEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            ScheduleCalendar(
                yearMonth = projectPersonnelDetailState.yearMonth,
                projects = projectPersonnelDetailState.personnelInfo.projects,
                onEvent = onEvent
            )
        }
    }
}

/* 캘린더 */
@Composable
private fun ScheduleCalendar(yearMonth: YearMonth, projects: List<ProjectDTO.ProjectStatusInfo>, onEvent: (ProjectPersonnelDetailEvent) -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val ymStr = remember(yearMonth) { yearMonth.format(formatter) }

    Column {
        YearMonthBar(
            ymStr = ymStr,
            onEvent = onEvent
        )

        Spacer(modifier = Modifier.height(15.dp))

        WeekBar()
        Month(Modifier.weight(1f), yearMonth, projects, onEvent)
    }
}

/* 년,월 출력 바 */
@Composable
private fun YearMonthBar(ymStr: String, onEvent: (ProjectPersonnelDetailEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(color = YearMonthBtn),
            onClick = { onEvent(ProjectPersonnelDetailEvent.ClickedPrev) }
        ) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "이전 월 이동 버튼", tint = Color.White)
        }

        Text(
            text = ymStr,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        IconButton(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(color = YearMonthBtn),
            onClick = { onEvent(ProjectPersonnelDetailEvent.ClickedNext) }
        ) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "이전 월 이동 버튼", tint = Color.White)
        }
    }
}

/* 한 달 출력 */
@Composable
private fun Month(modifier: Modifier, yearMonth: YearMonth, projects: List<ProjectDTO.ProjectStatusInfo>, onEvent: (ProjectPersonnelDetailEvent) -> Unit) {
    val firstDayOfWeek = yearMonth.atDay(1).dayOfWeek   // 해당 월에서 1일의 요일
    val offset = firstDayOfWeek.value % 7       // 해당 월에서 1일의 요일 인덱스화 (일=0, 월=1, ..., 토=6)
    val lastDate = yearMonth.lengthOfMonth()    // 해당 월의 총 일수
    val weeks = ((offset + lastDate + 6) / 7)   // 주수
    var date = 1 - offset

    Column(
        modifier = Modifier
            .fillMaxSize()
            .border(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
    ) {
        repeat(weeks) { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(7) { col ->
                    if (date < 1 || date > lastDate) {
                        EmptyDayBlock()
                    } else {
                        val currentDate = yearMonth.atDay(date)
                        val isToday = currentDate == LocalDate.now()
                        val filteredProject = projects
                            .filter { project ->
                                // 일정 기간에 현재 날짜가 포함되는 프로젝트만 필터링
                                val startDate = LocalDate.parse(project.businessStartDate.substring(0, 10))
                                val endDate = LocalDate.parse(project.businessEndDate.substring(0, 10))

                                !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate)
                            }
                            .sortedBy { project ->
                                // 끝 날짜 기준 내림차순 정렬 (늦은 일정이 우선순위)
                                LocalDate.parse(project.businessStartDate.substring(0, 10))
                            }

                        DayBlock(currentDate, isToday, filteredProject, onEvent)
                    }
                    date++

                    if (col < 6) {
                        VerticalDivider(thickness = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f), modifier = Modifier.fillMaxHeight())
                    }
                }
            }

            if (week < weeks - 1) {
                HorizontalDivider(thickness = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
            }
        }
    }
}

/* 날짜 블럭 */
@Composable
private fun RowScope.DayBlock(date: LocalDate, isToday: Boolean, projects: List<ProjectDTO.ProjectStatusInfo>, onEvent: (ProjectPersonnelDetailEvent) -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .background(if (isToday) TodayBlockColor else Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(true)
            ) { onEvent(ProjectPersonnelDetailEvent.ClickedDateWith(date)) },
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(end = 3.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "${date.dayOfMonth}",
                    fontSize = 10.sp,
                    color = if (date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY) ApprovalInfoItem_Red else Color.Black
                )
            }

            projects.take(3).forEachIndexed { idx, schedule ->
                DayBlockScheduleItem(currentDate = date, projectInfo = schedule)
                if (idx < 2) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Box(
                modifier = Modifier.fillMaxSize().padding(end = 2.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                // 일정 개수가 4개 이상이면 +N으로 표시
                val remain = projects.size - 3
                if (remain > 0) {
                    Text(
                        text = "+${remain}",
                        fontSize = 8.sp,
                        color = TextGray
                    )
                }
            }
        }
    }
}

/* 날짜 블럭 일정 아이템 */
@Composable
private fun DayBlockScheduleItem(currentDate: LocalDate, projectInfo: ProjectDTO.ProjectStatusInfo) {
    val startDate = LocalDate.parse(projectInfo.businessStartDate.substring(0, 10))
    val endDate = LocalDate.parse(projectInfo.businessEndDate.substring(0, 10))

    val paddingModifier = when {
        // 일정이 오늘 시작하고 오늘 끝남
        startDate == endDate -> Modifier.padding(horizontal = 2.dp)
        // 일정이 오늘 시작했지만, 오늘 끝나지 않음
        startDate == currentDate && endDate != currentDate -> Modifier.padding(start = 2.dp)
        // 일정이 이전에 시작했고, 오늘 끝남
        startDate.isBefore(currentDate) && currentDate == endDate -> Modifier.padding(end = 2.dp)
        // 일정 진행 중임
        else -> Modifier
    }

    val shape = when {
        // 일정이 오늘 시작하고 오늘 끝남
        startDate == endDate -> {
            RoundedCornerShape(CornerSize(percent = 90))
        }
        // 일정이 오늘 시작했지만, 오늘 끝나지 않음
        startDate == currentDate && endDate != currentDate -> {
            RoundedCornerShape(
                topStart = CornerSize(percent = 90),
                topEnd = CornerSize(percent = 0),
                bottomEnd = CornerSize(percent = 0),
                bottomStart = CornerSize(percent = 90)
            )
        }
        // 일정이 이전에 시작했고, 오늘 끝남
        startDate.isBefore(currentDate) && currentDate == endDate -> {
            RoundedCornerShape(
                topStart = CornerSize(percent = 0),
                topEnd = CornerSize(percent = 90),
                bottomEnd = CornerSize(percent = 90),
                bottomStart = CornerSize(percent = 0)
            )
        }
        // 일정 진행 중임
        else -> {
            RoundedCornerShape(CornerSize(percent = 0))
        }
    }

    val color = when (projectInfo.type) {
        "국가과제" -> Schedule_Blue
        "내부" -> Schedule_Green
        "용역" -> Schedule_Yellow
        else -> Color.Black
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(15.dp)
            .then(paddingModifier)
            .background(color = color, shape = shape)
            .padding(start = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (startDate == currentDate || currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
            Text(
                text = projectInfo.projectName,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 15.sp,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* 프로젝트 목록 바텀 시트 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectsBottomSheet(projects: List<ProjectDTO.ProjectStatusInfo>, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxSize(),
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        containerColor = BackgroundColor
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "프로젝트 내역이 없습니다",
                            color = TextGray,
                            fontSize = 15.sp
                        )
                    }
                }
            }
            else {
                items(projects) { project ->
                    ProjectListItem(
                        projectInfo = project,
                        onClick = {}
                    )
                }
            }

            item {
                Spacer(Modifier.height(5.dp))
            }
        }
    }
}
