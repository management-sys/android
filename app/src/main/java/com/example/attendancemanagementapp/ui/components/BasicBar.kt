package com.example.attendancemanagementapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LaptopWindows
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.DarkBlue
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

/* 기본 상단바 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTopBar(
    title: String,                  // 제목
    actIcon: ImageVector? = null,   // 액션 아이콘
    actTint: Color = Color.Black,   // 액션 아이콘 색상
    onClickNavIcon: () -> Unit,     // 뒤로 가기 아이콘 클릭 이벤트
    onClickActIcon: () -> Unit = {} // 액션 아이콘 클릭 이벤트
) {
    CenterAlignedTopAppBar(
        windowInsets = WindowInsets(0),
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onClickNavIcon()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로 가기 버튼"
                )
            }
        },
        actions = {
            if (actIcon != null) {
                IconButton(
                    onClick = {
                        onClickActIcon()
                    }
                ) {
                    Icon(
                        imageVector = actIcon,
                        contentDescription = "액션 버튼",
                        tint = actTint
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

/* 드로어 상단바 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerTopBar(
    onClickNavIcon: () -> Unit,     // 드로어 아이콘 클릭 이벤트
    onClickActIcon: () -> Unit = {} // 마이페이지 아이콘 클릭 이벤트
) {
    CenterAlignedTopAppBar(
        windowInsets = WindowInsets(0),
        title = {},
        navigationIcon = {
            IconButton(
                onClick = {
                    onClickNavIcon()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "드로어 버튼"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    onClickActIcon()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "마이페이지 버튼"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

/* 기본 하단 바 */
@Composable
fun BasicBottomBar(
    selected: Int,
    onSelected: (Int) -> Unit
) {
    val itemColors = NavigationBarItemDefaults.colors(
        indicatorColor = DarkBlue,      // 선택된 버튼 배경(빨간색)
        selectedIconColor = Color.White // 선택 아이콘 색
    )

    NavigationBar(
        windowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NavigationBarItem(
            selected = selected == 0,
            onClick = { onSelected(0) },
            icon = { Icon(imageVector = Icons.Outlined.Timer, contentDescription = "근무체크 아이콘")},
            label = { Text("근무체크") },
            colors = itemColors
        )
        NavigationBarItem(
            selected = selected == 1,
            onClick = { onSelected(1) },
            icon = { Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = "캘린더 아이콘")},
            label = { Text("캘린더") },
            colors = itemColors
        )
        NavigationBarItem(
            selected = selected == 2,
            onClick = { onSelected(2) },
            icon = { Icon(imageVector = Icons.Outlined.LaptopWindows, contentDescription = "프로젝트 아이콘")},
            label = { Text("프로젝트") },
            colors = itemColors
        )
    }
}

/* 정보 출력 바 */
@Composable
fun InfoBar(name: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 13.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(end = 10.dp).width(80.dp)
        )

        Text(
            text = value,
            fontSize = 16.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

/* 두 줄 정보 출력 바 */
@Composable
fun TowLineInfoBar(name: String, value: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 13.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                fontSize = 15.sp
            )
        }
    }
}

/* 두가지 정보 출력 바 */
@Composable
fun TwoInfoBar(value1: String, value2: String, color: Color = Color.Black, fontSize: TextUnit = 16.sp, fontWeight: FontWeight = FontWeight.Normal) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value1,
            fontSize = fontSize,
            color = color,
            fontWeight = fontWeight
        )

        Text(
            text = value2,
            fontSize = fontSize,
            color = if (value2 == "사용중") MainBlue else color
        )
    }
}

/* 정보 수정 바 */
@Composable
fun EditBar(
    name: String,                           // 이름
    value: String,                          // 값
    onValueChange: (String) -> Unit = {},   // 값 변경 시 실행 함수
    enabled: Boolean = true,                // 이용 가능 여부
    isRequired: Boolean = false,            // 필수 여부
    hint: String = "",                      // 값이 없을 때 보이는 값
    isPassword: Boolean = false             // 비밀번호 여부
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.35f)
        )

        BasicOutlinedTextField(
            modifier = Modifier.weight(0.65f),
            value = value,
            onValueChange = { onValueChange(it) },
            enabled = enabled,
            hint = hint,
            isPassword = isPassword
        )
    }
}

