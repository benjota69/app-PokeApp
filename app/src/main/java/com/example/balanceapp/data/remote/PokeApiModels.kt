    package com.example.balanceapp.data.remote

    // respuesta cuando la API devuelve una lista de Pokemon
    data class PokemonListResponse(
        val count: Int,   // Cantidad total de Pokemones en la API y en la lista.
        val results: List<PokemonResult> // Lista básica de pokemones (name + url)
    )

    data class PokemonResult( // representa cada pokemon dentro de la lista
        val name: String, // Nombre del pokemon
        val url: String // URL con más detalles del pokemon, contiene el ID
    )

    // Modelo propio de la APP para mostrar la LISTA
    data class PokemonItem(
        val id: Int, // ID del Pokémon (extraído desde la URL)
        val name: String, // Nombre del pokemon
    val imageUrl: String, // URL a la imagen del respectivo pokemon en la API
    val types: List<String> = emptyList() // Tipos del Pokémon para filtros (ej: Fuego, Agua)
    )

    // Descripción de pokemones
    data class PokemonDetailResponse(
        val id: Int,
        val name: String,
        val height: Int, // Altura de Pokemon
        val weight: Int, // Peso de Pokemon
        val types: List<PokemonTypeSlot>, //Tipos de pokemon
        val stats: List<PokemonStat> // Stats de pokemon
    )

    // Representa un tipo dentro del detalle
    data class PokemonTypeSlot( // nombre del tipo de pokemon
        val type: NamedResource // Solo contiene un nombre (ej: "fuego")
    )

    // Representa cada stat base
    data class PokemonStat( //
        val base_stat: Int, // Valor numérico
        val stat: NamedResource // Nombre de la estadística
    )

    data class NamedResource(
        val name: String
    )

    // Modelo propio de la APP para mostrar DETALLE
    data class PokemonDetail( // modelo para dialogo de detalle
        val id: Int,
        val name: String,
        val imageUrl: String,
        val height: Int,
        val weight: Int,
        val types: List<String>, // Nombres limpios de tipos
        val stats: List<Pair<String, Int>> // Lista (nombreStat, valor)
    )

