package com.panomc.plugins.slider.util

import com.panomc.platform.error.BadRequest
import io.vertx.ext.web.FileUpload
import io.vertx.ext.web.RoutingContext
import java.io.File
import java.util.UUID

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
        val contentType = IMAGE_TYPES[file.extension.lowercase()]
        if (contentType != null) {
            response.putHeader("Content-Type", contentType)
        } else {
            // Files stored by older versions with a non-image extension: never let the browser render them.
            response.putHeader("Content-Type", "application/octet-stream")
            response.putHeader("Content-Disposition", "attachment")
        }
        response.putHeader("X-Content-Type-Options", "nosniff")
        response.putHeader("Content-Security-Policy", "default-src 'none'; sandbox")
        response.putHeader("ETag", "\"$fileName\"")
        response.putHeader("Cache-Control", "public, max-age=$CACHE_TTL_SECONDS, immutable")

        try {
            response.sendFile(file.absolutePath)
        } catch (_: Exception) {
        }
    }

    private val IMAGE_TYPES = mapOf(
        "png" to "image/png",
        "jpg" to "image/jpeg",
        "jpeg" to "image/jpeg",
        "gif" to "image/gif",
        "webp" to "image/webp"
    )

    /** The image format of [file] read from its first bytes (png, jpg, gif or webp), or null when it is no such image. */
    fun detectImageExtension(file: File): String? {
        val head = ByteArray(12)
        val read = file.inputStream().use { input ->
            var total = 0
            while (total < head.size) {
                val n = input.read(head, total, head.size - total)
                if (n < 0) break
                total += n
            }
            total
        }
        fun at(offset: Int, vararg bytes: Int) =
            read >= offset + bytes.size && bytes.indices.all { head[offset + it] == bytes[it].toByte() }

        return when {
            at(0, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A) -> "png"
            at(0, 0xFF, 0xD8, 0xFF) -> "jpg"
            at(0, 0x47, 0x49, 0x46, 0x38) -> "gif"
            at(0, 0x52, 0x49, 0x46, 0x46) && at(8, 0x57, 0x45, 0x42, 0x50) -> "webp"
            else -> null
        }
    }

    /**
     * Stores an uploaded slide image under a name chosen here, with the extension of its real format. Anything that is
     * not a png, jpeg, gif or webp image is refused.
     */
    fun store(fileUpload: FileUpload, uploadsDir: File): String {
        val source = File(fileUpload.uploadedFileName())
        val extension = (if (source.isFile) detectImageExtension(source) else null) ?: throw BadRequest()

        val fileName = "${System.currentTimeMillis()}-${UUID.randomUUID()}.$extension"
        val destFile = File(uploadsDir, fileName)

        source.copyTo(destFile, true)

        ImageUtil.generateThumbnail(destFile, thumbnailsDir(uploadsDir))

        return fileName
    }
}
