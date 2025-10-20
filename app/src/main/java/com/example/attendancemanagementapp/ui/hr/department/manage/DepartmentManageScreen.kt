package com.example.attendancemanagementapp.ui.hr.department.manage

import android.util.Log
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.SubdirectoryArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.example.attendancemanagementapp.data.dto.DepartmentDTO
import com.example.attendancemanagementapp.ui.components.BasicDialog
import com.example.attendancemanagementapp.ui.components.BasicTopBar
import com.example.attendancemanagementapp.ui.hr.department.DepartmentViewModel
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.util.rememberOnce
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.max

/* 부서 관리 화면 */
@Composable
fun DepartmentManageScreen(navController: NavController, departmentViewModel: DepartmentViewModel) {
    val onEvent = departmentViewModel::onManageEvent

    val departmentManageState by departmentViewModel.departmentManageState.collectAsState()

    var fromInfo by remember { mutableStateOf(DepartmentDTO.DepartmentsInfo()) }  // 드래그 시작하는 부서 정보
    var toInfo by remember { mutableStateOf(DepartmentDTO.DepartmentsInfo()) }    // 드래그 끝내는 부서 정보

    var openUpdateOrderDialog by remember { mutableStateOf(false) } // 부서 위치 변경 확인 디알로그 열림 상태

    if (openUpdateOrderDialog) {
        BasicDialog(
            title = "부서 위치를 변경하시겠습니까?",
            onDismiss = {
                // 취소 -> 원래 리스트로 변경
                openUpdateOrderDialog = false
            },
            onClickConfirm = {
                // 확인 -> 변경한 리스트로 저장
                onEvent(DepartmentManageEvent.MoveDepartmentWith(fromInfo, toInfo))
            }
        )
    }

    Scaffold(
        topBar = {
            BasicTopBar(
                title = "부서 관리",
                onClickNavIcon = rememberOnce { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var deptList by remember { mutableStateOf(departmentManageState.departments) }
            val lazyListState = rememberLazyListState()

            var currentDraggedIndex by remember { mutableStateOf<Int?>(null) }
            var currentHoverDepth by remember { mutableStateOf<Int?>(null) }

            val state = rememberReorderableLazyListState(
                lazyListState = lazyListState,
                onMove = { from, to ->
                    deptList = deptList.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }

                    fromInfo = departmentManageState.departments[from.index]
                    toInfo = departmentManageState.departments[to.index]

                    // 드래그 중인 아이템 정보 갱신
                    currentDraggedIndex = to.index
                    currentHoverDepth = departmentManageState.departments[to.index].depth
                }
            )

            LaunchedEffect(state.isAnyItemDragging) {
                if (!state.isAnyItemDragging) {
                    openUpdateOrderDialog = departmentManageState.departments != deptList
                    currentDraggedIndex = null
                    currentHoverDepth = null
                }
            }

            LazyColumn(
                state = lazyListState
            ) {
                itemsIndexed(deptList, key = { _, item -> item.id }) { index, dept ->
                    ReorderableItem(state = state, key = dept.id) { isDragging ->
                        // 드래그 중 depth 시각적 변경
                        val visualDepth =
                            if (isDragging && currentHoverDepth != null) currentHoverDepth!!
                            else dept.depth

                        // depth 변화에 따라 부드럽게 애니메이션 적용
                        val animatedIndent by animateDpAsState(
                            targetValue = max(visualDepth * 26.dp, 1.dp),
                            animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing),
                            label = "depthIndentAnim"
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.width(animatedIndent),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (visualDepth > 0) {
                                    Icon(
                                        imageVector = Icons.Default.SubdirectoryArrowRight,
                                        contentDescription = "부서 깊이",
                                        tint = DarkGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }

                            DepartmentInfoItem(
                                modifier = Modifier.draggableHandle(),
                                dept = dept,
                                isDragging = isDragging,
                                onClick = { onEvent(DepartmentManageEvent.SelectedDepartmentWith(dept.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

    /* 부서 목록 아이템 */
    @Composable
    fun DepartmentInfoItem(
        modifier: Modifier,
        dept: DepartmentDTO.DepartmentsInfo,
        isDragging: Boolean,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(
                    if (isDragging)
                        Modifier.shadow(10.dp, RoundedCornerShape(10.dp))
                    else Modifier
                ),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 8.dp else 1.dp),
            onClick = { onClick() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dept.name,
                    modifier = Modifier.padding(start = 10.dp)
                )

                IconButton(
                    modifier = modifier,
                    onClick = {}
                ) {
                    Icon(
                        imageVector = Icons.Default.DragHandle,
                        contentDescription = "드래그 아이콘",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }