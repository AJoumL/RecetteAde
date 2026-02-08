
package com.example.recettes.data

import android.content.Context
import kotlinx.serialization.json.Json

object RecipeRepository {
    private val json = Json { ignoreUnknownKeys = true }

    fun load(context: Context): List<Recipe> {
        val assetName = "recettes_fusionnees.json"
        val text = context.assets.open(assetName).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val book = json.decodeFromString(RecipeBook.serializer(), text)
        return book.recipes
    }
}
