/*PokemonRepository se encarga de acceder a los datos, llamando a la API PokeApiService
transforma las respuestas PokemonListResponse, PokemonDetailResponse
en modelos que usa la app PokemonItem, PokemonDetail
*/
package com.example.balanceapp.data

import com.example.balanceapp.data.remote.PokeApiService
import com.example.balanceapp.data.remote.PokemonDetail
import com.example.balanceapp.data.remote.PokemonItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

// Se comunica con PokeApiService y adapta los datos para la app
class PokemonRepository(
    private val api: PokeApiService = PokeApiService.create()
) {
    // Traduce nombres de estadísticas al español
    private fun traducirNombreStats(nombreApi: String): String = when (nombreApi) {
        "hp" -> "PS"
        "attack" -> "Ataque"
        "defense" -> "Defensa"
        "special-attack" -> "Ataque especial"
        "special-defense" -> "Defensa especial"
        "speed" -> "Velocidad"
        else -> nombreApi.replace('-', ' ').replaceFirstChar { it.titlecase() }
    }
    // Traduce tipos al español
    private fun traducirTipo(nombreApi: String): String = when (nombreApi) {
        "bug" -> "Bicho"
        "dark" -> "Siniestro"
        "dragon" -> "Dragón"
        "electric" -> "Eléctrico"
        "fairy" -> "Hada"
        "fighting" -> "Lucha"
        "fire" -> "Fuego"
        "flying" -> "Volador"
        "ghost" -> "Fantasma"
        "grass" -> "Planta"
        "ground" -> "Tierra"
        "ice" -> "Hielo"
        "normal" -> "Normal"
        "poison" -> "Veneno"
        "psychic" -> "Psíquico"
        "rock" -> "Roca"
        "steel" -> "Acero"
        "water" -> "Agua"
        else -> nombreApi.replace('-', ' ').replaceFirstChar { it.titlecase() }
    }

    // devuelve una lista para la interfaz, con id, nombre, imagen y tipos de cada Pokémon.
    // Para no hacer la app lenta, limitamos la cantidad y pedimos los tipos en paralelo.
    suspend fun obtenerListaPokemon(limit: Int = 60): List<PokemonItem> = coroutineScope {
        val response = api.getPokemonList(limit = limit, offset = 0)

        // Primero armamos una lista básica con id, nombre e imagen
        val baseList = response.results.mapNotNull { r ->
            val id = r.url.trimEnd('/').substringAfterLast('/').toIntOrNull()
            id?.let {
                PokemonItem(
                    id = it,
                    name = r.name.replaceFirstChar { c -> c.titlecase() }, // Capitaliza
                    imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$it.png"
                )
            }
        }

        // Luego, para cada Pokémon pedimos el detalle en paralelo para obtener sus tipos
        baseList.map { item ->
            async {
                try {
                    val detalle = api.getPokemonDetail(item.id)
                    item.copy(
                        types = detalle.types.map { slot -> traducirTipo(slot.type.name) }
                    )
                } catch (_: Throwable) {
                    // Si falla el detalle, devolvemos al menos el item básico sin tipos
                    item
                }
            }
        }.awaitAll()
    }

    // Obtiene el DETALLE y lo adapta para la UI
    suspend fun obtenerDetallePokemon(id: Int): PokemonDetail {
        val res = api.getPokemonDetail(id)
        return PokemonDetail(
            id = res.id,
            name = res.name.replaceFirstChar { it.titlecase() },
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${res.id}.png",
            height = res.height,
            weight = res.weight,
            types = res.types.map { traducirTipo(it.type.name) }, // Traduce Tipos
            stats = res.stats.map { traducirNombreStats(it.stat.name) to it.base_stat } // Traduce Stats
        )
    }
}


