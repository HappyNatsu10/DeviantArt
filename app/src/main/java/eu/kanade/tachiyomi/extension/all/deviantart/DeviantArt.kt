package eu.kanade.tachiyomi.extension.all.deviantart

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.HttpSource
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.util.concurrent.TimeUnit

class DeviantArt : HttpSource() {

    override val name = "DeviantArt"

    override val baseUrl = "https://www.deviantart.com"

    override val lang = "all"

    override val supportsLatest = true

    override val id: Long = 6512398401928374152L

    override val client: OkHttpClient = network.client.newBuilder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun headersBuilder(): Headers.Builder = Headers.Builder()
        .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36")
        .add("Referer", "$baseUrl/")

    // ============================== POPULAR ==============================

    override fun popularMangaRequest(page: Int): Request {
        val offset = (page - 1) * 60
        val url = "https://backend.deviantart.com/rss.xml".toHttpUrl().newBuilder()
            .addQueryParameter("type", "deviation")
            .addQueryParameter("order", "9") // Popular
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("limit", "60")
            .build()

        return GET(url.toString(), headers)
    }

    override fun popularMangaParse(response: Response): MangasPage {
        return parseRssResponse(response)
    }

    // ============================== LATEST ==============================

    override fun latestUpdatesRequest(page: Int): Request {
        val offset = (page - 1) * 60
        val url = "https://backend.deviantart.com/rss.xml".toHttpUrl().newBuilder()
            .addQueryParameter("type", "deviation")
            .addQueryParameter("order", "5") // Latest
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("limit", "60")
            .build()

        return GET(url.toString(), headers)
    }

    override fun latestUpdatesParse(response: Response): MangasPage {
        return parseRssResponse(response)
    }

    // ============================== SEARCH ==============================

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val offset = (page - 1) * 60
        var order = "9"
        var categoryQuery = ""
        var authorQuery = ""
        var tagQuery = ""

        filters.forEach { filter ->
            when (filter) {
                is DeviantArtFilters.OrderFilter -> order = filter.toOrderValue()
                is DeviantArtFilters.CategoryFilter -> categoryQuery = filter.toCategoryQuery()
                is DeviantArtFilters.AuthorFilter -> authorQuery = filter.state.trim()
                is DeviantArtFilters.TagFilter -> tagQuery = filter.state.trim()
                else -> {}
            }
        }

        val queryParts = mutableListOf<String>()

        // Process query string directly if user typed @username or by:username
        val cleanQuery = query.trim()
        if (cleanQuery.isNotEmpty()) {
            if (cleanQuery.startsWith("@")) {
                queryParts.add("by:${cleanQuery.substring(1)}")
            } else if (cleanQuery.startsWith("by:") || cleanQuery.startsWith("gallery:")) {
                queryParts.add(cleanQuery)
            } else {
                queryParts.add(cleanQuery)
            }
        }

        if (authorQuery.isNotEmpty()) {
            queryParts.add("by:$authorQuery")
        }

        if (tagQuery.isNotEmpty()) {
            queryParts.add(tagQuery)
        }

        if (categoryQuery.isNotEmpty()) {
            queryParts.add(categoryQuery)
        }

        val finalQuery = if (queryParts.isNotEmpty()) queryParts.joinToString(" ") else "wallpaper"

        val url = "https://backend.deviantart.com/rss.xml".toHttpUrl().newBuilder()
            .addQueryParameter("type", "deviation")
            .addQueryParameter("q", finalQuery)
            .addQueryParameter("order", order)
            .addQueryParameter("offset", offset.toString())
            .addQueryParameter("limit", "60")
            .build()

