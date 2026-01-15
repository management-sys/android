package com.example.attendancemanagementapp.ui.asset.card.manage

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.CarDTO
import com.example.attendancemanagementapp.data.dto.CardDTO
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageEvent
import com.example.attendancemanagementapp.ui.asset.car.manage.CarManageState
import com.example.attendancemanagementapp.ui.asset.card.CardViewModel
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce

/* 카드정보 관리 화면 */
@Composable
fun CardManageScreen(navController: NavController, cardViewModel: CardViewModel) {
    val focusManager = LocalFocusManager.current    // 포커스 관리

    val onEvent = cardViewModel::onManageEvent
    val cardManageState by cardViewModel.cardManageState.collectAsState()

    LaunchedEffect(Unit) {
        onEvent(CardManageEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "카드정보 관리",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            BasicFloatingButton(
                onClick = { navController.navigate("cardAdd") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropDownField(
                modifier = Modifier.fillMaxWidth(),
                options = listOf("전체", "카드명"),
                selected = cardManageState.type,
                onSelected = { onEvent(CardManageEvent.SelectedTypeWith(it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SearchBar(
                searchState = SearchState(
                    value = cardManageState.searchText,
                    onValueChange = { onEvent(CardManageEvent.ChangedSearchTextWith(it)) },
                    onClickSearch = { onEvent(CardManageEvent.ClickedSearch) },
                    onClickInit = { onEvent(CardManageEvent.ClickedInitSearchText) }
                )
            )

            Spacer(Modifier.height(15.dp))

            CardListCard(
                cardManageState = cardManageState,
                onEvent = onEvent
            )
        }
    }
}

/* 카드정보 목록 출력 카드 */
@Composable
private fun CardListCard(cardManageState: CardManageState, onEvent: (CardManageEvent) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (cardManageState.cards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "카드 내역이 없습니다",
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
                    items(cardManageState.cards) { cardInfo ->
                        CardListItem(
                            cardInfo = cardInfo,
                            onClick = { onEvent(CardManageEvent.ClickedCarWith(cardInfo.id)) }
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

/* 카드정보 목록 아이템 */
@Composable
private fun CardListItem(cardInfo: CardDTO.CardsInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f)),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        TwoInfoBar(
            value1 = cardInfo.name,
            value2 = ""
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}