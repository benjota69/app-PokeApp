package com.example.balanceapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import coil.compose.AsyncImage
import com.example.balanceapp.data.remote.PokemonItem
// Pantalla PRINCIPAL de la Pokédex: lista de Pokémon + detalle en diálogo.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonListScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: PokemonListViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Cada vez que cambia, la UI se recompone.
    val estado by viewModel.estado.collectAsState()

    // Estados de búsqueda y filtros en la propia pantalla.
    var buscando by remember { mutableStateOf(false) } // para mostrar/ocultar el buscador
    var consulta by remember { mutableStateOf("") }
    var tipoSeleccionado by remember { mutableStateOf<String?>(null) }

    // Lista de tipos disponibles para filtros (derivados de la data)
    val todosLosTipos = remember(estado.pokemones) {
        estado.pokemones.flatMap { it.types }.distinct().sorted()
    }

    // Lista de pokémon a mostrar, filtrada según la consulta y el tipo seleccionado.
    val itemsParaMostrar = remember(estado.pokemones, consulta, tipoSeleccionado) {
        estado.pokemones
            .filter { item ->
                consulta.isBlank() || item.name.contains(consulta, ignoreCase = true)
            }
            .filter { item ->
                tipoSeleccionado == null || item.types.contains(tipoSeleccionado)
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokedex") },
                actions = {
                    // Botón de perfil
                    IconButton(onClick = onOpenProfile) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Perfil"
                        )
                    }
                    // Botón para cerrar sesión
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                    // Icono de búsqueda en la barra superior.
                    IconButton(onClick = { buscando = !buscando }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Si está activada la búsqueda, mostramos el campo de texto.
            if (buscando) {
                OutlinedTextField(
                    value = consulta,
                    onValueChange = { consulta = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = { Text("Buscar Pokémon...") }
                )
            }
            // Filtros por tipo de Pokémon
            if (todosLosTipos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val selectedBorder = AssistChipDefaults.assistChipBorder(
                        borderColor = Color(0xFF430000),
                        borderWidth = 2.dp
                    )
                    val allSelected = tipoSeleccionado == null
                    AssistChip(
                        onClick = { tipoSeleccionado = null },
                        label = { Text("Todos") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (allSelected)
                                Color(0xFF430000).copy(alpha = 0.15f)
                            else
                                Color.Transparent,
                            labelColor = MaterialTheme.colorScheme.onBackground
                        ),
                        border = if (allSelected) selectedBorder else null
                    )
                    todosLosTipos.forEach { tipo ->
                        val isSelected = tipoSeleccionado == tipo
                        AssistChip(
                            onClick = {
                                tipoSeleccionado =
                                    if (isSelected) null else tipo
                            },
                            label = { Text(tipo) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (isSelected)
                                    Color(0xFF430000).copy(alpha = 0.15f)
                                else
                                    Color.Transparent,
                                labelColor = MaterialTheme.colorScheme.onBackground
                            ),
                            border = if (isSelected) selectedBorder else null
                        )
                    }
                }
            }
            // Distintos estados de la pantalla
            when {
                // Modo cargando: muestra el spinner al centro.
                estado.cargando -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                // estado error, mostrar el mensaje.
                estado.error != null -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text(estado.error ?: "Error") }

                // estado normal: mostrar la lista de pokémon en una cuadrícula de cards
                else -> LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(itemsParaMostrar) { item ->
                        PokemonCard(
                            item = item,
                            onClick = { viewModel.cargarDetalle(item.id) }
                        )
                    }
                }
            }
        }
        // Si el detalle se está cargando, mostramos otro spinner encima
        if (estado.detalleCargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // Si hay un detalle seleccionado, mostramos el AlertDialog con la info del pokemon
        estado.detalleSeleccionado?.let { detail ->
            AlertDialog(
                onDismissRequest = { viewModel.cerrarDetalle() },
                confirmButton = {
                    TextButton(onClick = {
                        // al cerrar el diálogo, limpiamos el detalle en el ViewModel
                        viewModel.cerrarDetalle() }) { Text("Cerrar") }
                },
                title = { Text(text = "${detail.id}. ${detail.name}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // imagen más grande del pokemon
                        AsyncImage(
                            model = detail.imageUrl,
                            contentDescription = detail.name,
                            modifier = Modifier.height(120.dp).fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                        Text("Altura: ${detail.height}")
                        Text("Peso: ${detail.weight}")

                        // tipos del pokemon como chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            detail.types.forEach { t ->
                                AssistChip(onClick = {}, label = { Text(t) })
                            }
                        }
                        // estadisticas básicas: Ataque, Defensa, etc
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            detail.stats.forEach { (name, value) ->
                                // Capitalizamos la primera letra del nombre de la stat.
                                Text("${name.replaceFirstChar { it.titlecase() }}: ${value}")
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun PokemonCard(
    item: PokemonItem,
    onClick: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF3B0000), // rojo muy oscuro arriba
                            Color(0xFF7B1111)  // rojo más vivo abajo
                        )
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Imagen del Pokémon destacada
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .height(96.dp),
                    contentScale = ContentScale.Fit
                )

                // ID estilizado
                Text(
                    text = "#${item.id.toString().padStart(3, '0')}",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )

                // Nombre con tipografía más grande y en blanco
                Text(
                    text = item.name.replaceFirstChar { it.titlecase() },
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


