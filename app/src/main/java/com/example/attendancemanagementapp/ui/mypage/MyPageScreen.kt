package com.example.attendancemanagementapp.ui.mypage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.attendancemanagementapp.data.dto.EmployeeDTO
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.BasicTextButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DateEditBar
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.InfoBar
import com.example.attendancemanagementapp.ui.components.ProfileImage
import com.example.attendancemanagementapp.util.rememberOnce

/* 마이페이지 화면 */
@Composable
fun MyPageScreen(navController: NavController, myPageViewModel: MyPageViewModel) {
    val onEvent = myPageViewModel::onEvent

    val myPageState by myPageViewModel.myPageState.collectAsState()

    LaunchedEffect(Unit) {
        myPageViewModel.init()
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "마이페이지",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 26.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MyInfoCard(
                myInfo = myPageState.myInfo,
                onEvent = onEvent
            )

            PasswordEditCard(
                myPageState = myPageState,
                onEvent = onEvent
            )

            BasicLongButton(
                name = "수정",
                onClick = { onEvent(MyPageEvent.ClickedUpdate) }
            )

            BasicTextButton(
                name = "로그아웃",
                onClick = {
                    onEvent(MyPageEvent.ClickedLogout)
                }
            )
        }
    }
}

/* 내 정보 카드 */
@Composable
private fun MyInfoCard(myInfo: EmployeeDTO.GetMyInfoResponse, onEvent: (MyPageEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage()
            Spacer(modifier = Modifier.height(30.dp))
            InfoBar(name = "아이디", value = myInfo.loginId)
            EditBar(name = "이름", value = myInfo.name, isRequired = true, onValueChange = { onEvent(
                MyPageEvent.ChangedValueWith(MyPageField.NAME, it)) })
            InfoBar(name = "부서", value = myInfo.department)
            InfoBar(name = "직급", value = myInfo.grade)
            InfoBar(name = "직책", value = myInfo.title ?: "")
            EditBar(name = "연락처", value = myInfo.phone ?: "", onValueChange = { onEvent(
                MyPageEvent.ChangedValueWith(MyPageField.PHONE, it)) })
            DateEditBar(name = "생년월일", value = myInfo.birthDate ?: "", onValueChange = { onEvent(
                MyPageEvent.ChangedValueWith(MyPageField.BIRTHDATE, it)) })
            InfoBar(name = "입사일", value = myInfo.hireDate)
        }
    }
}

/* 비밀번호 변경 카드 */
@Composable
private fun PasswordEditCard(myPageState: MyPageState, onEvent: (MyPageEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EditBar(name = "현재 비밀번호", value = myPageState.curPassword, isPassword = true, onValueChange = { onEvent(
                MyPageEvent.ChangedValueWith(MyPageField.CUR_PASSWORD, it)) })
            EditBar(name = "새 비밀번호", value = myPageState.newPassword, isPassword = true, onValueChange = { onEvent(
                MyPageEvent.ChangedValueWith(MyPageField.NEW_PASSWORD, it)) })

            Text(
                text = "※ 비밀번호는 8~50자의 영문자와 숫자, 특수기호 조합으로 작성해주세요.",
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 10.dp).padding(horizontal = 5.dp)
            )
        }
    }
}