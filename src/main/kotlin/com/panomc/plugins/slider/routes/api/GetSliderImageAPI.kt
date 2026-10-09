package com.panomc.plugins.slider.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.model.Api
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.platform.util.MimeTypeUtil
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.util.ImageUtil
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import com.panomc.platform.schema.dsl.Parameters.optionalParam
import com.panomc.platform.schema.dsl.Parameters.param
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.booleanSchema
import io.vertx.json.schema.common.dsl.Schemas.stringSchema
import java.io.File
import com.panomc.platform.schema.EndpointDoc
import io.vertx.json.schema.common.dsl.Schemas.*
import com.panomc.platform.error.NotFound

@Endpoint
class GetSliderImageAPI(
    private val plugin: SliderPlugin
) : Api() {
    override val paths = listOf(Path("/items/image/:fileName", RouteType.GET))

    override val doc = EndpointDoc(
        summary = "A slide image, or its thumbnail with ?thumbnail=true.",
        tag = "slider",
        binary = true,
        errors = listOf(NotFound::class)
    )

    companion object {
        private const val CACHE_TTL_SECONDS = 7 * 24 * 60 * 60 // 1 week
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("fileName", stringSchema()))
            .queryParameter(optionalParam("thumbnail", booleanSchema()))
            .build()

    override suspend fun handle(context: RoutingContext): Result? {
        val parameters = getParameters(context)
        val fileName = parameters.pathParameter("fileName").string

        val isThumbnail = parameters.queryParameter("thumbnail")?.boolean ?: false
        val file = if (isThumbnail) {
            getThumbnailFile(fileName)
        } else {
            File(plugin.uploadsDir, fileName)
        }

        if (!file.exists()) {
            context.response().setStatusCode(404).end()
            return null
        }

        val etag = "\"$fileName\""
        val mimeType = MimeTypeUtil.getMimeTypeFromFileName(file.absolutePath)

        val response = context.response()
        response.putHeader("Content-Type", mimeType)
        response.putHeader("ETag", etag)
        response.putHeader("Cache-Control", "public, max-age=$CACHE_TTL_SECONDS, immutable")

        try {
            response.sendFile(file.absolutePath)
        } catch (_: Exception) {}

        return null
    }

    private fun getThumbnailFile(fileName: String): File {
        val thumbnailsDir = File(plugin.uploadsDir, "thumbnails")
        val originalFile = File(plugin.uploadsDir, fileName)
        
        if (!originalFile.exists()) return originalFile

        // Ensure thumbnail exists
        ImageUtil.generateThumbnail(originalFile, thumbnailsDir)

        val thumbnailFile = File(thumbnailsDir, fileName)
        return if (thumbnailFile.exists()) thumbnailFile else originalFile
    }
}
