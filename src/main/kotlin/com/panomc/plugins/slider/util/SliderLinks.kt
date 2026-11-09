package com.panomc.plugins.slider.util

import com.panomc.platform.error.BadRequest

/** Slide links: empty, an http(s) address, a path on the site (`/store`) or an anchor (`#news`). */
object SliderLinks {
    private val SAFE_LINK = Regex("""^(https?://\S+|/(?![/\\])\S*|#\S*)$""", RegexOption.IGNORE_CASE)

    fun isSafe(link: String): Boolean = link.isEmpty() || SAFE_LINK.matches(link)

    /** The trimmed link, or [BadRequest] when it is something else (for example a `javascript:` address). */
    fun checked(raw: String?): String {
        val link = raw?.trim() ?: ""
        if (!isSafe(link)) throw BadRequest()
        return link
    }
}
