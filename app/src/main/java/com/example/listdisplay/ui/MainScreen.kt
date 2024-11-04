package com.example.listdisplay.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listdisplay.MainViewModel

@Composable
fun MainScreen() {
    val navController = remember { mutableStateOf(0) }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedItem = navController.value,
                onItemSelected = { navController.value = it }
            )
        }
    ) { innerPadding ->
        when (navController.value) {
            0 -> HomeScreen()
            1 -> SearchScreen()
        }
    }
}

@Composable
fun BottomNavigationBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    BottomNavigation(
        backgroundColor = Color.White,
        contentColor = Color.Blue,
        elevation = 8.dp
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = null) },
            label = { Text("home") },
            selected = selectedItem == 0,
            onClick = { onItemSelected(0) }
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = null) },
            label = { Text("search") },
            selected = selectedItem == 1,
            onClick = { onItemSelected(1) }
        )
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    ListScreen(state)
}

@Composable
fun SearchScreen(viewModel: MainViewModel = hiltViewModel() ){
    val text by viewModel.searchText.collectAsState()
    val state by viewModel.searchState.collectAsState()
    var inputText by remember { mutableStateOf(text) }

    // Use LaunchedEffect to perform the search with a delay to debounce
    LaunchedEffect(inputText) {
        // Add a short delay to debounce the search (e.g., 500 milliseconds)
        kotlinx.coroutines.delay(500)
        if (inputText.isNotEmpty()) {
            viewModel.search(inputText)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter text here") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
                viewModel.search(inputText)
                 },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("search")
        }

        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp)) // Optional spacer before the list
            ListScreen(state)
        }
    }
}

