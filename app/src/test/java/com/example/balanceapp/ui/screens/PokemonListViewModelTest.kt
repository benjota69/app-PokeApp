package com.example.balanceapp.ui.screens

import com.example.balanceapp.data.PokemonRepository
import com.example.balanceapp.data.remote.NamedResource
import com.example.balanceapp.data.remote.PokeApiService
import com.example.balanceapp.data.remote.PokemonDetailResponse
import com.example.balanceapp.data.remote.PokemonItem
import com.example.balanceapp.data.remote.PokemonListResponse
import com.example.balanceapp.data.remote.PokemonResult
import com.example.balanceapp.data.remote.PokemonStat
import com.example.balanceapp.data.remote.PokemonTypeSlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests unitarios para [PokemonListViewModel] usando un [PokemonRepository]
 * conectado a un [PokeApiService] de prueba (sin red real).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private class FakePokeApiService : PokeApiService {
        override suspend fun getPokemonList(limit: Int, offset: Int): PokemonListResponse {
            return PokemonListResponse(
                count = 2,
                results = listOf(
                    PokemonResult(name = "bulbasaur", url = "https://pokeapi.co/api/v2/pokemon/1/"),
                    PokemonResult(name = "ivysaur", url = "https://pokeapi.co/api/v2/pokemon/2/")
                )
            )
        }

        override suspend fun getPokemonDetail(id: Int): PokemonDetailResponse {
            return PokemonDetailResponse(
                id = id,
                name = if (id == 1) "bulbasaur" else "ivysaur",
                height = 10,
                weight = 100,
                types = listOf(PokemonTypeSlot(NamedResource("grass"))),
                stats = listOf(PokemonStat(45, NamedResource("hp")))
            )
        }
    }

    private lateinit var viewModel: PokemonListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        val repo = PokemonRepository(api = FakePokeApiService())
        viewModel = PokemonListViewModel(repositorio = repo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargar llena el estado con lista de pokemones y desactiva cargando`() = runTest {
        // Avanzamos las corrutinas que se lanzan en init { cargar() }
        dispatcher.scheduler.advanceUntilIdle()

        val estado = viewModel.estado.value
        assertEquals(false, estado.cargando)
        assertEquals(2, estado.pokemones.size)
        assertEquals("Bulbasaur", estado.pokemones[0].name)
    }
}



