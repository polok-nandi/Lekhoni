package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.LekhoniTheme
import com.example.viewmodel.LekhoniViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: LekhoniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LekhoniTheme {
                val currentScreen by viewModel.currentScreen.collectAsState()
                val feedbackMessage by viewModel.feedbackMessage.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(feedbackMessage) {
                    feedbackMessage?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        viewModel.feedbackMessage.value = null
                    }
                }

                when (val screen = currentScreen) {
                    is Screen.Dashboard -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onOpenDocument = { docId ->
                                viewModel.openDocument(docId)
                            },
                            onNavigateToSettings = {
                                viewModel.navigateTo(Screen.Settings)
                            }
                        )
                    }
                    is Screen.Editor -> {
                        EditorScreen(
                            viewModel = viewModel,
                            onBack = {
                                viewModel.navigateTo(Screen.Dashboard)
                            }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            viewModel = viewModel,
                            onBack = {
                                viewModel.navigateTo(Screen.Dashboard)
                            }
                        )
                    }
                }
            }
        }
    }
}
