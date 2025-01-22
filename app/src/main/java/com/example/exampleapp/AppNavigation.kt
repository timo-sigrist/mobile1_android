package com.example.exampleapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {

    //val model = viewModel<AppViewModel>() -> To pass a model down

    NavHost(navController = navController,
        startDestination = "greeting",
        modifier = modifier) {

        composable("greeting") {
            Greeting(modifier = modifier ,
                navController = navController
               // model: model -> pass model down and init it
            )
        }

        composable("viewmodelExample") {
            ViewModelExample(modifier = modifier,
                onNavigateBack = { navController.navigate("greeting") }
            )
        }

        composable("someView") {
            SomeView(modifier = modifier,
                navController = navController
            )
        }
    }
}
