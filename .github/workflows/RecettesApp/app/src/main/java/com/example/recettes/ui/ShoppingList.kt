
package com.example.recettes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.recettes.data.Recipe

data class Item(val name: String, val unit: String?, val quantity: Double?)

@Composable
fun ShoppingListScreen(recipes: List<Recipe>, selections: List<Pair<Int,Int>>, onClear: ()->Unit) {
    val aggregated = remember(selections) {
        aggregate(recipes, selections)
    }
    Scaffold(topBar = { TopAppBar(title = { Text("Liste de courses (${selections.size} sélection(s))") }, actions = {
        TextButton(onClick = onClear) { Text("Vider") }
    }) }) { pad ->
        if (aggregated.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize()) { Text("Votre liste de courses est vide", modifier = Modifier.padding(16.dp)) }
        } else {
            LazyColumn(Modifier.padding(pad)) {
                items(aggregated) { item ->
                    ListItem(headlineContent = { Text(itemLabel(item)) })
                    Divider()
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

private fun itemLabel(i: Item): String {
    val qty = i.quantity?.let { fmt(it) } ?: ""
    val unit = i.unit ?: ""
    val space = if (qty.isNotBlank() && unit.isNotBlank()) " " else ""
    return listOfNotNull(i.name, if (qty.isNotBlank() || unit.isNotBlank()) ": $qty$space$unit" else null).joinToString("")
}

private fun aggregate(recipes: List<Recipe>, selections: List<Pair<Int,Int>>): List<Item> {
    val map = linkedMapOf<String, MutableMap<String?, Double>>() // name -> unit -> qty
    for ((idx, servings) in selections) {
        val r = recipes.getOrNull(idx) ?: continue
        val factor = servings.toDouble() / maxOf(1, r.servings)
        for (ing in r.ingredients) {
            val key = ing.name.trim().lowercase()
            val unit = ing.unit?.trim()
            val qty = (ing.quantity ?: ing.grams)?.times(factor)
            if (qty != null) {
                val byUnit = map.getOrPut(key) { mutableMapOf() }
                byUnit[unit] = (byUnit[unit] ?: 0.0) + qty
            } else {
                val byUnit = map.getOrPut(key) { mutableMapOf() }
                byUnit[unit] = (byUnit[unit] ?: 0.0)
            }
        }
    }
    val out = mutableListOf<Item>()
    for ((nameKey, units) in map) {
        for ((unit, qty) in units) {
            out.add(Item(name = nameKey.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }, unit = unit, quantity = qty))
        }
    }
    return out.sortedBy { it.name }
}

private fun fmt(x: Double): String {
    val r = kotlin.math.round(x * 100.0) / 100.0
    return if (r % 1.0 == 0.0) r.toInt().toString() else r.toString()
}
