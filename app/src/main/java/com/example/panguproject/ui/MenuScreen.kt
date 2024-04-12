package com.example.panguproject.ui

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.panguproject.GameViewModel
import com.example.panguproject.R

@Composable
fun MenuScreen(
    navController: NavController?,
    gameViewModel: GameViewModel? = null,
) {
    val activity = LocalContext.current as? Activity
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Pangu Project",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(66.dp))
            if (gameViewModel?.gameStateStorage?.hasSavedState() == true) {
                Button(onClick = {
                    gameViewModel.loadGame()
                    navController?.navigate("game")
                }) {
                    Text(stringResource(id = R.string.resume))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(onClick = {
                gameViewModel?.newGame()
                navController?.navigate("game")
            }) {
                Text(stringResource(id = R.string.play))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                activity?.finish()
            }) {
                Text(stringResource(id = R.string.quit))
            }
        }
    }
}

@Preview(device = "id:S9+")
@Composable
fun MenuScreenPreview() {
    MenuScreen(null)
}