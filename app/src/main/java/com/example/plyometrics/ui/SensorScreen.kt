package com.example.plyometrics.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
    val isRunning by viewModel.isRunning.collectAsState()
    val result by viewModel.jumpResult.collectAsState()

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

        Spacer(Modifier.height(24.dp))

        result?.let { jump ->
            Text(text = "Flight time: ${jump.flightTime} ms")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Jump height: %.2f m".format(jump.height))
        }
    }
}