/* 두 줄 정보 수정 바 */
@Composable
fun TwoLineEditBar(
    name: String,                           // 이름
    value: String,                          // 값
    onValueChange: (String) -> Unit = {},   // 값 변경 시 실행 함수
    enabled: Boolean = true,                // 이용 가능 여부
    isRequired: Boolean = false,            // 필수 여부
    hint: String = "",                      // 값이 없을 때 보이는 값
    isPassword: Boolean = false,            // 비밀번호 여부
    onlyNumber: Boolean = false             // 숫자만 입력 가능
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        BasicOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { newValue ->
                if (onlyNumber) {
                    if (newValue.length <= 9 && newValue.matches(Regex("^[0-9]*$"))) {
                        onValueChange(newValue)
                    }
                } else {
                    onValueChange(newValue)
                }
            },
            hint = hint,
            enabled = enabled,
            isPassword = isPassword
        )
    }
}

/* 정보 수정 바 - 큰 텍스트 필드 */
@Composable
fun TwoLineBigEditBar(
    name: String,
    value: String,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        BasicOutlinedTextField(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            value = value,
            onValueChange = { onValueChange(it) },
            enabled = enabled
        )
    }
}

/* 정보 수정 바 - 라디오 버튼 */
@Composable
fun TwoLineRadioEditBar(
    name: String,
    selected: String,
    isRequired: Boolean = false
) {
    val options = listOf("사용", "미사용")

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            Modifier.fillMaxWidth().padding(end = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            options.forEach { info ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = info == selected,
                        enabled = false,
                        onClick = {}
                    )
                    Text(
                        text = info,
                        fontSize = 16.sp,
                        color = DarkGray
                    )
                }
            }
        }
    }
}

/* 정보 수정 바 - 검색 */
@Composable
fun TwoLineSearchEditBar(
    name: String,
    value: String,
    onClick: () -> Unit,
    isRequired: Boolean = false,
    enabled: Boolean = false,
    icon: ImageVector = Icons.Default.Search
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicOutlinedTextField(
                modifier = Modifier.weight(0.95f),
                value = value,
                onValueChange = {},
                readOnly = true,
                enabled = enabled
            )

            IconButton(
                onClick = { onClick() }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "검색",
                    tint = MainBlue
                )
            }
        }
    }
}

/* 정보 수정 바 - 드롭다운 */
@Composable
fun TwoLineDropdownEditBar(
    name: String,
    isRequired: Boolean = false,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        DropDownField(
            modifier = Modifier.fillMaxWidth(),
            options = options,
            selected = selected,
            onSelected = onSelected
        )
    }
}

/* 정보 수정 바 - 전화번호 */
@Composable
fun TwoLinePhoneEditBar(
    name: String,                           // 이름
    value: String,                          // 값
    onValueChange: (String) -> Unit = {},   // 값 변경 시 실행 함수
    enabled: Boolean = true,                // 이용 가능 여부
    isRequired: Boolean = false             // 필수 여부
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = { onValueChange(it) },
            singleLine = true,
            shape = RoundedCornerShape(5.dp),
            colors = BasicOutlinedTextFieldColors(),
            placeholder = {
                Text(
                    text = value.ifBlank { "01012345678" },
                    fontSize = 16.sp
                )
            },
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)   // 숫자 키보드 출력
        )
    }
}

