package com.example.buildnote

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {

    //val model = viewModel<AppViewModel>() -> To pass a model down

    NavHost(navController = navController,
        startDestination = "someView", // hier ist die start-seite
        modifier = modifier) {

        /** DIESER BLOCK MUSS PRO Componente .kt file erstellt werden*/
        composable("someView") { // "someView ist der Name, mit der Navigiert
            SomeView(modifier = modifier,
                navController = navController
            )
        }
        /** BIS HIER **/

    }
}
