package com.panomc.plugins.slider.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.model.Api
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.util.SliderImageFiles
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import com.panomc.platform.schema.dsl.Parameters.optionalParam
import com.panomc.platform.schema.dsl.Parameters.param
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import io.vertx.json.schema.common.dsl.Schemas.booleanSchema
import io.vertx.json.schema.common.dsl.Schemas.stringSchema
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

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .pathParameter(param("fileName", stringSchema()))
            .queryParameter(optionalParam("thumbnail", booleanSchema()))
            .build()

    override suspend fun handle(context: RoutingContext): Result? {
        val parameters = getParameters(context)
        val fileName = parameters.pathParameter("fileName").string
        val isThumbnail = parameters.queryParameter("thumbnail")?.boolean ?: false

        SliderImageFiles.serve(context, plugin.uploadsDir, fileName, isThumbnail)

        return null
    }
}
