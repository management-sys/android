package com.example.attendancemanagementapp.ui.base

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge

@Composable
fun CollectUiEffect(
    navController: NavController,
    vararg uiEffect: SharedFlow<UiEffect>
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        merge(*uiEffect).collectLatest { effect ->
            when (effect) {
                UiEffect.NavigateBack -> navController.popBackStack()
                is UiEffect.Navigate -> navController.navigate(effect.route)
                is UiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}