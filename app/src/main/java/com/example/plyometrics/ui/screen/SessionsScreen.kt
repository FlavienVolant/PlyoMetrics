package com.example.plyometrics.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.plyometrics.model.JumpPoint
import com.example.plyometrics.ui.components.SessionItem
import com.example.plyometrics.ui.theme.PlyoMetricsTheme
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun SessionsScreen(viewModel: SensorViewModel) {

    val sessions by viewModel.sessions.collectAsState()

    SessionsScreen(
        sessions = sessions
    )
}

@Composable
fun SessionsScreen(
    sessions: List<List<JumpPoint>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Sessions",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No session register")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(sessions) { _, session ->
                    SessionItem(
                        session = session
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionsScreenPreview() {
    PlyoMetricsTheme {
        SessionsScreen(
            sessions = previewSessions()
        )
    }
}

private fun previewSessions(): List<List<JumpPoint>> {
    return listOf(
        List(100) { index ->
            JumpPoint(
                timestamp = index * 5_000_000L,
                accelerationZ = 9.81f
            )
        },

        List(250) { index ->
            JumpPoint(
                timestamp = index * 5_000_000L,
                accelerationZ = 9.81f + kotlin.math.sin(index * 0.1).toFloat() * 2f
            )
        }
    )
}