package com.panomc.plugins.slider.routes.panel

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.auth.AuthProvider
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.PanelApi
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.platform.model.Successful
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.permission.ManageSliderPermission
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.arraySchema
import io.vertx.json.schema.common.dsl.Schemas.numberSchema
import io.vertx.json.schema.common.dsl.Schemas.objectSchema

@Endpoint
class PanelReorderSliderItemsAPI(
    private val plugin: SliderPlugin,
    private val sliderDao: SliderDao
) : PanelApi() {
    override val paths = listOf(Path("/api/panel/slider/items/reorder", RouteType.POST))

    private val authProvider by lazy {
        plugin.applicationContext.getBean(AuthProvider::class.java)
    }

    private val databaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .body(
                io.vertx.ext.web.validation.builder.Bodies.json(
                    objectSchema()
                        .requiredProperty("ids", arraySchema().items(numberSchema()))
                )
            )
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        authProvider.requirePermission(ManageSliderPermission(), context)

        val body = context.body().asJsonObject()
        val ids = body.getJsonArray("ids").map { it.toString().toDouble().toLong() }

        val sqlClient = databaseManager.getSqlClient()
        
        ids.forEachIndexed { index, id ->
            sliderDao.updateOrder(id, index + 1, sqlClient)
        }

        return Successful()
    }
}
