package eu.kanade.tachiyomi.extension.all.deviantart

import eu.kanade.tachiyomi.source.model.Filter
import eu.kanade.tachiyomi.source.model.FilterList

object DeviantArtFilters {

    class OrderFilter : Filter.Select<String>(
        "Sort Order",
        arrayOf("Popular", "Latest Updates", "All-Time Popular")
    ) {
        fun toOrderValue(): String = when (state) {
            0 -> "9" // Popular
            1 -> "5" // Latest
            2 -> "11" // All-Time
            else -> "9"
        }
    }

    class AuthorFilter : Filter.Text("Artist / User (e.g. username)")

    class TagFilter : Filter.Text("Tag / Keyword (e.g. landscape, wallpaper)")

    class CategoryFilter : Filter.Select<String>(
        "Category",
        arrayOf(
            "All Categories",
            "Digital Art",
            "Traditional Art",
            "Photography",
            "Anime & Manga",
            "Customization & Wallpapers",
            "Fan Art",
            "3D Art"
        )
    ) {
        fun toCategoryQuery(): String = when (state) {
            1 -> "digitalart"
            2 -> "traditional"
            3 -> "photography"
            4 -> "manga"
            5 -> "wallpaper"
            6 -> "fanart"
            7 -> "3d"
            else -> ""
        }
    }

    fun getFilterList(): FilterList = FilterList(
        OrderFilter(),
        AuthorFilter(),
        TagFilter(),
        CategoryFilter()
    )
}
