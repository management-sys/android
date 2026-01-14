package com.example.attendancemanagementapp.ui.asset.car.manage

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
import com.example.attendancemanagementapp.ui.asset.car.CarViewModel
import com.example.attendancemanagementapp.ui.components.BasicFloatingButton
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.components.DropDownField
import com.example.attendancemanagementapp.ui.components.TwoInfoBar
import com.example.attendancemanagementapp.ui.components.search.SearchBar
import com.example.attendancemanagementapp.ui.components.search.SearchState
import com.example.attendancemanagementapp.ui.theme.TextGray
import com.example.attendancemanagementapp.util.rememberOnce

/* 차량정보 관리 화면 */
@Composable
fun CarManageScreen(navController: NavController, carViewModel: CarViewModel) {
    val focusManager = LocalFocusManager.current    // 포커스 관리

    val onEvent = carViewModel::onManageEvent
    val carManageState by carViewModel.carManageState.collectAsState()

    LaunchedEffect(Unit) {
        onEvent(CarManageEvent.Init)
    }

    Scaffold(
        modifier = Modifier.pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) },
        topBar = {
            BasicTopBar(
                title = "차량정보 관리",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            BasicFloatingButton(
                onClick = { navController.navigate("carAdd") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropDownField(
                modifier = Modifier.fillMaxWidth(),
                options = listOf("전체", "차량명", "차량번호"),
                selected = carManageState.type,
                onSelected = { onEvent(CarManageEvent.SelectedTypeWith(it)) }
            )

            Spacer(modifier = Modifier.height(10.dp))

            SearchBar(
                searchState = SearchState(
                    value = carManageState.searchText,
                    onValueChange = { onEvent(CarManageEvent.ChangedSearchTextWith(it)) },
                    onClickSearch = { onEvent(CarManageEvent.ClickedSearch) },
                    onClickInit = { onEvent(CarManageEvent.ClickedInitSearchText) }
                )
            )

            Spacer(Modifier.height(15.dp))

            CarListCard(
                carManageState = carManageState,
                onEvent = onEvent
            )
        }
    }
}

/* 차량정보 목록 출력 카드 */
@Composable
private fun CarListCard(carManageState: CarManageState, onEvent: (CarManageEvent) -> Unit) {
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
            if (carManageState.cars.isEmpty()) {
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(carManageState.cars) { carInfo ->
                        CarListItem(
                            carInfo = carInfo,
                            onClick = { onEvent(CarManageEvent.ClickedCarWith(carInfo.id)) }
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

/* 차량정보 목록 아이템 */
@Composable
private fun CarListItem(carInfo: CarDTO.CarsInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 0.5.dp, color = DividerDefaults.color.copy(alpha = 0.8f)),
        onClick = onClick
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        TwoInfoBar(
            value1 = carInfo.name,
            value2 = ""
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}