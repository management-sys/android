package com.example.attendancemanagementapp.ui.base

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
                is UiEffect.TargetDeleteNavigate -> navController.popBackStack(effect.route, false)
                is UiEffect.AllDeleteNavigate -> {
                    navController.navigate(effect.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is UiEffect.ShowToast -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}