        return GET(url.toString(), headers)
    }

    override fun searchMangaParse(response: Response): MangasPage {
        return parseRssResponse(response)
    }

    // ============================== RSS PARSER ==============================

    private fun parseRssResponse(response: Response): MangasPage {
        val xmlBody = response.body.string()
        val doc = Jsoup.parse(xmlBody, "", Parser.xmlParser())
        val items = doc.select("item")

        val mangas = items.mapNotNull { item ->
            val title = item.selectFirst("title")?.text() ?: item.selectFirst("media|title")?.text()
            val link = item.selectFirst("link")?.text() ?: item.selectFirst("guid")?.text()
            if (title.isNullOrEmpty() || link.isNullOrEmpty()) return@mapNotNull null

            val author = item.selectFirst("media|credit[role=author]")?.text()
                ?: item.selectFirst("dc|creator")?.text()
                ?: "DeviantArt Artist"

            val thumbnail = item.selectFirst("media|thumbnail")?.attr("url")
                ?: item.selectFirst("media|content")?.attr("url")
                ?: ""

            val highResThumbnail = DeviantArtUtils.getHighResImageUrl(thumbnail)

            val rawDescription = item.selectFirst("media|description")?.text()
                ?: item.selectFirst("description")?.text()
                ?: ""

            val cleanDesc = DeviantArtUtils.cleanHtmlDescription(rawDescription)
            val keywords = item.selectFirst("media|keywords")?.text() ?: ""

            SManga.create().apply {
                url = link
                this.title = title
                this.artist = author
                this.author = author
                this.thumbnail_url = highResThumbnail
                this.description = cleanDesc
                this.genre = keywords.ifEmpty { "Art, Illustration" }
                this.status = SManga.COMPLETED
                this.initialized = true
            }
        }

        return MangasPage(mangas, mangas.size >= 60)
    }

    // ============================== MANGA DETAILS ==============================

    override fun mangaDetailsRequest(manga: SManga): Request {
        val oembedUrl = "https://backend.deviantart.com/oembed".toHttpUrl().newBuilder()
            .addQueryParameter("url", manga.url)
            .build()

        return GET(oembedUrl.toString(), headers)
    }

    override fun mangaDetailsParse(response: Response): SManga {
        val responseBody = response.body.string()
        return try {
            val dto = json.decodeFromString<DeviantArtOEmbedDto>(responseBody)
            SManga.create().apply {
                url = dto.url ?: ""
                title = dto.title ?: "Untitled Artwork"
                artist = dto.authorName ?: "DeviantArt Artist"
                author = dto.authorName ?: "DeviantArt Artist"
                thumbnail_url = DeviantArtUtils.getHighResImageUrl(dto.url ?: dto.thumbnailUrl)
                genre = dto.tags ?: "Art"
                description = "Title: ${dto.title}\nArtist: ${dto.authorName}\nSafety: ${dto.safety ?: "nonadult"}\nUploaded: ${dto.pubdate ?: "Unknown"}"
                status = SManga.COMPLETED
                initialized = true
            }
        } catch (e: Exception) {
            SManga.create().apply {
                status = SManga.COMPLETED
                initialized = true
            }
        }
    }

    // ============================== CHAPTER LIST ==============================

    override fun chapterListRequest(manga: SManga): Request {
        return mangaDetailsRequest(manga)
    }

    override fun chapterListParse(response: Response): List<SChapter> {
        val mangaDetails = mangaDetailsParse(response)
        val chapter = SChapter.create().apply {
            url = response.request.url.queryParameter("url") ?: response.request.url.toString()
            name = mangaDetails.title.ifEmpty { "Full Artwork" }
            chapter_number = 1f
            date_upload = System.currentTimeMillis()
        }
        return listOf(chapter)
    }

    // ============================== PAGE LIST ==============================

    override fun pageListRequest(chapter: SChapter): Request {
        val oembedUrl = "https://backend.deviantart.com/oembed".toHttpUrl().newBuilder()
            .addQueryParameter("url", chapter.url)
            .build()

        return GET(oembedUrl.toString(), headers)
    }

    override fun pageListParse(response: Response): List<Page> {
        val responseBody = response.body.string()
        val pages = mutableListOf<Page>()

        try {
            val dto = json.decodeFromString<DeviantArtOEmbedDto>(responseBody)
            val fullImageUrl = dto.url ?: dto.thumbnailUrl
            if (!fullImageUrl.isNullOrEmpty()) {
                val highResUrl = DeviantArtUtils.getHighResImageUrl(fullImageUrl)
                pages.add(Page(0, "", highResUrl))
            }
        } catch (e: Exception) {
            // Fallback: request web page HTML
        }

        if (pages.isEmpty()) {
            val chapterUrl = response.request.url.queryParameter("url") ?: chapterUrlFromResponse(response)
            if (chapterUrl.isNotEmpty()) {
                val htmlResponse = client.newCall(GET(chapterUrl, headers)).execute()
                val html = htmlResponse.body.string()
                val doc = Jsoup.parse(html)

                val metaImage = doc.selectFirst("meta[property=og:image]")?.attr("content")
                    ?: doc.selectFirst("meta[name=twitter:image]")?.attr("content")

                if (!metaImage.isNullOrEmpty()) {
                    pages.add(Page(0, "", DeviantArtUtils.getHighResImageUrl(metaImage)))
                }
            }
        }

        return pages
    }

    private fun chapterUrlFromResponse(response: Response): String {
        return response.request.url.toString()
    }

    override fun imageUrlParse(response: Response): String {
        throw UnsupportedOperationException("Not used")
    }

    override fun getFilterList(): FilterList = DeviantArtFilters.getFilterList()
}
