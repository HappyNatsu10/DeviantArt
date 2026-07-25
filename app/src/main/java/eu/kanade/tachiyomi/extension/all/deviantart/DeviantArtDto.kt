package eu.kanade.tachiyomi.extension.all.deviantart

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviantArtOEmbedDto(
    val version: String? = null,
    val type: String? = null,
    val title: String? = null,
    val url: String? = null,
    @SerialName("author_name") val authorName: String? = null,
    @SerialName("author_url") val authorUrl: String? = null,
    val provider_name: String? = null,
    val safety: String? = null,
    val pubdate: String? = null,
    val tags: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null
)

data class DeviantArtRssItem(
    val title: String,
    val link: String,
    val pubDate: String,
    val author: String,
    val description: String,
    val thumbnailUrl: String,
    val mediaUrl: String,
    val keywords: String,
    val rating: String
)
