
package com.example.recettes.data

import kotlinx.serialization.Serializable

@Serializable
data class Ingredient(
    val raw: String? = null,
    val name: String,
    val quantity: Double? = null,
    val unit: String? = null,
    val grams: Double? = null
)

@Serializable
data class Recipe(
    val title: String,
    val steps: List<String> = emptyList(),
    val step_count: Int? = null,
    val ingredients: List<Ingredient> = emptyList(),
    val servings: Int = 1
)

@Serializable
data class RecipeBook(
    val recipes: List<Recipe> = emptyList()
)
