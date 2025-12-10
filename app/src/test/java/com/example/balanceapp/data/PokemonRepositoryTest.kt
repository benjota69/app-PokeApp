package com.example.balanceapp.data

import com.example.balanceapp.data.remote.NamedResource
import com.example.balanceapp.data.remote.PokeApiService
import com.example.balanceapp.data.remote.PokemonDetailResponse
import com.example.balanceapp.data.remote.PokemonListResponse
import com.example.balanceapp.data.remote.PokemonResult
import com.example.balanceapp.data.remote.PokemonStat
import com.example.balanceapp.data.remote.PokemonTypeSlot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests unitarios para [PokemonRepository] usando una implementación
 * simple de prueba de [PokeApiService] (sin llamadas reales a red).
 */
class PokemonRepositoryTest {

    private class FakePokeApiService : PokeApiService {
        override suspend fun getPokemonList(limit: Int, offset: Int): PokemonListResponse {
            // Lista pequeña de prueba con 2 pokémon
            return PokemonListResponse(
                count = 2,
                results = listOf(
                    PokemonResult(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/"),
                    PokemonResult(name = "charmander", url = "https://pokeapi.co/api/v2/pokemon/4/")
                )
            )
        }

        override suspend fun getPokemonDetail(id: Int): PokemonDetailResponse {
            return when (id) {
                1 -> PokemonDetailResponse(
                    id = 1,
                    name = "bulbasaur",
                    height = 7,
                    weight = 69,
                    types = listOf(
                        PokemonTypeSlot(NamedResource("grass")),
                        PokemonTypeSlot(NamedResource("poison"))
                    ),
                    stats = listOf(
                        PokemonStat(base_stat = 45, stat = NamedResource("hp")),
                        PokemonStat(base_stat = 49, stat = NamedResource("attack"))
                    )
                )

                4 -> PokemonDetailResponse(
                    id = 4,
                    name = "charmander",
                    height = 6,
                    weight = 85,
                    types = listOf(PokemonTypeSlot(NamedResource("fire"))),
                    stats = listOf(
                        PokemonStat(base_stat = 39, stat = NamedResource("hp")),
                        PokemonStat(base_stat = 52, stat = NamedResource("attack"))
                    )
                )

                else -> error("Id no soportado en FakePokeApiService")
            }
        }
    }

    private val repository = PokemonRepository(api = FakePokeApiService())

    @Test
    fun `obtenerListaPokemon adapta correctamente id nombre y tipos`() = runBlocking {
        val lista = repository.obtenerListaPokemon(limit = 2)

        // Deberíamos tener 2 elementos
        assertEquals(2, lista.size)

        val bulbasaur = lista.first()
        assertEquals(1, bulbasaur.id)
        // el repositorio capitaliza el nombre
        assertEquals("Bulbasaur", bulbasaur.name)
        // la url de imagen se arma con el id
        assertEquals(
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png",
            bulbasaur.imageUrl
        )
        // tipos traducidos al español
        assertEquals(listOf("Planta", "Veneno"), bulbasaur.types)
    }

    @Test
    fun `obtenerDetallePokemon traduce tipos y stats`() = runBlocking {
        val detail = repository.obtenerDetallePokemon(1)

        assertEquals(1, detail.id)
        assertEquals("Bulbasaur", detail.name)
        assertEquals(listOf("Planta", "Veneno"), detail.types)

        // Stats traducidas (hp -> PS, attack -> Ataque)
        val expectedStatsNames = listOf("PS", "Ataque")
        assertEquals(expectedStatsNames, detail.stats.map { it.first })
    }
}


