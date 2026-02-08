
package com.example.recettes.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.recettes.R
import com.example.recettes.data.Ingredient
import com.example.recettes.data.Recipe

@Composable
fun HomeScreen(recipes: List<Recipe>, onOpen: (Int)->Unit, onOpenList:()->Unit) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, recipes) {
        if (query.isBlank()) recipes else recipes.filter { it.title.contains(query, ignoreCase = true) }
    }
    Scaffold(topBar = {
        TopAppBar(title = { Text("Recettes") }, actions = {
            TextButton(onClick = onOpenList) { Text(stringResource(id = R.string.open_list)) }
        })
    }) { pad ->
        Column(Modifier.padding(pad)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(id = R.string.search_hint)) },
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            )
            LazyColumn(Modifier.fillMaxSize()) {
                itemsIndexed(filtered) { idx, r ->
                    RecipeRow(index = idx, recipe = r, onClick = { onOpen(idx) })
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun RecipeRow(index: Int, recipe: Recipe, onClick: ()->Unit) {
    val ctx = LocalContext.current
    val base = sanitizeTitleToFilename(recipe.title)
    val assetPath = remember { assetImageExists(ctx, base) }
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(12.dp)) {
        Box(Modifier.size(96.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
            if (assetPath != null) {
                AsyncImage(model = assetPath, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Image(painterResource(id = R.drawable.placeholder), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f).align(Alignment.CenterVertically)) {
            Text(recipe.title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${recipe.ingredients.size} ingr. • ${recipe.servings} portion(s)", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun RecipeScreen(recipe: Recipe, onAddToList: (Int)->Unit) {
    var servings by remember { mutableIntStateOf(maxOf(1, recipe.servings)) }
    val factor = servings.toDouble() / maxOf(1, recipe.servings)
    Scaffold(topBar = { TopAppBar(title = { Text(recipe.title) }) }) { pad ->
        LazyColumn(Modifier.padding(pad)) {
            item { HeaderImage(title = recipe.title) }
            item {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(id = R.string.servings), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = servings.toString(),
                        onValueChange = { v -> v.toIntOrNull()?.let { servings = it.coerceIn(1, 50) } },
                        modifier = Modifier.width(96.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    Button(onClick = { onAddToList(servings) }) { Text(stringResource(id = R.string.add_to_list)) }
                }
            }
            item { SectionTitle(stringResource(id = R.string.ingredients)) }
            items(recipe.ingredients.size) { i ->
                val ing = recipe.ingredients[i]
                IngredientRow(scale(ing, factor))
                Divider()
            }
            item { SectionTitle(stringResource(id = R.string.steps)) }
            items(recipe.steps.size) { i ->
                Text("${i+1}. ${recipe.steps[i].trim()}", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun HeaderImage(title: String) {
    val ctx = LocalContext.current
    val base = sanitizeTitleToFilename(title)
    val assetPath = remember { assetImageExists(ctx, base) }
    if (assetPath != null) {
        AsyncImage(model = assetPath, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(220.dp))
    } else {
        Image(painterResource(id = R.drawable.placeholder), contentDescription = null, modifier = Modifier.fillMaxWidth().height(220.dp), contentScale = ContentScale.Crop)
    }
}

@Composable
private fun SectionTitle(text: String) { Text(text, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium) }

private fun scale(ing: Ingredient, factor: Double): Ingredient {
    return ing.copy(
        quantity = ing.quantity?.times(factor),
        grams = ing.grams?.times(factor)
    )
}

@Composable
private fun IngredientRow(ing: Ingredient) {
    val qty = ing.quantity?.let { fmt(it) + (if (!ing.unit.isNullOrBlank()) " " + ing.unit else "") }
    val g = ing.grams?.let { " (~" + fmt(it) + " g)" } ?: ""
    val label = buildString {
        append(ing.name)
        if (!qty.isNullOrBlank()) append(": ").append(qty)
        append(g)
    }
    Text(label, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
}

private fun fmt(x: Double): String {
    val r = kotlin.math.round(x * 100.0) / 100.0
    return if (r % 1.0 == 0.0) r.toInt().toString() else r.toString()
}
