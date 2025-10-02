package com.example.attendancemanagementapp.ui.home.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.R
import com.example.attendancemanagementapp.ui.components.BasicBottomBar
import com.example.attendancemanagementapp.ui.components.CalendarImage
import com.example.attendancemanagementapp.ui.components.DrawerTopBar
import com.example.attendancemanagementapp.ui.home.calendar.CalendarViewModel
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Gray
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Green
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Red
import com.example.attendancemanagementapp.ui.theme.ApprovalInfoItem_Yellow
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Blue
import com.example.attendancemanagementapp.ui.theme.AttendanceInfoItem_Gray
import com.example.attendancemanagementapp.ui.theme.EmptyDayBlockColor
import com.example.attendancemanagementapp.ui.theme.TodayBlockColor
import com.example.attendancemanagementapp.ui.theme.YearMonthBtn
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/* 캘린더 화면 */
@Composable
fun CalendarScreen(navController: NavController, calendarViewModel: CalendarViewModel, openMonthInfo: Boolean, onClick: () -> Unit) {
    val calendarUiState by calendarViewModel.calendarUiState.collectAsState()
    val yearMonth = calendarUiState.yearMonth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 26.dp)
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        MonthInfo(month = yearMonth.monthValue, openMonthInfo = openMonthInfo, onClick = onClick)
        Divider()
        Calendar(
            yearMonth = yearMonth,
            onClickPrev = { calendarViewModel.onClickPrev() },
            onClickNext = { calendarViewModel.onClickNext() }
        )
    }
}

///* 일정 목록 바텀 시트 */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun SchedulesBottomSheet(schedules: List<CalendarDTO.SchedulesInfo>, onDismiss: () -> Unit) {
//    val sheetState = rememberModalBottomSheetState()
//
//    ModalBottomSheet(
//        onDismissRequest = { onDismiss() },
//        sheetState = sheetState,
//        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp, horizontal = 26.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            InfoBar(name = "아이디", value = employeeInfo.id)
//            InfoBar(name = "이름", value = employeeInfo.name)
//            InfoBar(name = "부서", value = employeeInfo.department)
//            InfoBar(name = "직급", value = employeeInfo.grade)
//            InfoBar(name = "직책", value = employeeInfo.title ?: "")
//            InfoBar(name = "연락처", value = employeeInfo.phone ?: "")
//            InfoBar(name = "생년월일", value = employeeInfo.birthDate ?: "")
//            InfoBar(name = "입사일", value = employeeInfo.hireDate)
//        }
//    }
//}

/* 캘린더 */
@Composable
fun Calendar(yearMonth: YearMonth, onClickPrev: () -> Unit, onClickNext: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM")
    val ymStr = remember(yearMonth) { yearMonth.format(formatter) }

    Card(
        colors = (CardDefaults.cardColors(containerColor = Color.White)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            YearMonthBar(
                ymStr = ymStr,
                onClickPrev = { onClickPrev() },
                onClickNext = { onClickNext() })

            Spacer(modifier = Modifier.height(15.dp))

            WeekBar()
            Month(Modifier.weight(1f), yearMonth)
        }
    }
}

/* 년,월 출력 바 */
@Composable
fun YearMonthBar(ymStr: String, onClickPrev: () -> Unit, onClickNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(color = YearMonthBtn),
            onClick = { onClickPrev() }
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
            onClick = { onClickNext() }
        ) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "이전 월 이동 버튼", tint = Color.White)
        }
    }
}

/* 요일 출력 바 */
@Composable
fun WeekBar() {
    val weeks = listOf("일", "월", "화", "수", "목", "금", "토")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(Color.White)
            .border(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weeks.forEachIndexed { idx, week ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = week,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (idx < weeks.lastIndex) {
                VerticalDivider(thickness = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
            }
        }
    }
}

/* 한 달 출력 */
@Composable
fun Month(modifier: Modifier, yearMonth: YearMonth) {
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
                    val isToday = date in 1..lastDate && YearMonth.from(LocalDate.now()) == yearMonth && LocalDate.now().dayOfMonth == date

                    if (date < 1 || date > lastDate) {
                        EmptyDayBlock()
                    } else {
                        DayBlock(date, isToday, {})
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

/* 빈 블럭 */
@Composable
fun RowScope.EmptyDayBlock() {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(color = EmptyDayBlockColor)
    )
}

/* 날짜 블럭 */
@Composable
fun RowScope.DayBlock(date: Int, isToday: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(if (isToday) TodayBlockColor else Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(true)
            ) { onClick() }
            .padding(top = 5.dp, end = 3.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Column {
            Text(
                text = "$date",
                fontSize = 13.sp
            )
        }
    }
}

/* 월 현황 */
@Composable
fun MonthInfo(month: Int, openMonthInfo: Boolean, onClick: () -> Unit) {
//    var openMonthInfo by remember { mutableStateOf(false) } // 월 현황 열림 여부
    val rotation by animateFloatAsState(targetValue = if (openMonthInfo) 90f else 0f)

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClick() }
            ) {
                CalendarImage()
                Text(
                    text = "${month}월 현황",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "월 현황 여닫기 버튼",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotation)
                )
            }

            AnimatedVisibility(
                visible = openMonthInfo,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))
                    ApprovalInfo()
                    Spacer(modifier = Modifier.height(18.dp))
                    AttendanceInfo()
                }
            }
        }
    }
}

/* 근태 관련 현황 */
@Composable
fun AttendanceInfo() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AttendanceInfoItem(count = 0, name = "지각", backColor = AttendanceInfoItem_Gray)
        AttendanceInfoItem(count = 0, name = "조기퇴근", backColor = AttendanceInfoItem_Blue)
        AttendanceInfoItem(count = 2, name = "결근", backColor = ApprovalInfoItem_Red)
    }
}

/* 근태 정보 출력 아이템 */
@Composable
fun RowScope.AttendanceInfoItem(count: Int, name: String, backColor: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color = backColor.copy(alpha = 0.05f))
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color = backColor)
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .fillMaxWidth()
        )

        Column(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = "$count",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* 결재 관련 현황 */
@Composable
fun ApprovalInfo() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(0.5.dp, DividerDefaults.color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(Color.White)
        ) {
            // TODO: 클릭 이벤트 추가 (결재만 다른 화면으로 이동, 나머지 같은 화면)
            ApprovalInfoItem(count = 0, name = "신청", backColor = ApprovalInfoItem_Yellow)
            VerticalDivider(thickness = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.4f))
            ApprovalInfoItem(count = 0, name = "대기", backColor = ApprovalInfoItem_Gray)
            VerticalDivider(thickness = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.4f))
            ApprovalInfoItem(count = 0, name = "승인", backColor = ApprovalInfoItem_Green)
            VerticalDivider(thickness = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.4f))
            ApprovalInfoItem(count = 3, name = "결재", backColor = ApprovalInfoItem_Red)
        }
    }
}

/* 결재 정보 출력 아이템 */
@Composable
fun RowScope.ApprovalInfoItem(count: Int, name: String, backColor: Color) {
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$count",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(color = backColor)
                .padding(horizontal = 12.dp, vertical = 3.dp)
        )
    }
}