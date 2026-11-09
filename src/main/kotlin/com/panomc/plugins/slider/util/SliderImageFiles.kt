package com.panomc.plugins.slider.util

import io.vertx.ext.web.RoutingContext
import java.io.File

/**
 * Serving of stored slide images. A requested name is a single plain file name inside the uploads folder; anything
 * else (separators, `..`, odd characters) is treated as not found, so a request can never read a file outside it.
 */
object SliderImageFiles {
    private const val CACHE_TTL_SECONDS = 7 * 24 * 60 * 60 // 1 week

    private val SAFE_NAME = Regex("^[A-Za-z0-9][A-Za-z0-9._-]{0,254}$")

    fun isSafeName(name: String?): Boolean = name != null && SAFE_NAME.matches(name) && !name.contains("..")

    /** [name] inside [dir], or null when it is not a plain file name or would leave [dir]. */
    fun resolveInside(dir: File, name: String?): File? {
        if (!isSafeName(name)) return null
        val base = dir.toPath().toAbsolutePath().normalize()
        val target = base.resolve(name!!).normalize()
        if (target.parent != base) return null
        return target.toFile()
    }

    fun thumbnailsDir(uploadsDir: File) = File(uploadsDir, "thumbnails")

    fun serve(context: RoutingContext, uploadsDir: File, fileName: String, thumbnail: Boolean) {
        val original = resolveInside(uploadsDir, fileName)
        if (original == null || !original.isFile) {
            context.response().setStatusCode(404).end()
            return
        }

        val file = if (thumbnail) {
            val thumbnailsDir = thumbnailsDir(uploadsDir)
            ImageUtil.generateThumbnail(original, thumbnailsDir)
            resolveInside(thumbnailsDir, fileName)?.takeIf { it.isFile } ?: original
        } else original

        val response = context.response()
        response.putHeader("Content-Type", contentType(file.name))
        response.putHeader("ETag", "\"$fileName\"")
        response.putHeader("Cache-Control", "public, max-age=$CACHE_TTL_SECONDS, immutable")

        try {
            response.sendFile(file.absolutePath)
        } catch (_: Exception) {
        }
    }

    private fun contentType(name: String): String = com.panomc.platform.util.MimeTypeUtil.getMimeTypeFromFileName(name)
}
