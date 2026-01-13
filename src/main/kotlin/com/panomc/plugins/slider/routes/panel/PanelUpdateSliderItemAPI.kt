package com.panomc.plugins.slider.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.error.PageNotFound
import com.panomc.platform.model.*
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.db.model.SliderItem
import com.panomc.plugins.slider.log.UpdatedSliderItemLog
import com.panomc.plugins.slider.permission.ManageSliderPermission
import com.panomc.plugins.slider.util.ImageUtil
import io.vertx.core.Handler
import io.vertx.ext.web.FileUpload
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.validation.RequestPredicate
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.Parameters.param
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.*
import java.io.File

@Endpoint
class PanelUpdateSliderItemAPI(
    private val plugin: SliderPlugin,
    private val sliderDao: SliderDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/slider/items/:id", RouteType.PUT))

    private val authProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun bodyHandler(): Handler<RoutingContext> =
        BodyHandler.create()
            .setDeleteUploadedFilesOnEnd(true)
            .setBodyLimit(5 * 1024 * 1024)

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("id", numberSchema()))
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

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageSliderPermission(), context)

        val parameters = getParameters(context)
        val id = parameters.pathParameter("id").long
        val data = parameters.body().jsonObject
        val fileUploads = context.fileUploads()

        val sqlClient = databaseManager.getSqlClient()
        val existingItem = sliderDao.getById(id, sqlClient) ?: throw PageNotFound()

        val fileUpload = fileUploads.firstOrNull { it.name() == "image" }
        
        var imageFileName = existingItem.imageFileName
        var imageUrl = existingItem.imageUrl

        if (fileUpload != null) {
            // Delete old file
            if (existingItem.imageFileName != null) {
                File(plugin.uploadsDir, existingItem.imageFileName).delete()
                File(File(plugin.uploadsDir, "thumbnails"), existingItem.imageFileName).delete()
            }
            imageFileName = saveUploadedFile(fileUpload)
            imageUrl = "/api/slider/items/image/$imageFileName"
        }

        val updatedItem = SliderItem(
            id = id,
            title = data.getString("title") ?: "",
            subtitle = data.getString("subtitle"),
            imageUrl = imageUrl,
            imageFileName = imageFileName,
            linkUrl = data.getString("linkUrl") ?: "",
            openInNewTab = data.getBoolean("openInNewTab") ?: existingItem.openInNewTab,
            itemOrder = data.getInteger("itemOrder") ?: existingItem.itemOrder,
            active = data.getBoolean("active") ?: existingItem.active,
            createdAt = existingItem.createdAt,
            updatedAt = System.currentTimeMillis()
        )

        val changes = io.vertx.core.json.JsonObject()
        if (existingItem.title != updatedItem.title) {
            changes.put("title", io.vertx.core.json.JsonObject().put("old", existingItem.title).put("new", updatedItem.title))
        }
        if (existingItem.subtitle != updatedItem.subtitle) {
            changes.put("subtitle", io.vertx.core.json.JsonObject().put("old", existingItem.subtitle).put("new", updatedItem.subtitle))
        }
        if (existingItem.imageUrl != updatedItem.imageUrl) {
            changes.put("imageUrl", io.vertx.core.json.JsonObject().put("old", existingItem.imageUrl).put("new", updatedItem.imageUrl))
        }
        if (existingItem.linkUrl != updatedItem.linkUrl) {
            changes.put("linkUrl", io.vertx.core.json.JsonObject().put("old", existingItem.linkUrl).put("new", updatedItem.linkUrl))
        }
        if (existingItem.openInNewTab != updatedItem.openInNewTab) {
            changes.put("openInNewTab", io.vertx.core.json.JsonObject().put("old", existingItem.openInNewTab).put("new", updatedItem.openInNewTab))
        }
        if (existingItem.itemOrder != updatedItem.itemOrder) {
            changes.put("itemOrder", io.vertx.core.json.JsonObject().put("old", existingItem.itemOrder).put("new", updatedItem.itemOrder))
        }
        if (existingItem.active != updatedItem.active) {
            changes.put("active", io.vertx.core.json.JsonObject().put("old", existingItem.active).put("new", updatedItem.active))
        }

        sliderDao.update(updatedItem, sqlClient)

        val userId = authProvider.getUserIdFromRoutingContext(context)
        val username = databaseManager.userDao.getUsernameFromUserId(userId, sqlClient)!!

        val logTitle = if (updatedItem.title.isBlank()) "#${updatedItem.id}" else updatedItem.title

        databaseManager.panelActivityLogDao.add(
            UpdatedSliderItemLog(userId, username, plugin.pluginId, updatedItem.id, logTitle, changes),
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
