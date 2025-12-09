package com.example.attendancemanagementapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue
import com.example.attendancemanagementapp.ui.theme.TextGray
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun BasicOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    readOnly: Boolean = false,
    enabled: Boolean = true,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChange(it) },
        readOnly = readOnly,
        singleLine = true,
        shape = RoundedCornerShape(5.dp),
        colors = BasicOutlinedTextFieldColors(),
        placeholder = {
            Text(
                text = value.ifBlank { hint },
                fontSize = 16.sp
            )
        },
        enabled = enabled,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}

@Composable
fun BasicOutlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedContainerColor = Color.White,
    focusedContainerColor = Color.White,
    unfocusedBorderColor = DarkGray,
    focusedBorderColor = MainBlue,
    disabledContainerColor = DisableGray,
    disabledBorderColor = DarkGray,
    disabledTextColor = DarkGray,
    cursorColor = MainBlue,
)

/* 날짜 선택 필드 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateEditField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit
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
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { open = true }
            )
        }
    )
}
