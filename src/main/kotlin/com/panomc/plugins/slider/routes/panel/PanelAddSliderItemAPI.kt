package com.panomc.plugins.slider.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.error.BadRequest
import com.panomc.platform.model.*
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.db.model.SliderItem
import com.panomc.plugins.slider.log.CreatedSliderItemLog
import com.panomc.plugins.slider.permission.ManageSliderPermission
import com.panomc.plugins.slider.util.ImageUtil
import io.vertx.core.Handler
import io.vertx.ext.web.FileUpload
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.*
import java.io.File

@Endpoint
class PanelAddSliderItemAPI(
    private val plugin: SliderPlugin,
    private val sliderDao: SliderDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/slider/items", RouteType.POST))

    private val authProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun bodyHandler(): Handler<RoutingContext> =
        BodyHandler.create()
            .setDeleteUploadedFilesOnEnd(true)
            .setBodyLimit(5 * 1024 * 1024) // 5MB for slider images

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .body(
                io.vertx.ext.web.validation.builder.Bodies.multipartFormData(
                    objectSchema()
                        .optionalProperty("title", stringSchema())
                        .optionalProperty("subtitle", stringSchema())
                        .optionalProperty("linkUrl", stringSchema())
                        .optionalProperty("openInNewTab", booleanSchema())
                        .requiredProperty("itemOrder", numberSchema())
                        .requiredProperty("active", booleanSchema())
                )
            )
            .predicate(RequestPredicate.BODY_REQUIRED)
            .build()

    override suspend fun handle(context: RoutingContext): Result  {
        authProvider.requirePermission(ManageSliderPermission(), context)

        val parameters = getParameters(context)
        val data = parameters.body().jsonObject
        val fileUploads = context.fileUploads()

        val fileUpload = fileUploads.firstOrNull { it.name() == "image" }
        if (fileUpload == null) {
            throw BadRequest()
        }

        val title = data.getString("title") ?: ""

        val imageFileName = saveUploadedFile(fileUpload)
        val imageUrl = "/api/slider/items/image/$imageFileName"

        val sliderItem = SliderItem(
            title = title,
            subtitle = data.getString("subtitle"),
            imageUrl = imageUrl,
            imageFileName = imageFileName,
            linkUrl = data.getString("linkUrl") ?: "",
            openInNewTab = data.getBoolean("openInNewTab") ?: false,
            itemOrder = 1,
            active = data.getBoolean("active") ?: true
        )

        val sqlClient = databaseManager.getSqlClient()
        sliderDao.incrementAllOrders(sqlClient)
        val generatedId = sliderDao.add(sliderItem, sqlClient)

        val userId = authProvider.getUserIdFromRoutingContext(context)
        val username = databaseManager.userDao.getUsernameFromUserId(userId, sqlClient)!!

        val logTitle = if (title.isBlank()) "#$generatedId" else title

        databaseManager.panelActivityLogDao.add(
            CreatedSliderItemLog(userId, username, plugin.pluginId, generatedId, logTitle),
            sqlClient
        )

        return Successful()
    }

    private fun saveUploadedFile(fileUpload: FileUpload): String {
        val extension = fileUpload.fileName().split(".").last()
        val fileName = "${System.currentTimeMillis()}-${fileUpload.uploadedFileName().split(File.separator).last()}.$extension"
        val destFile = File(plugin.uploadsDir, fileName)

        File(fileUpload.uploadedFileName()).copyTo(destFile, true)

        ImageUtil.generateThumbnail(destFile, File(plugin.uploadsDir, "thumbnails"))

        return fileName
    }
}
