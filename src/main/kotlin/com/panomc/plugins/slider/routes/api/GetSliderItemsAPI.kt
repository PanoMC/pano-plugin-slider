package com.panomc.plugins.slider.routes.api

import com.panomc.platform.annotation.Endpoint
import com.panomc.platform.db.DatabaseManager
import com.panomc.platform.model.Api
import com.panomc.platform.model.Path
import com.panomc.platform.model.Result
import com.panomc.platform.model.RouteType
import com.panomc.platform.model.Successful
import com.panomc.plugins.slider.SliderPlugin
import com.panomc.plugins.slider.db.dao.SliderDao
import com.panomc.plugins.slider.db.dao.SliderSettingsDao
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.validation.ValidationHandler
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder
import io.vertx.json.schema.SchemaRepository

@Endpoint
class GetSliderItemsAPI(
    private val plugin: SliderPlugin,
    private val sliderDao: SliderDao,
    private val sliderSettingsDao: SliderSettingsDao
) : Api() {
    override val paths = listOf(Path("/api/slider/items", RouteType.GET))

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
        val titleColor = sliderSettingsDao.getSetting("titleColor", "#ffffff", sqlClient)
        val subtitleColor = sliderSettingsDao.getSetting("subtitleColor", "#ffffff", sqlClient)
        val captionBackground = sliderSettingsDao.getSetting("captionBackground", "#000000", sqlClient)
        val captionOpacity = sliderSettingsDao.getSetting("captionOpacity", "0.5", sqlClient)
        val blurAmount = sliderSettingsDao.getSetting("blurAmount", "5", sqlClient)
        val captionStyle = sliderSettingsDao.getSetting("captionStyle", "solid", sqlClient)

        return Successful(
            mapOf(
                "sliderItems" to sliderItems,
                "settings" to mapOf(
                    "renderHook" to renderHook,
                    "homepageOnly" to (homepageOnly == "true"),
                    "autoSlide" to (autoSlide == "true"),
                    "interval" to interval.toInt(),
                    "pauseOnHover" to (pauseOnHover == "true"),
                    "wrap" to (wrap == "true"),
                    "indicators" to (indicators == "true"),
                    "controls" to (controls == "true"),
                    "fade" to (fade == "true"),
                    "titleColor" to titleColor,
                    "subtitleColor" to subtitleColor,
                    "captionBackground" to captionBackground,
                    "captionOpacity" to captionOpacity.toDouble(),
                    "blurAmount" to blurAmount.toInt(),
                    "captionStyle" to captionStyle
                )
            )
        )
    }
}
