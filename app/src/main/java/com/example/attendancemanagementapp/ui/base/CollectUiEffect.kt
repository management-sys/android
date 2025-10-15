package com.example.attendancemanagementapp.ui.base

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
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
    uiEffect: SharedFlow<UiEffect>,
    navController: NavController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        uiEffect.collect { effect ->
            when (effect) {
                UiEffect.NavigateBack -> navController.popBackStack()
                is UiEffect.Navigate -> navController.navigate(effect.route)
                is UiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT)
                is UiEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
}