/* 정보 수정 바 - 날짜 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEditBar(
    name: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,                // 이용 가능 여부
    isRequired: Boolean = false
) {
    var open by rememberSaveable { mutableStateOf(false) }
    val fmt = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.KOREA) }

    val parsed = remember(value) { runCatching { LocalDate.parse(value, fmt) }.getOrNull() }
    val initialMillis = remember(parsed) { parsed?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli() }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    if (open) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onValueChange(picked.format(fmt))
                    }
                    open = false
                }) {
                    Text(
                        text = "확인",
                        color = MainBlue
                    )
                }
            },
            dismissButton = { TextButton({ open = false }) { Text(text = "취소", color = TextGray) } }
        ) {
            DatePicker(
                state = pickerState,
                showModeToggle = true,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    selectedDayContainerColor = MainBlue,
                    todayDateBorderColor = MainBlue,
                    todayContentColor = MainBlue
                )
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.35f)
        )

        Box(modifier = Modifier.weight(0.65f)) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                shape = RoundedCornerShape(5.dp),
                colors = BasicOutlinedTextFieldColors(),
                placeholder = {
                    Text(
                        text = value.ifBlank { "연도-월-일" },
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "캘린더 열기",
                        modifier = Modifier.then(
                            if (enabled) {
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { open = true }
                            } else {
                                Modifier
                            }
                        )
                    )
                },
                enabled = enabled
            )
        }
    }
}

/* 정보 수정 바 - 날짜 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoLineDateEditBar(
    name: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,                // 이용 가능 여부
    isRequired: Boolean = false,
    modifier: Modifier = Modifier
) {
    var open by rememberSaveable { mutableStateOf(false) }
    val fmt = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.KOREA) }

    val parsed = remember(value) { runCatching { LocalDate.parse(value, fmt) }.getOrNull() }
    val initialMillis = remember(parsed) { parsed?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli() }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    if (open) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onValueChange(picked.format(fmt))
                    }
                    open = false
                }) {
                    Text(
                        text = "확인",
                        color = MainBlue
                    )
                }
            },
            dismissButton = { TextButton({ open = false }) { Text(text = "취소", color = TextGray) } }
        ) {
            DatePicker(
                state = pickerState,
                showModeToggle = true,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    selectedDayContainerColor = MainBlue,
                    todayDateBorderColor = MainBlue,
                    todayContentColor = MainBlue
                )
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = modifier,
                value = value,
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                shape = RoundedCornerShape(5.dp),
                colors = BasicOutlinedTextFieldColors(),
                placeholder = {
                    Text(
                        text = value.ifBlank { "연도-월-일" },
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "캘린더 열기",
                        modifier = Modifier.then(
                            if (enabled) {
                                Modifier.clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { open = true }
                            } else {
                                Modifier
                            }
                        )
                    )
                },
                enabled = enabled
            )
        }
    }
}

/* 정보 수정 바 - 날짜, 삭제 아이콘 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwoLineDateEditDeleteBar(
    name: String,
    value: String,
    onClick: () -> Unit,
    onValueChange: (String) -> Unit,
    isRequired: Boolean = false
) {
    var open by rememberSaveable { mutableStateOf(false) }
    val fmt = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.KOREA) }

    val parsed = remember(value) { runCatching { LocalDate.parse(value, fmt) }.getOrNull() }
    val initialMillis = remember(parsed) { parsed?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli() }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

    if (open) {
        DatePickerDialog(
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val picked = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        onValueChange(picked.format(fmt))
                    }
                    open = false
                }) {
                    Text(
                        text = "확인",
                        color = MainBlue
                    )
                }
            },
            dismissButton = { TextButton({ open = false }) { Text(text = "취소", color = TextGray) } }
        ) {
            DatePicker(
                state = pickerState,
                showModeToggle = true,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,
                    selectedDayContainerColor = MainBlue,
                    todayDateBorderColor = MainBlue,
                    todayContentColor = MainBlue
                )
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                append(name)
                if (isRequired) {
                    append(" ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                }
            },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                modifier = Modifier.weight(0.95f),
                value = value,
                onValueChange = {},
                singleLine = true,
                readOnly = true,
                shape = RoundedCornerShape(5.dp),
                colors = BasicOutlinedTextFieldColors(),
                placeholder = {
                    Text(
                        text = value.ifBlank { "연도-월-일" },
                        fontSize = 16.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "캘린더 열기",
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { open = true }
                    )
                }
            )

            IconButton(
                onClick = { onClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "날짜 삭제",
                    tint = MainBlue
                )
            }
        }
    }
}