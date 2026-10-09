package com.panomc.plugins.slider.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.*
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.db.dao.SliderSettingsDao
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import com.panomc.platform.schema.dsl.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository
import com.panomc.platform.schema.EndpointDoc
import io.vertx.json.schema.common.dsl.Schemas.*

@Endpoint
class GetSliderItemsAPI(
    private val plugin: SliderPlugin,
    private val sliderDao: SliderDao,
    private val sliderSettingsDao: SliderSettingsDao
) : Api() {
    override val paths = listOf(Path("/items", RouteType.GET))

    override val doc = EndpointDoc(
        summary = "The active slides and the look of the slider.",
        tag = "slider",
        response = objectSchema()
            .requiredProperty(
                "items",
                arraySchema().items(
                    objectSchema()
                    .requiredProperty("id", intSchema())
                    .requiredProperty("title", stringSchema())
                    .requiredProperty("imageUrl", stringSchema())
                    .requiredProperty("active", booleanSchema())
                    .optionalProperty("subtitle", stringSchema().nullable())
                    .optionalProperty("imageFileName", stringSchema().nullable())
                    .optionalProperty("linkUrl", stringSchema().nullable())
                    .optionalProperty("openInNewTab", booleanSchema())
                    .optionalProperty("itemOrder", intSchema())
                    .optionalProperty("createdAt", intSchema())
                    .optionalProperty("updatedAt", intSchema())
                )
            )
            .requiredProperty(
                "settings",
                objectSchema()
                    .requiredProperty("renderHook", stringSchema())
                    .requiredProperty("homepageOnly", booleanSchema())
                    .requiredProperty("autoSlide", booleanSchema())
                    .requiredProperty("interval", intSchema())
                    .requiredProperty("pauseOnHover", booleanSchema())
                    .requiredProperty("wrap", booleanSchema())
                    .requiredProperty("indicators", booleanSchema())
                    .requiredProperty("controls", booleanSchema())
                    .requiredProperty("transition", stringSchema())
                    .requiredProperty("titleColor", stringSchema())
                    .requiredProperty("subtitleColor", stringSchema())
                    .requiredProperty("captionBackground", stringSchema())
                    .requiredProperty("captionOpacity", numberSchema())
                    .requiredProperty("blurAmount", intSchema())
                    .requiredProperty("captionStyle", stringSchema())
                    .requiredProperty("titleTag", stringSchema())
            )
    )

    private val databaseManager by lazy {
        plugin.applicationContext.getBean(DatabaseManager::class.java)
    }

    override fun getValidationHandler(schemaRepository: SchemaRepository): ValidationHandler =
        ValidationHandlerBuilder.create(schemaRepository)
            .build()

    override suspend fun handle(context: RoutingContext): Result {
        val sqlClient = databaseManager.getSqlClient()
        val sliderItems = sliderDao.getAllActive(sqlClient)
        val renderHook = sliderSettingsDao.getSetting("renderHook", "page:home:top", sqlClient)
        val homepageOnly = sliderSettingsDao.getSetting("homepageOnly", "true", sqlClient)
        val autoSlide = sliderSettingsDao.getSetting("autoSlide", "true", sqlClient)
        val interval = sliderSettingsDao.getSetting("interval", "5000", sqlClient)
        val pauseOnHover = sliderSettingsDao.getSetting("pauseOnHover", "true", sqlClient)
        val wrap = sliderSettingsDao.getSetting("wrap", "true", sqlClient)
        val indicators = sliderSettingsDao.getSetting("indicators", "true", sqlClient)
        val controls = sliderSettingsDao.getSetting("controls", "true", sqlClient)
        val fade = sliderSettingsDao.getSetting("fade", "true", sqlClient)
        val transition = sliderSettingsDao.getSetting("transition", if (fade == "true") "fade" else "slide", sqlClient)
        val titleColor = sliderSettingsDao.getSetting("titleColor", "#ffffff", sqlClient)
        val subtitleColor = sliderSettingsDao.getSetting("subtitleColor", "#ffffff", sqlClient)
        val captionBackground = sliderSettingsDao.getSetting("captionBackground", "#000000", sqlClient)
        val captionOpacity = sliderSettingsDao.getSetting("captionOpacity", "0.5", sqlClient)
        val blurAmount = sliderSettingsDao.getSetting("blurAmount", "5", sqlClient)
        val captionStyle = sliderSettingsDao.getSetting("captionStyle", "solid", sqlClient)
        val titleTag = sliderSettingsDao.getSetting("titleTag", "h2", sqlClient)

        return Successful(
            mapOf(
                "items" to sliderItems,
                "settings" to mapOf(
                    "renderHook" to renderHook,
                    "homepageOnly" to (homepageOnly == "true"),
                    "autoSlide" to (autoSlide == "true"),
                    "interval" to interval.toInt(),
                    "pauseOnHover" to (pauseOnHover == "true"),
                    "wrap" to (wrap == "true"),
                    "indicators" to (indicators == "true"),
                    "controls" to (controls == "true"),
                    "transition" to transition,
                    "titleColor" to titleColor,
                    "subtitleColor" to subtitleColor,
                    "captionBackground" to captionBackground,
                    "captionOpacity" to captionOpacity.toDouble(),
                    "blurAmount" to blurAmount.toInt(),
                    "captionStyle" to captionStyle,
                    "titleTag" to titleTag
                )
            )
        )
    }
}
