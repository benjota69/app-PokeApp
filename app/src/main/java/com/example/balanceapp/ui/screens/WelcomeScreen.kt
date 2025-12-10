package com.example.balanceapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.balanceapp.R
import com.example.balanceapp.ui.theme.BaseAndroidProjectTheme

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onGetStartedClick: () -> Unit = {}
) {
    // Column centrada en donde metemos los textos y el botón.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // titulo principal de bienvenida
        Text(
            text = stringResource(id = R.string.welcome_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))
        // subtitulo de bienvenida
        Text(
            text = stringResource(id = R.string.welcome_subtitle),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
        // descripción
        Text(
            text = stringResource(id = R.string.welcome_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))
        // botón para iniciar
        Button(
            onClick = onGetStartedClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(id = R.string.get_started))
        }
    }
}
// Preview simple en modo claro
@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    BaseAndroidProjectTheme {
        WelcomeScreen()
    }
}
// Preview simple en modo oscuro
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun WelcomeScreenDarkPreview() {
    BaseAndroidProjectTheme {
        WelcomeScreen()
    }
}
