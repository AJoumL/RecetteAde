
package com.example.recettes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.example.recettes.data.RecipeRepository
import com.example.recettes.ui.HomeScreen
import com.example.recettes.ui.RecipeScreen
import com.example.recettes.ui.ShoppingListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface { AppNav() }
            }
        }
    }
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val recipes = remember { RecipeRepository.load(ctx) }
    var listSelections by remember { mutableStateOf(mutableListOf<Pair<Int,Int>>()) } // pair(index, servings)

    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(recipes = recipes, onOpen = { idx -> nav.navigate("detail/$idx") }, onOpenList = { nav.navigate("list") })
        }
        composable(
            route = "detail/{index}",
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val idx = backStackEntry.arguments?.getInt("index") ?: 0
            val recipe = recipes.getOrNull(idx)
            if (recipe != null) {
                RecipeScreen(recipe = recipe) { servings ->
                    listSelections.add(idx to servings)
                }
            }
        }
        composable("list") {
            ShoppingListScreen(recipes = recipes, selections = listSelections) {
                listSelections = mutableListOf()
                nav.popBackStack()
            }
        }
    }
}
