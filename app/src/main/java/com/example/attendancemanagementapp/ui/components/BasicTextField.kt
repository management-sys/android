package com.example.attendancemanagementapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.DarkGray
import com.example.attendancemanagementapp.ui.theme.DisableGray
import com.example.attendancemanagementapp.ui.theme.MainBlue

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