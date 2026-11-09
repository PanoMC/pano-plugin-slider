package com.panomc.plugins.slider.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.PanelApi
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.permission.ManageSliderPermission
import com.panomc.plugins.slider.util.SliderImageFiles
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import com.panomc.platform.schema.dsl.Parameters.optionalParam
import com.panomc.platform.schema.dsl.Parameters.param
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.booleanSchema
import io.vertx.json.schema.common.dsl.Schemas.stringSchema

@Endpoint
class PanelGetSliderImageAPI(
    private val plugin: SliderPlugin,
    private val sliderDao: SliderDao
) : PanelApi() {
    override val paths = listOf(Path("/items/image/:fileName", RouteType.GET))

    private val authProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("fileName", stringSchema()))
            .queryParameter(optionalParam("thumbnail", booleanSchema()))
            .build()

    override suspend fun handle(context: RoutingContext): Result? {
        authProvider.requirePermission(ManageSliderPermission(), context)

        val parameters = getParameters(context)
        val fileName = parameters.pathParameter("fileName").string
        val isThumbnail = parameters.queryParameter("thumbnail")?.boolean ?: false

        SliderImageFiles.serve(context, plugin.uploadsDir, fileName, isThumbnail)

        return null
    }
}
