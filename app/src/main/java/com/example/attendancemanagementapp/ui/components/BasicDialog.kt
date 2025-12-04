package com.example.attendancemanagementapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.BackgroundColor
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.MiddleBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/* 기본 디알로그 */
@Composable
fun BasicDialog(
    title: String,              // 제목
    text: String = "",          // 내용
    onDismiss: () -> Unit,
    onClickConfirm: () -> Unit,
    onClickDismiss: () -> Unit = onDismiss
) {
    AlertDialog(
        title = {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = text,
                fontSize = 15.sp
            )
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClickConfirm()
                }
            ) {
                Text(
                    text = "확인",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onClickDismiss()
                }
            ) {
                Text(
                    text = "취소",
                    fontSize = 16.sp,
                    color = MainBlue
                )
            }
        },
        containerColor = Color.White
    )
}

/* 날짜 선택 디알로그 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicDatePickerDialog(
    initialDateTime: String? = null,                  // "yyyy-MM-dd'T'HH:mm:ss"
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit                       // 선택 결과 "yyyy-MM-dd'T'HH:mm:ss"
) {
    val dateTimeFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    }

    // 다이얼로그 처음 열릴 때 선택해 둘 날짜 millis
    val initialMillis = remember(initialDateTime) {
        initialDateTime?.let {
            runCatching {
                LocalDateTime.parse(it, dateTimeFormatter)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            }.getOrNull()
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton

                    val selectedDate = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    // 기존 값이 있으면 날짜만 바꾸고 시간은 유지
                    val resultDateTime = initialDateTime?.let {
                        runCatching {
                            val old = LocalDateTime.parse(it, dateTimeFormatter)
                            LocalDateTime.of(
                                selectedDate,
                                old.toLocalTime()
                            )
                        }.getOrNull()
                    } ?: selectedDate.atStartOfDay()

                    onConfirm(resultDateTime.format(dateTimeFormatter))
                    onDismiss()
                }
            ) {
                Text(
                    text = "확인",
                    color = MainBlue
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "취소",
                    color = TextGray
                )
            }
        },
        colors = DatePickerDefaults.colors(
            containerColor = BackgroundColor
        )
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = BackgroundColor,
                todayDateBorderColor = MainBlue,        // 오늘 날짜 테두리 색
                todayContentColor = MainBlue,           // 오늘 날짜 텍스트 색
                selectedDayContainerColor = MainBlue,   // 선택된 날짜 배경색
                selectedDayContentColor = Color.White,  // 선택된 날짜 텍스트 색
                currentYearContentColor = MainBlue,     // 현재 연도 텍스트 색
                selectedYearContainerColor = MainBlue,  // 선택된 연도 배경색
                selectedYearContentColor = Color.White  // 선택된 연도 텍스트 색
            )
        )
    }
}

/* 시간 선택 디알로그 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTimePickerDialog(
    initialDateTime: String? = null,                  // "yyyy-MM-dd'T'HH:mm:ss"
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit                       // 선택 결과 "yyyy-MM-dd'T'HH:mm:ss"
) {
    val dateTimeFormatter = remember {
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    }

    val baseDateTime = remember(initialDateTime) {
        initialDateTime?.let {
            runCatching { LocalDateTime.parse(it, dateTimeFormatter) }.getOrNull()
        } ?: LocalDateTime.now()
    }

    val timePickerState = rememberTimePickerState(
        initialHour = baseDateTime.hour,
        initialMinute = baseDateTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val newDateTime = LocalDateTime.of(
                        baseDateTime.toLocalDate(),
                        LocalTime.of(timePickerState.hour, timePickerState.minute)
                    )

                    onConfirm(newDateTime.format(dateTimeFormatter))
                    onDismiss()
                }
            ) {
                Text(
                    text = "확인",
                    color = MainBlue
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "취소",
                    color = TextGray
                )
            }
        },
        title = {
            Text(
                text = "시간 선택",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        containerColor = BackgroundColor,
        text = {
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = Color.White,                       // 다이얼 배경 색
                    selectorColor = MainBlue,                           // 시/분 선택 포인트 색
                    timeSelectorSelectedContainerColor = MiddleBlue,    // 선택된 시/분 숫자 배경 색
                    timeSelectorSelectedContentColor = Color.White,     // 선택된 시/분 숫자 텍스트 색
                    timeSelectorUnselectedContainerColor = Color.White, // 선택되지 않은 시/분 숫자 배경 색
                    timeSelectorUnselectedContentColor = Color.Black    // 선택되지 않은 시/분 숫자 텍스트 색
                )
            )
        }
    )
}