package com.example.listdisplay.ui

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.listdisplay.data.Result
import com.example.listdisplay.data.model.AnimData
import com.example.listdisplay.data.source.local.MyData

const val TAG="EFFECT"

@Composable
fun ListScreen(state: Result<List<MyData>>) {

    Box(modifier = Modifier.fillMaxSize()){
        when (state) {
            is Result.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is Result.Success -> {
                // This will run after recomposition
                SideEffect {
                    Log.d(TAG, "SideEffect")
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Display 2 items per row
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.data) { item ->
                        UserItem(item,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
            is Result.Error -> {
                Text(text = "Error: ${state.message}")
            }
        }
    }

    // 4. Using DisposableEffect to clean up resources when the composable leaves the composition
    DisposableEffect(Unit) {
        Log.d(TAG,"Screen entered composition")
        onDispose {
            Log.d(TAG,"Screen left composition - cleanup performed")
            // Perform cleanup operations, such as removing listeners or canceling coroutines
        }
    }
}


@Composable
fun UserItem(data: MyData, modifier: Modifier = Modifier){
    Column (modifier = modifier
        //.wrapContentHeight()
        .height(300.dp) // Set a fixed height for each item
        .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = data.imageUrl,
            contentDescription = null,
            Modifier
                .size(200.dp)
                .padding(8.dp),

        )
        Text(text = data.title, fontSize = 20.sp,
            minLines = 3,
            maxLines = 3, // Restrict to a single line
            overflow = TextOverflow.Ellipsis,
//            modifier = Modifier
//                .wrapContentHeight() // Equivalent to android:layout_height="wrap_content" /
           )

    }
}