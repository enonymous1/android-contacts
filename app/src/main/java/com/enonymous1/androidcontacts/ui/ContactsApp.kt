package com.enonymous1.androidcontacts.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.enonymous1.androidcontacts.PeopleViewModel

/**
 * One activity, three destinations. The original used three activities and
 * three fragments, each hosting a WebView that talked to Android through a
 * JavaScript bridge; here every screen reads the same ViewModel, so the
 * list on screen is always the list in the database.
 */
@Composable
fun ContactsApp(viewModel: PeopleViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "list",
        modifier = modifier,
    ) {
        composable("list") {
            val people by viewModel.people.collectAsState()
            ListScreen(
                people = people,
                onAddPerson = { navController.navigate("form") },
                onPersonClick = { person -> navController.navigate("details/${person.id}") },
            )
        }
        composable("form") {
            FormScreen(
                viewModel = viewModel,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable(
            route = "details/{personId}",
            arguments = listOf(navArgument("personId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getLong("personId") ?: 0L
            DetailsScreen(
                viewModel = viewModel,
                personId = personId,
                onBack = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() },
            )
        }
    }
}
