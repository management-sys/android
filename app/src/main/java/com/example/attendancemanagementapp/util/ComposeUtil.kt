package com.example.attendancemanagementapp.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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