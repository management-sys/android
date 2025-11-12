package com.example.attendancemanagementapp.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/* 뒤로 가기 버튼 한 번만 동작하게 처리 */
@Composable
fun rememberOnce(action: () -> Unit): () -> Unit {
    var done by remember { mutableStateOf(false) }
    return {
        if (!done) {
            done = true
            action()
        }
    }
}