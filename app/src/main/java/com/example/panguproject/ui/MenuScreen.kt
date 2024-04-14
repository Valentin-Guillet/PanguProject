package com.example.panguproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.panguproject.GameViewModel
import com.example.panguproject.R
import com.example.panguproject.ui.theme.BackgroundColor
import com.example.panguproject.ui.theme.EcruWhite
import com.example.panguproject.ui.theme.PinkRed

@Composable
fun MenuScreen(
    navController: NavController?,
    gameViewModel: GameViewModel? = null,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Pangu\nProject",
                textAlign = TextAlign.Center,
                fontSize = 64.sp,
                color = EcruWhite,
                lineHeight = 80.sp,
            )
            Spacer(modifier = Modifier.height(90.dp))
            if (gameViewModel?.gameStateStorage?.hasSavedState() == true) {
                Button(
                    onClick = {
                        gameViewModel.loadGame()
                        navController?.navigate("game")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkRed),
                ) {
                    Text(
                        text = stringResource(id = R.string.resume),
                        fontSize = 24.sp,
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            Button(
                onClick = {
                    navController?.navigate("game")
                    gameViewModel?.newGame()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkRed),
            ) {
                Text(
                    text = stringResource(id = R.string.play),
                    fontSize = 24.sp,
                )
            }
        }
    }
}

@Preview(device = "id:S9+")
@Composable
fun MenuScreenPreview() {
    MenuScreen(null)
}