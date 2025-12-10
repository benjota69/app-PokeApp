package com.example.balanceapp.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.balanceapp.data.PokemonRepository
import com.example.balanceapp.data.remote.PokemonDetail
import com.example.balanceapp.data.remote.PokemonItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//Clase que guarda TODO el estado necesario para la pantalla de la lista
data class EstadoListaPokemon(
    val cargando: Boolean = true, // ¿La lista está cargando?
    val pokemones: List<PokemonItem> = emptyList(), // Lista ya adaptada para la UI
    val error: String? = null, // Error si falla la petición
    val detalleCargando: Boolean = false, // ¿Se está cargando el detalle?
    val detalleSeleccionado: PokemonDetail? = null // Objeto con el detalle
)

// ViewModel = lógica + estado de la pantalla
class PokemonListViewModel(
    private val repositorio: PokemonRepository = PokemonRepository()
) : ViewModel() {
    // StateFlow PRIVADO y editable
    private val _estado = MutableStateFlow(EstadoListaPokemon())
    // Versión pública SOLO-LECTURA
    val estado: StateFlow<EstadoListaPokemon> = _estado.asStateFlow()


    init { cargar() }   // Se ejecuta al crear el ViewModel: carga automática

    // Cargar la LISTA completa
    fun cargar() {

        _estado.value = EstadoListaPokemon(cargando = true) // Indica que estamos cargando
        viewModelScope.launch {
            try {
                // Cargamos una cantidad moderada de pokémon para mantener buena velocidad
                val lista = repositorio.obtenerListaPokemon()
                _estado.value = EstadoListaPokemon(cargando = false, pokemones = lista) // Actualiza el estado con la lista
            } catch (t: Throwable) { // Error al cargar
                _estado.value = EstadoListaPokemon(cargando = false, error = t.message ?: "Error")
            }
        }
    }

    // Cargar el DETALLE de un Pokémon
    fun cargarDetalle(id: Int) {
        _estado.value = _estado.value.copy(detalleCargando = true, detalleSeleccionado = null) // Marca estado de carga del detalle
        viewModelScope.launch {
            try {
                val detalle = repositorio.obtenerDetallePokemon(id)
                _estado.value = _estado.value.copy(detalleCargando = false, detalleSeleccionado = detalle) // Actualiza el estado con el detalle
            } catch (t: Throwable) {
                _estado.value = _estado.value.copy(detalleCargando = false, error = t.message ?: "Error")
            }
        }
    }

    // Cerrar el diálogo de detalle
    fun cerrarDetalle() {
        _estado.value = _estado.value.copy(detalleSeleccionado = null)
    }
}


