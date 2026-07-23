package com.example.plyometrics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.plyometrics.viewmodel.SensorViewModel

@Composable
fun SensorScreen(viewModel: SensorViewModel) {
    val acceleration by viewModel.acceleration.collectAsState()
    val session by viewModel.session.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
        ) {
        Button(onClick = {
            if(isRunning)
                viewModel.stop()
            else
                viewModel.start()
        }) {
            Text(if(isRunning) "Stop" else "Start")
        }

        Spacer(Modifier.height(12.dp))

        Text("X : ${acceleration.x}")
        Text("Y : ${acceleration.y}")
        Text("Z : ${acceleration.z}")
        Text("Magnitude : ${acceleration.magnitude}")

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(session) { point ->
                Text("${point.time} ms : ${point.acceleration.magnitude}")
            }
        }
    }
}