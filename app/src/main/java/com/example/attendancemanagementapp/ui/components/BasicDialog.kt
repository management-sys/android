package com.example.attendancemanagementapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.attendancemanagementapp.ui.theme.MainBlue

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
                    onDismiss()
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