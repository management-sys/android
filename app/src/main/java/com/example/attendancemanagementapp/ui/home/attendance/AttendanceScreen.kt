package com.example.attendancemanagementapp.ui.home.attendance

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.outlined.Mouse
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.R
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.components.AnnualLeaveImage
import com.example.attendancemanagementapp.ui.components.FinishWorkImage
import com.example.attendancemanagementapp.ui.components.StartWorkImage
import com.example.attendancemanagementapp.ui.theme.DarkBlue
import com.example.attendancemanagementapp.ui.theme.DeepDarkBlue
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.MidDarkBlue
import com.example.attendancemanagementapp.ui.theme.MiddleBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/* 근무체크 화면 */
@Composable
fun AttendanceScreen(navController: NavController, attendanceViewModel: AttendanceViewModel) {
    val onEvent = attendanceViewModel::onEvent
    val attendanceState by attendanceViewModel.attendanceState.collectAsState()

    LaunchedEffect(Unit) {
        onEvent(AttendanceEvent.Init)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 26.dp).padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AttendanceCheckTopBar(isWorking = attendanceState.isWorking)
            AttendanceCheck(onEvent = onEvent, isWorking = attendanceState.isWorking)
        }
        Divider()
        ProfileCard(attendanceState.myInfo)
        Divider()
        AnnualLeaveInfo(16, 16, 0)
    }
}

/* 프로필 카드 */
@Composable
fun ProfileCard(myInfo: EmployeeDTO.GetMyInfoResponse) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(vertical = 20.dp, horizontal = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "프로필 이미지",
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(90.dp))
            )

            Text(
                text = myInfo.name,
                fontSize = 15.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(color = DeepDarkBlue)
                    .padding(horizontal = 20.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = myInfo.department,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Text(
                    text = myInfo.grade,    // TODO: 웹 확인해보고 뭐 출력인지 확인 필요
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}

/* 근무체크 상단바 */
@Composable
fun AttendanceCheckTopBar(isWorking: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "근무체크",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (isWorking) "근무 중" else "출근 전",
                fontSize = 11.sp,
                color = Color.Red,
                modifier = Modifier
                    .border(width = 0.5.dp, color = Color.Red, shape = RoundedCornerShape(90.dp))
                    .clip(RoundedCornerShape(90.dp))
                    .padding(vertical = 1.dp, horizontal = 8.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = "오늘 날짜 아이콘", modifier = Modifier.size(12.dp))

            Text(
                text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                fontSize = 14.sp
            )
        }
    }
}

/* 근무체크 카드 */
@Composable
fun AttendanceCheck(onEvent: (AttendanceEvent) -> Unit, isWorking: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MiddleBlue.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row {
                TimeCard()
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
            ) {
                StartWorkButton(isWorking = isWorking, onClick = { onEvent(AttendanceEvent.ClickedStartWork) })
                VerticalDivider(thickness = 0.5.dp, color = Color.White)
                FinishWorkButton(isWorking = isWorking, onClick = { onEvent(AttendanceEvent.ClickedFinishWork) })
            }
        }
    }
}

/* 출근 버튼 */
@Composable
fun RowScope.StartWorkButton(isWorking: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        StartWorkImage()
        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = !isWorking,
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(containerColor = if (!isWorking) MainBlue else DisableGray),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(end = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mouse,
                        contentDescription = "출근 버튼",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "출근하기",
                        fontSize = 13.sp,
                        color = if (!isWorking) Color.White else TextGray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/* 퇴근 버튼 */
@Composable
fun RowScope.FinishWorkButton(isWorking: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        FinishWorkImage()
        Spacer(modifier = Modifier.width(10.dp))

        Button(
            enabled = isWorking,
            onClick = { onClick() },
            colors = ButtonDefaults.buttonColors(containerColor = if (isWorking) MainBlue else DisableGray),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.padding(end = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mouse,
                        contentDescription = "퇴근 버튼",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "퇴근하기",
                        fontSize = 13.sp,
                        color = if (isWorking) Color.White else TextGray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/* 현재 시간 출력 아이템 */
@Composable
fun RowScope.TimeCard() {
    val formatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    var now by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(System.currentTimeMillis()) {
        now = System.currentTimeMillis()
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .padding(vertical = 20.dp)
            .weight(1f),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = formatter.format(Date(now)),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/* 연차 잔여일수 카드 */
@Composable
fun AnnualLeaveInfo(leave: Int, total: Int, used: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AnnualLeaveImage()
                Spacer(modifier = Modifier.width(20.dp))
                AnnualLeaveItem(leave)
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnnualLeaveInfoItem(total, used)
            }
        }
    }
}

/* 연차 잔여일수 출력 아이템 */
@Composable
fun RowScope.AnnualLeaveItem(leave: Int) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(color = MidDarkBlue.copy(alpha = 0.05f)).weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "연차 잔여일수",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(color = MidDarkBlue)
                .padding(vertical = 5.dp)
        )
        Column(
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            Text(
                text = "$leave",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* 연차 정보 출력 아이템 */
@Composable
fun RowScope.AnnualLeaveInfoItem(total: Int, used: Int) {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(10.dp))
            .border(shape = RoundedCornerShape(10.dp), width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f))
            .padding(vertical = 15.dp)
            .weight(1f),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "전체",
                fontSize = 14.sp
            )

            Text(
                text = "$total",
                fontSize = 14.sp
            )
        }

        VerticalDivider(
            modifier = Modifier.padding(vertical = 10.dp),
            color = DividerDefaults.color.copy(alpha = 0.8f)
        )

        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "사용",
                fontSize = 14.sp
            )

            Text(
                text = "$used",
                fontSize = 14.sp,
                color = Color.Red
            )
        }
    }
}
