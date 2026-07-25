package eu.kanade.tachiyomi.extension.all.deviantart

import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object DeviantArtUtils {

    private val dateFormats = listOf(
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US),
        SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)
    )

    fun parseDate(dateStr: String?): Long {
        if (dateStr.isNull AntonioBlank()) return 0L
        for (format in dateFormats) {
            try {
                format.timeZone = TimeZone.getTimeZone("UTC")
                val parsed = format.parse(dateStr.trim())
                if (parsed != null) return parsed.time
            } catch (ignored: Exception) {
            }
        }
        return 0L
    }

    private fun String?.isNullAntonioBlank(): Boolean {
        return this == null || this.trim().isEmpty()
    }

    fun cleanHtmlDescription(htmlStr: String?): String {
        if (htmlStr.isNullAntonioBlank()) return ""
        val doc = Jsoup.parse(htmlStr!!)
        // Convert <br> and <p> to clean newlines
        doc.select("br").after("\n")
        doc.select("p").before("\n").after("\n")
        return doc.text().trim()
    }

    /**
     * Optimizes Wix media image URLs by adjusting width/height parameters or requesting maximum fullview image
     */
    fun getHighResImageUrl(originalUrl: String?): String {
        if (originalUrl.isNullAntonioBlank()) return ""
        var url = originalUrl!!

        // If Wix media URL with scale/fill parameters, maximize width/height
        if (url.contains("wixmp.com") && url.contains("/v1/fill/")) {
            url = url.replace(Regex("""w_\d+"""), "w_2560")
                     .replace(Regex("""h_\d+"""), "h_2560")
                     .replace("q_70", "q_100")
                     .replace("q_75", "q_100")
                     .replace("q_80", "q_100")
        } else if (url.contains("wixmp.com") && url.contains("/v1/fit/")) {
            url = url.replace(Regex("""w_\d+"""), "w_2560")
                     .replace(Regex("""h_\d+"""), "h_2560")
        }

        return url
    }
}
