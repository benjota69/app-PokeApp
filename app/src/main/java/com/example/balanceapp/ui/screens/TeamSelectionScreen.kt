package com.example.balanceapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamSelectionScreen(
    onTeamSelectedAndSaved: () -> Unit,
    viewModel: UserProfileViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Estado local: qué team está seleccionado actualmente
    var selectedTeam by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Elige tu team") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Elige tu team de Pokémon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Puedes cambiarlo después en tu perfil.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            if (state.loading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TeamCard(
                        name = "Team Valor (Rojo)",
                        description = "Liderado por Candela. Cree que la verdadera fuerza de un Pokémon se logra entrenando con pasión y coraje.",
                        color = Color(0xFFE53935),
                        isSelected = selectedTeam == "Rojo",
                        onClick = { selectedTeam = "Rojo" }
                    )
                    TeamCard(
                        name = "Team Mystic (Azul)",
                        description = "Liderado por Blanche. Valora la sabiduría y el estudio de la evolución para entender a los Pokémon.",
                        color = Color(0xFF1E88E5),
                        isSelected = selectedTeam == "Azul",
                        onClick = { selectedTeam = "Azul" }
                    )
                    TeamCard(
                        name = "Team Instinct (Amarillo)",
                        description = "Liderado por Spark. Confía en la intuición y en el instinto natural que une a los entrenadores con sus Pokémon.",
                        color = Color(0xFFFFEB3B),
                        isSelected = selectedTeam == "Amarillo",
                        onClick = { selectedTeam = "Amarillo" }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        selectedTeam?.let { team ->
                            viewModel.saveTeam(team, onTeamSelectedAndSaved)
                        }
                    },
                    enabled = selectedTeam != null && !state.loading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) {
                    Text("Confirmar team")
                }

                state.error?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamCard(
    name: String,
    description: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "teamScale"
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 10.dp else 4.dp,
        animationSpec = tween(durationMillis = 200),
        label = "teamElevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = color
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(elevation),
        border = if (isSelected) BorderStroke(3.dp, Color.White) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = Color.Black,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

