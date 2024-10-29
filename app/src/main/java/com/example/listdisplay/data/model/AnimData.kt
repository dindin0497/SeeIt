package com.example.listdisplay.data.model

data class AnimResponse(
   val data: List<AnimData>,
)

data class AnimData(
    val mal_id: Int,
    val title: String,
    val images: Images?=null
)

data class Images(
    val jpg: ImageDetails?=null,
    val webp: ImageDetails?=null
)

data class ImageDetails(
    val image_url: String? = null,
    val small_image_url: String? = null,
    val large_image_url: String? = null
)
