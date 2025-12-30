package com.example.attendancemanagementapp.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.R
import com.example.attendancemanagementapp.ui.components.BasicButton
import com.example.attendancemanagementapp.ui.components.BasicLongButton
import com.example.attendancemanagementapp.ui.components.EditBar
import com.example.attendancemanagementapp.ui.components.TwoLineEditBar

/* 로그인 화면 */
@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel) {
    val onEvent = loginViewModel::onEvent
    val loginState by loginViewModel.loginState.collectAsState()

    val focusManager = LocalFocusManager.current    // 포커스 관리

    LaunchedEffect(Unit) {
        loginViewModel.init()
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize().padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginCard(loginState, navController, onEvent)
        }
    }
}

/* 로그인 카드 */
@Composable
fun LoginCard(loginState: LoginState, navController: NavController, onEvent: (LoginEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "로고 이미지",
                modifier = Modifier.padding(bottom = 15.dp)
            )

            TwoLineEditBar(
                name = "아이디",
                value = loginState.id,
                onValueChange = { onEvent(LoginEvent.ChangedValueWith(LoginField.ID, it)) },
                hint = "아이디를 입력하세요."
            )

            TwoLineEditBar(
                name = "비밀번호",
                value = loginState.password,
                onValueChange = { onEvent(LoginEvent.ChangedValueWith(LoginField.PASSWORD, it)) },
                hint = "비밀번호를 입력하세요."
            )

            Spacer(modifier = Modifier.height(20.dp))

            BasicLongButton(
                name = "로그인",
                onClick = {
                    onEvent(LoginEvent.ClickedLogin)
                }
            )
        }
    }
}