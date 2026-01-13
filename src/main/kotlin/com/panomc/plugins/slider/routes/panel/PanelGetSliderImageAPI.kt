package com.panomc.plugins.slider.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.PanelApi
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.platform.util.MimeTypeUtil
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.permission.ManageSliderPermission
import com.panomc.plugins.slider.util.ImageUtil
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters.optionalParam
import io.vertx.ext.web.validation.builder.Parameters.param
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.booleanSchema
import io.vertx.json.schema.common.dsl.Schemas.stringSchema
import java.io.File

@Endpoint
class PanelGetSliderImageAPI(
    private val plugin: SliderPlugin,
    private val sliderDao: SliderDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/slider/items/image/:fileName", RouteType.GET))

    private val authProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    companion object {
        private const val CACHE_TTL_SECONDS = 7 * 24 * 60 * 60 // 1 week
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("fileName", stringSchema()))
            .queryParameter(optionalParam("thumbnail", booleanSchema()))
            .build()

    override suspend fun handle(context: RoutingContext): Result? {
        // Optional: require permission or not? Announcements requires it.
        // authProvider.requirePermission(ManageSliderPermission(), context)

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
