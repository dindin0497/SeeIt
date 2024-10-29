

package com.example.listdisplay.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.listdisplay.data.model.AnimData

@Entity(tableName = "Anime")
data class MyData(
    @PrimaryKey val id: Int,
    var title: String,
    var imageUrl: String
)

fun List<AnimData>.toLocal() = map {
    MyData(
        id = it.mal_id,
        imageUrl = it.images?.jpg?.image_url ?: "",
        title = it.title
    )
}
