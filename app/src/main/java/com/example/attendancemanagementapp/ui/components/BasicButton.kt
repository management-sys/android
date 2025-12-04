package com.example.attendancemanagementapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.LightBlue
import com.example.attendancemanagementapp.ui.theme.MainBlue

/* 기본 플로팅 버튼 */
@Composable
fun BasicFloatingButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MainBlue,
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "플로팅 버튼"
        )
    }
}

/* 기본 버튼 */
@Composable
fun BasicButton(name: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.width(60.dp).height(40.dp),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainBlue,
            contentColor = Color.White
        )
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* 부 버튼 */
@Composable
fun SubButton(name: String, onClick: () -> Unit) {
    OutlinedButton(
        modifier = Modifier.width(60.dp).height(40.dp),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LightBlue,
            contentColor = MainBlue
        ),
        border = BorderStroke(width = 1.dp, color = MainBlue)
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* 기본 긴 버튼 */
@Composable
fun BasicLongButton(name: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp).height(40.dp),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MainBlue,
            contentColor = Color.White
        ),
        enabled = enabled
    ) {
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* 기본 텍스트 버튼 */
@Composable
fun BasicTextButton(name: String, onClick: () -> Unit, color: Color = DarkGray) {
    TextButton(
        modifier = Modifier.padding(end = 10.dp),
        onClick = { onClick() }
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            color = color,
            textDecoration = TextDecoration.Underline
        )
    }
}

/* 기본 체크박스 */
@Composable
fun BasicCheckbox(enabled: Boolean = true, isChecked: Boolean, onChecked: (Boolean) -> Unit) {
    Checkbox(
        enabled = enabled,
        checked = isChecked,
        onCheckedChange = { onChecked(it) },
        colors = if (enabled) {
            CheckboxDefaults.colors(
                checkedColor = MainBlue,
                checkmarkColor = Color.White
            )
        }
        else {
            CheckboxDefaults.colors(
                disabledCheckedColor = DisableGray,
                checkmarkColor = DarkGray
            )
        }
    )
}